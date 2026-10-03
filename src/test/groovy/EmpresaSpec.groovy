import spock.lang.Specification

class EmpresaSpec extends Specification {

    def "deve criar uma empresa corretamente"() {

        given:
        def empresa = new Empresa(
                "InovaTech",
                "contato@inovatech.com",
                "98.765.432/0001-10",
                "Brasil",
                "RJ",
                "20000-000",
                "Empresa especializada em desenvolvimento de sistemas",
                ["Java", "Python", "SQL"]
        )

        expect:
        empresa.nome == "InovaTech"
        empresa.email == "contato@inovatech.com"
        empresa.documento == "98.765.432/0001-10"
        empresa.pais == "Brasil"
        empresa.estado == "RJ"
        empresa.cep == "20000-000"
        empresa.descricao == "Empresa especializada em desenvolvimento de sistemas"
        empresa.competencias == ["Java", "Python", "SQL"]
    }
}

