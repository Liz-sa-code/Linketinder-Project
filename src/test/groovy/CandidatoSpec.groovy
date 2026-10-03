import spock.lang.Specification

class CandidatoSpec extends Specification {

    def "deve criar um candidato corretamente"() {

        given:
        def candidato = new Candidato(
                "Carlos Mendes",
                "carlos.mendes@email.com",
                "987.654.321-00",
                25,
                "SP",
                "01000-000",
                "Desenvolvedor de software",
                ["Java", "Groovy", "Python"]
        )

        expect:
        candidato.nome == "Carlos Mendes"
        candidato.email == "carlos.mendes@email.com"
        candidato.documento == "987.654.321-00"
        candidato.idade == 25
        candidato.estado == "SP"
        candidato.cep == "01000-000"
        candidato.descricao == "Desenvolvedor de software"
        candidato.competencias == ["Java", "Groovy", "Python"]
    }
}
