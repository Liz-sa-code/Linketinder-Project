
import spock.lang.Specification

import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import repository.VagaRepositoryInterface
import repository.CompetenciaRepositoryInterface

class BancoDeDadosSpec extends Specification {

    def candidatoRepository
    def empresaRepository
    def vagaRepository
    def competenciaRepository

    BancoDeDados banco

    def setup() {
        candidatoRepository = Mock(CandidatoInterfaceRepository)
        empresaRepository = Mock(EmpresaRepositoryInterface)
        vagaRepository = Mock(VagaRepositoryInterface)
        competenciaRepository = Mock(CompetenciaRepositoryInterface)

        banco = new BancoDeDados(
                candidatoRepository,
                empresaRepository,
                vagaRepository,
                competenciaRepository
        )
    }

    def "deve listar candidatos"() {
        when:
        def resultado = banco.listarCandidatos()

        then:
        1 * candidatoRepository.listarCandidatos() >> []
        resultado instanceof List
        resultado.isEmpty()
    }

    def "deve listar empresas cadastradas"() {
        when:
        def resultado = banco.listarEmpresas()

        then:
        1 * empresaRepository.listar() >> []
        resultado instanceof List
        resultado.isEmpty()
    }

    def "deve listar competências cadastradas"() {
        when:
        def resultado = banco.listarCompetencias()

        then:
        1 * competenciaRepository.listar() >> []
        resultado instanceof List
        resultado.isEmpty()
    }

    def "deve retornar null ao buscar candidato inexistente"() {
        when:
        def resultado = banco.buscarCandidato(-1L)

        then:
        1 * candidatoRepository.buscarPorId(-1L) >> null
        resultado == null
    }

    def "deve retornar null ao buscar empresa inexistente"() {
        when:
        def resultado = banco.buscarEmpresa(-1L)

        then:
        1 * empresaRepository.buscarPorId(-1L) >> null
        resultado == null
    }

    def "deve encaminhar a criação de candidato ao repositório"() {
        given:
        Map dados = [
                nome: "Teste",
                sobrenome: "Automatizado",
                data_nascimento: "2004-09-29",
                email: "teste@example.com",
                cpf: "00000000000",
                pais: "Brasil",
                cep: "00000-000",
                descricao: "Candidato de teste",
                senha_hash: "hash-de-teste"
        ]

        when:
        def resultado = banco.criarCandidato(dados, [])

        then:
        1 * candidatoRepository.criar(dados, []) >> 1L
        resultado == 1L
    }
}
