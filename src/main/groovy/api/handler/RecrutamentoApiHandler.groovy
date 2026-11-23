package api.handler

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import controller.CandidatoController
import controller.EmpresaController
import controller.RecrutadorController
import controller.VagaController
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.json.JsonException
import security.AuthPrincipal
import service.AuthService

import java.nio.charset.StandardCharsets
import java.sql.SQLException

class RecrutamentoApiHandler implements HttpHandler {

    private static final int LIMITE_CORPO = 65_536
    private static final Set<String> ORIGENS_PERMITIDAS = [
            'http://localhost:5173',
            'http://127.0.0.1:5173'
    ] as Set

    private final AuthService authService
    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final RecrutadorController recrutadorController
    private final VagaController vagaController

    RecrutamentoApiHandler(
            AuthService authService,
            CandidatoController candidatoController,
            EmpresaController empresaController,
            RecrutadorController recrutadorController,
            VagaController vagaController
    ) {
        this.authService = Objects.requireNonNull(authService)
        this.candidatoController = Objects.requireNonNull(candidatoController)
        this.empresaController = Objects.requireNonNull(empresaController)
        this.recrutadorController = Objects.requireNonNull(recrutadorController)
        this.vagaController = Objects.requireNonNull(vagaController)
    }

    @Override
    void handle(HttpExchange exchange) {
        try {
            configurarCabecalhos(exchange)
            if (exchange.requestMethod == 'OPTIONS') {
                responder(exchange, 204, null)
                return
            }

            Map body = [:]
            if (exchange.requestMethod in ['POST', 'DELETE']) {
                body = lerBody(exchange)
            } else if (exchange.requestMethod != 'GET') {
                exchange.responseHeaders.set(
                        'Allow',
                        'GET, POST, DELETE, OPTIONS'
                )
                responder(exchange, 405, [erro: 'Método não permitido.'])
                return
            }

            def resultado = rotear(exchange, body)
            int status = exchange.requestMethod == 'POST' &&
                    !exchange.requestURI.path.endsWith('/auth/login')
                    ? 201
                    : 200
            responder(exchange, status, resultado as Map)
        } catch (IllegalArgumentException e) {
            responder(exchange, 400, [erro: e.message])
        } catch (RouteNotFoundException e) {
            responder(exchange, 404, [erro: 'Rota não encontrada.'])
        } catch (UnsupportedMediaTypeException e) {
            responder(exchange, 415, [erro: 'Use Content-Type application/json.'])
        } catch (RequestTooLargeException e) {
            responder(exchange, 413, [erro: 'Corpo da requisição muito grande.'])
        } catch (JsonException e) {
            responder(exchange, 400, [erro: 'JSON inválido.'])
        } catch (SecurityException e) {
            responder(exchange, 401, [erro: 'Autenticação ou autorização inválida.'])
        } catch (Exception e) {
            if (sqlState(e) == '23505') {
                responder(exchange, 409, [erro: 'Registro já existente.'])
            } else {
                responder(exchange, 500, [erro: 'Erro interno do servidor.'])
            }
        } finally {
            exchange.close()
        }
    }

    private Map rotear(HttpExchange exchange, Map body) {
        String path = exchange.requestURI.path
        String method = exchange.requestMethod

        if (method == 'POST' && path == '/api/auth/login') {
            String tipo = texto(body.tipo, 'O tipo de conta')
            String email = texto(body.email, 'O e-mail')
            String senha = texto(body.senha, 'A senha')
            return authService.entrar(tipo, email, senha)
        }

        if (method == 'POST' && path == '/api/candidatos') {
            Map dados = objeto(body.dados, 'Os dados do candidato')
            exigirSenha(dados)
            List<String> skills = listaTextos(body.competencias, 'competências')
            Long id = candidatoController.cadastrar(dados, skills)
            return [mensagem: 'Candidato cadastrado com sucesso.', id: id]
        }

        if (method == 'POST' && path == '/api/empresas') {
            Map dados = body.empresa instanceof Map
                    ? body.empresa as Map
                    : body
            exigirSenha(dados)
            Long id = empresaController.cadastrar(dados)
            return [mensagem: 'Empresa cadastrada com sucesso.', id: id]
        }

        if (method == 'GET' && path == '/api/vagas') {
            return [vagas: vagaController.listarAtivasAnonimas()]
        }

        if (method == 'POST' && path == '/api/vagas') {
            AuthPrincipal principal = autenticar(exchange)
            exigirTipo(principal, 'empresa')
            Map vaga = objeto(body.vaga, 'Os dados da vaga')
            List<String> skills = listaTextos(body.competencias, 'competências')
            vaga = new LinkedHashMap(vaga)
            vaga.empresa_id = principal.empresaId
            Long id = vagaController.cadastrar(vaga, skills)
            return [mensagem: 'Vaga cadastrada com sucesso.', id: id]
        }

        if (method == 'GET' && path == '/api/empresa/vagas') {
            AuthPrincipal principal = autenticar(exchange)
            exigirTipo(principal, 'empresa')
            return [vagas: vagaController.listarPorEmpresa(principal.empresaId)]
        }

        if (method == 'GET' && path == '/api/empresa/recrutadores') {
            AuthPrincipal principal = autenticar(exchange)
            return [
                    recrutadores: recrutadorController.listarDaEmpresa(
                            principal
                    )
            ]
        }

        if (method == 'POST' && path == '/api/recrutadores') {
            AuthPrincipal principal = autenticar(exchange)
            Map dados = objeto(body.recrutador, 'Os dados do Recrutador')
            Long id = recrutadorController.cadastrar(dados, principal)
            return [mensagem: 'Recrutador cadastrado com sucesso.', id: id]
        }

        if (method == 'POST' && path == '/api/permissoes') {
            AuthPrincipal principal = autenticar(exchange)
            boolean concedida = recrutadorController.concederPermissao(
                    numero(body.recrutadorId, 'recrutadorId'),
                    numero(body.vagaId, 'vagaId'),
                    principal
            )
            if (!concedida) {
                throw new IllegalArgumentException(
                        'Recrutador e vaga devem pertencer à empresa autenticada.'
                )
            }
            return [mensagem: 'Permissão concedida.']
        }

        if (method == 'DELETE' && path == '/api/permissoes') {
            AuthPrincipal principal = autenticar(exchange)
            boolean removida = recrutadorController.revogarPermissao(
                    numero(body.recrutadorId, 'recrutadorId'),
                    numero(body.vagaId, 'vagaId'),
                    principal
            )
            return [removida: removida]
        }

        if (method == 'GET' && path == '/api/recrutadores/vagas') {
            return [
                    vagas: recrutadorController.listarVagasAutorizadas(
                            autenticar(exchange)
                    )
            ]
        }

        if (method == 'GET' && path == '/api/recrutadores/candidatos') {
            AuthPrincipal principal = autenticar(exchange)
            String token = token(exchange)
            return [
                    candidatos: recrutadorController.listarCandidatosAnonimos(
                            principal,
                            token
                    )
            ]
        }

        if (method == 'POST' && path == '/api/candidatos/interesses') {
            AuthPrincipal principal = autenticar(exchange)
            exigirTipo(principal, 'candidato')
            candidatoController.registrarInteresse(
                    principal.id,
                    numero(body.vagaId, 'vagaId')
            )
            return [mensagem: 'Interesse registrado.']
        }

        if (method == 'POST' && path == '/api/recrutadores/interesses') {
            AuthPrincipal principal = autenticar(exchange)
            boolean matchCriado = recrutadorController.registrarInteresse(
                    principal,
                    token(exchange),
                    texto(body.referencia, 'A referência do perfil'),
                    numero(body.vagaId, 'vagaId')
            )
            return [match: matchCriado]
        }

        if (method == 'GET' && path == '/api/matches') {
            AuthPrincipal principal = autenticar(exchange)
            return [matches: recrutadorController.listarMatches(principal)]
        }

        throw new RouteNotFoundException()
    }

    private static Map lerBody(HttpExchange exchange) {
        String contentType = exchange.requestHeaders.getFirst('Content-Type')
        if (contentType == null ||
                !contentType.toLowerCase(Locale.ROOT)
                        .startsWith('application/json')) {
            throw new UnsupportedMediaTypeException()
        }

        byte[] bytes = exchange.requestBody.readNBytes(LIMITE_CORPO + 1)
        if (bytes.length > LIMITE_CORPO) {
            throw new RequestTooLargeException()
        }

        def parsed = new JsonSlurper().parseText(
                new String(bytes, StandardCharsets.UTF_8)
        )
        return objeto(parsed, 'O corpo da requisição')
    }

    private AuthPrincipal autenticar(HttpExchange exchange) {
        return authService.autenticar(token(exchange))
    }

    private static String token(HttpExchange exchange) {
        String header = exchange.requestHeaders.getFirst('Authorization')
        if (!header?.startsWith('Bearer ')) {
            throw new SecurityException('Autenticação obrigatória.')
        }
        return header.substring('Bearer '.length())
    }

    private static void exigirTipo(AuthPrincipal principal, String tipo) {
        if (principal?.tipo != tipo) {
            throw new SecurityException('Acesso não autorizado.')
        }
    }

    private static void exigirSenha(Map dados) {
        String senha = texto(dados.senha, 'A senha')
        if (senha.length() < 8 || senha.length() > 256) {
            throw new IllegalArgumentException(
                    'A senha deve ter entre 8 e 256 caracteres.'
            )
        }
    }

    private static Map objeto(def value, String campo) {
        if (!(value instanceof Map) || value.isEmpty()) {
            throw new IllegalArgumentException("${campo} devem ser informados.")
        }
        return value as Map
    }

    private static String texto(def value, String campo) {
        if (!(value instanceof String) || value.trim().isEmpty()) {
            throw new IllegalArgumentException("${campo} deve ser informado.")
        }
        return value.trim()
    }

    private static Long numero(def value, String campo) {
        try {
            Long result = value instanceof Number
                    ? (value as Number).longValue()
                    : Long.valueOf(value.toString())
            if (result <= 0) {
                throw new NumberFormatException()
            }
            return result
        } catch (Exception ignored) {
            throw new IllegalArgumentException("${campo} inválido.")
        }
    }

    private static List<String> listaTextos(def value, String campo) {
        if (value == null) {
            return []
        }
        if (!(value instanceof List) ||
                !value.every { it instanceof String && !it.trim().isEmpty() }) {
            throw new IllegalArgumentException(
                    "A lista de ${campo} é inválida."
            )
        }
        return value.collect { it.trim() }.unique()
    }

    private static void configurarCabecalhos(HttpExchange exchange) {
        String origin = exchange.requestHeaders.getFirst('Origin')
        if (ORIGENS_PERMITIDAS.contains(origin)) {
            exchange.responseHeaders.set('Access-Control-Allow-Origin', origin)
            exchange.responseHeaders.set(
                    'Access-Control-Allow-Headers',
                    'Authorization, Content-Type'
            )
            exchange.responseHeaders.set(
                    'Access-Control-Allow-Methods',
                    'GET, POST, DELETE, OPTIONS'
            )
            exchange.responseHeaders.set('Vary', 'Origin')
        }
        exchange.responseHeaders.set('Cache-Control', 'no-store')
        exchange.responseHeaders.set('X-Content-Type-Options', 'nosniff')
    }

    private static String sqlState(Throwable error) {
        Throwable atual = error
        while (atual != null) {
            if (atual instanceof SQLException) {
                return (atual as SQLException).sqlState
            }
            atual = atual.cause
        }
        return null
    }

    private static void responder(
            HttpExchange exchange,
            int status,
            Map resposta
    ) {
        if (status == 404 && resposta == null) {
            exchange.sendResponseHeaders(status, -1)
            return
        }
        if (status == 204) {
            exchange.sendResponseHeaders(status, -1)
            return
        }
        byte[] bytes = JsonOutput.toJson(resposta ?: [:])
                .getBytes(StandardCharsets.UTF_8)
        exchange.responseHeaders.set(
                'Content-Type',
                'application/json; charset=UTF-8'
        )
        exchange.sendResponseHeaders(status, bytes.length)
        exchange.responseBody.write(bytes)
        exchange.responseBody.close()
    }

    private static class RouteNotFoundException extends RuntimeException {
    }

    private static class UnsupportedMediaTypeException extends RuntimeException {
    }

    private static class RequestTooLargeException extends RuntimeException {
    }
}
