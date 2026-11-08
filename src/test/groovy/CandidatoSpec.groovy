
import spock.lang.Specification

class CandidatoSpec extends Specification {

    def "deve criar um candidato corretamente"() {
        given:
        def competencias = [
                new Competencia("Java"),
                new Competencia("Groovy"),
                new Competencia("Python")
        ]

        when:
        def candidato = new Candidato(
                "Carlos Mendes",
                "carlos.mendes@email.com",
                "987.654.321-00",
                "SP",
                "01000-000",
                "Desenvolvedor de software",
                25,
                competencias
        )

        then:
        candidato.nome == "Carlos Mendes"
        candidato.email == "carlos.mendes@email.com"
        candidato.documento == "987.654.321-00"
        candidato.idade == 25
        candidato.estado == "SP"
        candidato.cep == "01000-000"
        candidato.descricao == "Desenvolvedor de software"
        candidato.competencias*.nome == ["Java", "Groovy", "Python"]
    }
}
