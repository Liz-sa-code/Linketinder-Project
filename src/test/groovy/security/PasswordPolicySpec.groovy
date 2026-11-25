package security

import spock.lang.Specification

class PasswordPolicySpec extends Specification {

    def "requires a nonblank password between 8 and 256 characters"() {
        when:
        PasswordPolicy.exigirSenha(senha)

        then:
        thrown(IllegalArgumentException)

        where:
        senha << [null, '', '   ', '1234567', 'x' * 257]
    }

    def "accepts passwords at the supported length boundaries"() {
        when:
        PasswordPolicy.exigirSenha(senha)

        then:
        noExceptionThrown()

        where:
        senha << ['12345678', 'x' * 256]
    }
}
