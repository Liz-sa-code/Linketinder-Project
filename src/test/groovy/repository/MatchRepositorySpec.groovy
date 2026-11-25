package repository

import database.ConnectionProvider
import spock.lang.Specification

import java.sql.Array
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class MatchRepositorySpec extends Specification {

    def "recruiter match response includes its vacancy ID"() {
        given:
        ConnectionProvider provider = Mock()
        Connection connection = Mock()
        PreparedStatement statement = Mock()
        ResultSet rows = Mock()
        Array competencies = Mock()
        Array formations = Mock()
        MatchRepository repository = new MatchRepository(provider)

        provider.conectar() >> connection
        connection.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> rows
        rows.next() >>> [true, false]
        rows.getLong('match_id') >> 10L
        rows.getLong('vaga_id') >> 44L
        rows.getString('vaga_nome') >> 'Backend'
        rows.getString('vaga_descricao') >> 'API'
        rows.getString('cidade') >> 'Salvador'
        rows.getString('estado') >> 'BA'
        rows.getString('perfil_descricao') >> 'Perfil profissional'
        rows.getArray('competencias') >> competencies
        rows.getArray('formacoes') >> formations
        competencies.getArray() >> (['Groovy'] as String[])
        formations.getArray() >> (['Sistemas de Informação'] as String[])

        when:
        List<Map> result = repository.listarParaRecrutador(8L)

        then:
        result.first().vaga.id == 44L
    }

    def "candidate match response includes its vacancy ID"() {
        given:
        ConnectionProvider provider = Mock()
        Connection connection = Mock()
        PreparedStatement statement = Mock()
        ResultSet rows = Mock()
        MatchRepository repository = new MatchRepository(provider)

        provider.conectar() >> connection
        connection.prepareStatement(_ as String) >> statement
        statement.executeQuery() >> rows
        rows.next() >>> [true, false]
        rows.getLong('match_id') >> 10L
        rows.getLong('vaga_id') >> 44L
        rows.getString('vaga_nome') >> 'Backend'
        rows.getString('vaga_descricao') >> 'API'
        rows.getString('cidade') >> 'Salvador'
        rows.getString('estado') >> 'BA'

        when:
        List<Map> result = repository.listarParaCandidato(21L)

        then:
        result.first().vaga.id == 44L
    }
}
