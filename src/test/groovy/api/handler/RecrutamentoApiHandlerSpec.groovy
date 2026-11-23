package api.handler

import com.sun.net.httpserver.HttpServer
import controller.CandidatoController
import controller.EmpresaController
import controller.RecrutadorController
import controller.VagaController
import database.ConnectionProvider
import groovy.json.JsonSlurper
import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import repository.MatchRepository
import repository.RecrutadorRepositoryInterface
import repository.VagaRepositoryInterface
import security.AuthPrincipal
import security.AuthSessionStore
import service.AuthService
import service.CandidatoService
import service.EmpresaService
import service.RecrutadorService
import service.VagaService
import spock.lang.Specification

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class RecrutamentoApiHandlerSpec extends Specification {

    def "anonymous candidate profile response excludes identity fields"() {
        given:
        CandidatoInterfaceRepository candidatos = Mock()
        EmpresaRepositoryInterface empresas = Mock()
        RecrutadorRepositoryInterface recrutadores = Mock()
        VagaRepositoryInterface vagas = Mock()
        MatchRepository matches = new MatchRepository(Mock(ConnectionProvider))
        AuthSessionStore sessoes = new AuthSessionStore()
        String token = sessoes.criar(
                new AuthPrincipal('recrutador', 12L, 3L)
        )
        AuthService auth = new AuthService(
                candidatos,
                empresas,
                recrutadores,
                sessoes
        )
        RecrutadorService recrutadorService = new RecrutadorService(
                recrutadores,
                candidatos,
                vagas,
                matches,
                auth
        )
        RecrutamentoApiHandler handler = new RecrutamentoApiHandler(
                auth,
                new CandidatoController(new CandidatoService(candidatos)),
                new EmpresaController(new EmpresaService(empresas)),
                new RecrutadorController(recrutadorService),
                new VagaController(new VagaService(vagas))
        )
        HttpServer server = HttpServer.create(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 0),
                0
        )
        server.createContext('/api', handler)

        when:
        server.start()
        HttpResponse<String> resposta
        try {
            resposta = HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "http://127.0.0.1:${server.address.port}" +
                                            '/api/recrutadores/candidatos'
                            ))
                            .header('Authorization', "Bearer ${token}")
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            )
        } finally {
            server.stop(0)
        }

        then:
        1 * candidatos.listarPerfisAnonimosPorEmpresa(3L) >> [[
                candidatoId: 99L,
                nome: 'Nome privado',
                email: 'privado@example.com',
                cpf: '000.000.000-00',
                dataNascimento: '1990-01-01',
                cep: '00000-000',
                descricao: 'Perfil profissional',
                competencias: ['Groovy'],
                formacoes: ['Formação fictícia']
        ]]
        resposta.statusCode() == 200
        Map payload = new JsonSlurper().parseText(resposta.body()) as Map
        payload.candidatos.first().keySet() as Set ==
                ['referencia', 'descricao', 'competencias', 'formacoes'] as Set
        !resposta.body().contains('Nome privado')
        !resposta.body().contains('privado@example.com')
        !resposta.body().contains('000.000.000-00')
        !resposta.body().contains('00000-000')
    }
}
