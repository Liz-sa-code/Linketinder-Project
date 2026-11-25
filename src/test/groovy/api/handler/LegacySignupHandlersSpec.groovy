package api.handler

import controller.CandidatoController
import controller.EmpresaController
import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import service.CandidatoService
import service.EmpresaService
import spock.lang.Specification

class LegacySignupHandlersSpec extends Specification {

    def "legacy candidate registration rejects a missing password"() {
        given:
        CandidatoInterfaceRepository repository = Mock()
        CandidatoController controller = new CandidatoController(
                new CandidatoService(repository)
        )

        when:
        new CandidatoHandler(controller).executar([
                dados: [nome: 'Teste'],
                competencias: []
        ])

        then:
        thrown(IllegalArgumentException)
        0 * repository._
    }

    def "legacy company registration rejects a missing password"() {
        given:
        EmpresaRepositoryInterface repository = Mock()
        EmpresaController controller = new EmpresaController(
                new EmpresaService(repository)
        )

        when:
        new EmpresaHandler(controller).executar([nome: 'Teste'])

        then:
        thrown(IllegalArgumentException)
        0 * repository._
    }
}
