package security

import spock.lang.Specification

class PasswordHasherSpec extends Specification {

    def "hashes passwords with a unique salted representation"() {
        when:
        String first = PasswordHasher.hash('correct horse battery')
        String second = PasswordHasher.hash('correct horse battery')

        then:
        first != 'correct horse battery'
        first != second
        PasswordHasher.verificar('correct horse battery', first)
        !PasswordHasher.verificar('wrong password', first)
    }

    def "rejects malformed or unsupported password hashes"() {
        expect:
        !PasswordHasher.verificar('password', '')
        !PasswordHasher.verificar('password', 'plain-text')
        !PasswordHasher.verificar('password', 'pbkdf2-sha256$1$bad$bad')
    }
}
