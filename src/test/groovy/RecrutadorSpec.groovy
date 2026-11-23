import model.Recrutador
import spock.lang.Specification

import java.time.LocalDate

class RecrutadorSpec extends Specification {

    def "deve vincular o recrutador à empresa e armazenar apenas o hash da senha"() {
        given:
        String senhaHash = '$pbkdf2-sha256$teste'

        when:
        def recrutador = new Recrutador(
                7L,
                "Joana Lima",
                "Tecnologia",
                "Recrutamento de profissionais de tecnologia",
                "joana@example.com",
                "123.456.789-00",
                LocalDate.parse("1990-04-15"),
                senhaHash
        )

        then:
        recrutador.empresaId == 7L
        recrutador.nome == "Joana Lima"
        recrutador.areaRecrutamento == "Tecnologia"
        recrutador.dataNascimento == LocalDate.parse("1990-04-15")
        recrutador.senhaHash == senhaHash
        !recrutador.properties.containsKey("senha")
    }
}
