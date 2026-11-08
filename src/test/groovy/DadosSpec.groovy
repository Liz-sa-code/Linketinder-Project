
import spock.lang.Specification

class DadosSpec extends Specification {

    def setup() {
        Dados.candidatos = []
        Dados.empresas = []
    }

    def cleanup() {
        Dados.candidatos = []
        Dados.empresas = []
    }

    def "deve inserir um novo candidato na lista"() {
        given:
        def candidato = new Candidato(
                "João Pereira",
                "joao.pereira@email.com",
                "321.654.987-00",
                "MG",
                "30000-000",
                "Analista de sistemas",
                27,
                [
                        new Competencia("Java"),
                        new Competencia("SQL"),
                        new Competencia("Git")
                ]
        )

        when:
        Dados.adicionarCandidato(candidato)

        then:
        Dados.candidatos.size() == 1
        Dados.candidatos.contains(candidato)
    }

    def "deve inserir uma nova empresa na lista"() {
        given:
        def empresa = new Empresa(
                "CodeSolutions",
                "contato@codesolutions.com",
                "76.543.210/0001-98",
                "PR",
                "80000-000",
                "Empresa especializada em soluções tecnológicas",
                "Brasil",
                [
                        new Competencia("Java"),
                        new Competencia("Groovy"),
                        new Competencia("Spring")
                ]
        )

        when:
        Dados.adicionarEmpresa(empresa)

        then:
        Dados.empresas.size() == 1
        Dados.empresas.contains(empresa)
    }

    def "deve permitir inserir mais de um candidato"() {
        given:
        def candidato1 = new Candidato(
                "Mariana Costa",
                "mariana.costa@email.com",
                "456.789.123-00",
                "BA",
                "40000-000",
                "Desenvolvedora web",
                23,
                [
                        new Competencia("JavaScript"),
                        new Competencia("HTML"),
                        new Competencia("CSS")
                ]
        )

        def candidato2 = new Candidato(
                "Rafael Almeida",
                "rafael.almeida@email.com",
                "654.321.987-00",
                "PE",
                "50000-000",
                "Engenheiro de software",
                29,
                [
                        new Competencia("Java"),
                        new Competencia("Spring"),
                        new Competencia("Docker")
                ]
        )

        when:
        Dados.adicionarCandidato(candidato1)
        Dados.adicionarCandidato(candidato2)

        then:
        Dados.candidatos.size() == 2
        Dados.candidatos[0] == candidato1
        Dados.candidatos[1] == candidato2
    }

    def "deve permitir inserir mais de uma empresa"() {
        given:
        def empresa1 = new Empresa(
                "DevMaster",
                "contato@devmaster.com",
                "34.567.890/0001-12",
                "Brasil",
                "SC",
                "88000-000",
                "Empresa de desenvolvimento de software",
                [
                        new Competencia("Java"),
                        new Competencia("Python")
                ]
        )

        def empresa2 = new Empresa(
                "TechWorld",
                "contato@techworld.com",
                "45.678.901/0001-23",
                "Brasil",
                "RS",
                "90000-000",
                "Empresa de tecnologia e inovação",
                [
                        new Competencia("Groovy"),
                        new Competencia("JavaScript")
                ]
        )

        when:
        Dados.adicionarEmpresa(empresa1)
        Dados.adicionarEmpresa(empresa2)

        then:
        Dados.empresas.size() == 2
        Dados.empresas[0] == empresa1
        Dados.empresas[1] == empresa2
    }
}
