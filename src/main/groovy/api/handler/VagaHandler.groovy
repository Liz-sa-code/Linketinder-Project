
package api.handler

import controller.VagaController
import security.AuthPrincipal
import service.AuthService
import com.sun.net.httpserver.HttpExchange

class VagaHandler extends JsonRequestHandler {

    private final VagaController vagaController
    private final AuthService authService

    VagaHandler(VagaController vagaController, AuthService authService) {
        this.vagaController = Objects.requireNonNull(
                vagaController
        )
        this.authService = Objects.requireNonNull(authService)
    }

    @Override
    protected Map executar(Map dados) {
        throw new SecurityException('Autenticação obrigatória.')
    }

    @Override
    protected Map executar(HttpExchange exchange, Map dados) {
        String header = exchange.requestHeaders.getFirst('Authorization')
        String token = header?.startsWith('Bearer ')
                ? header.substring('Bearer '.length())
                : null
        AuthPrincipal principal = authService.autenticar(token)

        if (principal.tipo != 'empresa') {
            throw new SecurityException('Acesso não autorizado.')
        }

        if (!(dados.vaga instanceof Map) || dados.vaga.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da vaga devem ser informados.'
            )
        }

        def competencias = dados.competencias ?: []

        if (!(competencias instanceof List)) {
            throw new IllegalArgumentException(
                    'As competências devem ser uma lista.'
            )
        }

        if (!competencias.every {
            it instanceof String && !it.trim().isEmpty()
        }) {
            throw new IllegalArgumentException(
                    'Todas as competências devem ser textos não vazios.'
            )
        }

        Map vaga = new LinkedHashMap(dados.vaga as Map)
        vaga.empresa_id = principal.empresaId

        Long id = vagaController.cadastrar(
                vaga,
                competencias.collect { it.trim() }
        )

        if (id == null || id <= 0) {
            throw new IllegalStateException(
                    'Não foi possível cadastrar a vaga.'
            )
        }

        return [
                mensagem: 'Vaga cadastrada com sucesso.',
                id: id
        ]
    }
}
