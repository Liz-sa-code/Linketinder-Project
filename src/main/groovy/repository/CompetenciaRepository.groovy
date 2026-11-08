package repository

import database.ConnectionProvider
import java.sql.Statement
import java.sql.ResultSet

class CompetenciaRepository implements CompetenciaRepositoryInterface {

    private final ConnectionProvider connectionProvider

    CompetenciaRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider
    }

    @Override
    List<Map> listar() {
        String sql = '''
            SELECT id, nome
            FROM competencias
            ORDER BY nome
        '''

        def conexao = connectionProvider.conectar()

        conexao.withCloseable {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.executeQuery().withCloseable { rs ->
                    List<Map> competencias = []

                    while (rs.next()) {
                        competencias.add(mapearCompetencia(rs))
                    }

                    return competencias
                }
            }
        }
    }

    @Override
    Map buscarPorId(Long id) {
        String sql = '''
            SELECT id, nome
            FROM competencias
            WHERE id = ?
        '''

        def conexao = connectionProvider.conectar()

        conexao.withCloseable {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, id)

                stmt.executeQuery().withCloseable { rs ->
                    return rs.next() ? mapearCompetencia(rs) : null
                }
            }
        }
    }

    @Override
    Long criar(String nome) {
        String sql = '''
            INSERT INTO competencias (nome)
            VALUES (?)
        '''

        def conexao = connectionProvider.conectar()
        Long idGerado = null

        conexao.withCloseable {
            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->

                stmt.setString(1, nome)
                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { rs ->
                    if (rs.next()) {
                        idGerado = rs.getLong(1)
                    }
                }
            }
        }

        if (idGerado == null) {
            throw new RuntimeException(
                    'Não foi possível obter o ID da competência.'
            )
        }

        return idGerado
    }

    @Override
    boolean atualizar(Long id, String nome) {
        String sql = '''
            UPDATE competencias
            SET nome = ?
            WHERE id = ?
        '''

        def conexao = connectionProvider.conectar()

        conexao.withCloseable {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setString(1, nome)
                stmt.setLong(2, id)

                return stmt.executeUpdate() > 0
            }
        }
    }

    @Override
    boolean excluir(Long id) {
        String sql = '''
            DELETE FROM competencias
            WHERE id = ?
        '''

        def conexao = connectionProvider.conectar()

        conexao.withCloseable {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, id)

                return stmt.executeUpdate() > 0
            }
        }
    }

    private Map mapearCompetencia(ResultSet rs) {
        return [
                id  : rs.getLong('id'),
                nome: rs.getString('nome')
        ]
    }
}