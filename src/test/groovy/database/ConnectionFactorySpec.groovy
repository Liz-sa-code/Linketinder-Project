package database

import spock.lang.Specification

class ConnectionFactorySpec extends Specification {

    def "deve retornar sempre a mesma instancia da fabrica"() {
        when:
        def fabrica1 = ConnectionFactory.getInstancia()
        def fabrica2 = ConnectionFactory.getInstancia()

        then:
        fabrica1.is(fabrica2)
    }

    def "deve criar um provedor de conexao PostgreSQL"() {
        when:
        def provedor = ConnectionFactory.getInstancia()
                .criar('postgresql')

        then:
        provedor instanceof ConexaoBanco
    }

    def "deve rejeitar banco de dados nao suportado"() {
        when:
        ConnectionFactory.getInstancia().criar('oracle')

        then:
        thrown(IllegalArgumentException)
    }

    def "deve criar um provedor de conexao MySQL"() {
        when:
        def provedor = ConnectionFactory.getInstancia().criar('mysql')

        then:
        provedor instanceof ConexaoMySQL
    }
}