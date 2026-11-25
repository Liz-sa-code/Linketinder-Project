package database

import spock.lang.IgnoreIf
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

@IgnoreIf({
    !System.getenv('DB_URL') ||
            !System.getenv('DB_USER') ||
            !System.getenv('DB_PASSWORD')
})
class DatabaseSchemaIntegrationSpec extends Specification {

    def "configured database contains the authentication and recruiter schema"() {
        given:
        Connection connection = DriverManager.getConnection(
                System.getenv('DB_URL'),
                System.getenv('DB_USER'),
                System.getenv('DB_PASSWORD')
        )

        when:
        List<String> missing = [
                ['candidatos', 'senha_hash'],
                ['empresas', 'senha_hash'],
                ['recrutadores', 'senha_hash'],
                ['permissoes_recrutador_vaga', 'recrutador_id'],
                ['interesses_recrutador_candidato', 'recrutador_id'],
                ['matches_anonimos', 'recrutador_id']
        ].findAll { List<String> spec ->
            !colunaExiste(connection, spec[0], spec[1])
        }.collect { List<String> spec ->
            "${spec[0]}.${spec[1]}"
        }
        if (!uniqueVagaEmpresaExiste(connection)) {
            missing.add('vagas(id, empresa_id) UNIQUE')
        }

        then:
        missing.empty

        cleanup:
        connection?.close()
    }

    private static boolean colunaExiste(
            Connection connection,
            String tabela,
            String coluna
    ) {
        ResultSet colunas = connection.metaData.getColumns(
                connection.catalog,
                'public',
                tabela,
                coluna
        )
        try {
            return colunas.next()
        } finally {
            colunas.close()
        }
    }

    private static boolean uniqueVagaEmpresaExiste(Connection connection) {
        connection.prepareStatement('''
            SELECT EXISTS (
                SELECT 1
                FROM pg_constraint
                WHERE conrelid = 'public.vagas'::regclass
                  AND contype = 'u'
                  AND pg_get_constraintdef(oid) =
                      'UNIQUE (id, empresa_id)'
            )
        ''').withCloseable { statement ->
            statement.executeQuery().withCloseable { result ->
                result.next()
                return result.getBoolean(1)
            }
        }
    }
}
