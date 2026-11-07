
package repository

import database.ConexaoBanco
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class CompetenciaRepository {

    List<Map> listar() {
        String sql = '''
            SELECT id, nome
            FROM competencias
            ORDER BY nome
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.executeQuery().withCloseable { rs ->
                    List<Map> competencias = []

                    while (rs.next()) {
                        competencias.add(mapearCompetencia(rs))
                    }

                    return competencias
                }
            }
        } finally {
            conexao.close()
        }
    }

    Map buscarPorId(long id) {
        String sql = '''
            SELECT id, nome
            FROM competencias
            WHERE id = ?
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, id)

                stmt.executeQuery().withCloseable { rs ->
                    return rs.next() ? mapearCompetencia(rs) : null
                }
            }
        } finally {
            conexao.close()
        }
    }

    long criar(String nome) {
        String sql = '''
            INSERT INTO competencias (nome)
            VALUES (?)
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->

                stmt.setString(1, nome)
                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { rs ->
                    if (rs.next()) {
                        return rs.getLong(1)
                    }
                }
            }

            throw new RuntimeException(
                    "Não foi possível obter o ID da competência."
            )
        } finally {
            conexao.close()
        }
    }

    boolean atualizar(long id, String nome) {
        String sql = '''
            UPDATE competencias
            SET nome = ?
            WHERE id = ?
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setString(1, nome)
                stmt.setLong(2, id)

                return stmt.executeUpdate() > 0
            }
        } finally {
            conexao.close()
        }
    }

    boolean excluir(long id) {
        String sql = 'DELETE FROM competencias WHERE id = ?'

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, id)
                return stmt.executeUpdate() > 0
            }
        } finally {
            conexao.close()
        }
    }

    private Map mapearCompetencia(ResultSet rs) {
        return [
                id  : rs.getLong('id'),
                nome: rs.getString('nome')
        ]
    }
}
