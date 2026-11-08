
import spock.lang.Specification
import model.Empresa
import model.Competencia
import spock.lang.Specification

class EmpresaSpec extends Specification {

    def "deve criar uma empresa corretamente"() {

        given:
        def competencias = [
                new Competencia("Java"),
                new Competencia("Python"),
                new Competencia("SQL")
        ]

        def empresa = new Empresa(
                "InovaTech",
                "contato@inovatech.com",
                "98.765.432/0001-10",
                "RJ",
                "20000-000",
                "Empresa especializada em desenvolvimento de sistemas",
                "Brasil",
                competencias
        )

        expect:
        empresa.nome == "InovaTech"
        empresa.email == "contato@inovatech.com"
        empresa.documento == "98.765.432/0001-10"
        empresa.pais == "Brasil"
        empresa.estado == "RJ"
        empresa.cep == "20000-000"
        empresa.descricao == "Empresa especializada em desenvolvimento de sistemas"
        empresa.competencias*.nome == ["Java", "Python", "SQL"]
    }
}
