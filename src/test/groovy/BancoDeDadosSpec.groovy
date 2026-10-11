
import spock.lang.Specification

class BancoDeDadosSpec extends Specification {

    def "deve conectar ao banco e listar candidatos"() {
        given:
        def banco = new BancoDeDados()

        when:
        def candidatos = banco.listarCandidatos()

        then:
        candidatos != null
        candidatos instanceof List
    }

    def "deve listar empresas cadastradas"() {
        given:
        def banco = new BancoDeDados()

        when:
        def empresas = banco.listarEmpresas()

        then:
        empresas != null
        empresas instanceof List
    }

    def "deve listar competências cadastradas"() {
        given:
        def banco = new BancoDeDados()

        when:
        def competencias = banco.listarCompetencias()

        then:
        competencias != null
        competencias instanceof List
    }

    def "deve retornar null ao buscar candidato inexistente"() {
        given:
        def banco = new BancoDeDados()

        when:
        def candidato = banco.buscarCandidato(-1L)

        then:
        candidato == null
    }

    def "deve retornar null ao buscar empresa inexistente"() {
        given:
        def banco = new BancoDeDados()

        when:
        def empresa = banco.buscarEmpresa(-1L)

        then:
        empresa == null
    }

    def "deve rejeitar data de nascimento inválida"() {
        given:
        def banco = new BancoDeDados()

        when:
        banco.criarCandidato([
                nome: "Teste",
                sobrenome: "Automatizado",
                data_nascimento: "29/09/2004",
                email: "teste-invalido@example.com",
                cpf: "00000000000",
                pais: "Brasil",
                cep: "00000-000",
                descricao: "Candidato de teste",
                senha_hash: "hash-de-teste"
        ], [])

        then:
        thrown(IllegalArgumentException)
    }
}
