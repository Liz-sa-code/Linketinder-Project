
package repository

import database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class VagaRepository implements VagaRepositoryInterface {

    private final ConnectionProvider connectionProvider

    VagaRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider
    }

    @Override
    List<Map> listar() {
        String sql = '''
            SELECT
                v.id,
                v.empresa_id,
                v.nome,
                v.descricao,
                v.cidade,
                v.estado,
                v.ativa,
                e.nome AS empresa,
                COALESCE(
                    STRING_AGG(c.nome, ', ' ORDER BY c.nome),
                    'Nenhuma competência cadastrada'
                ) AS competencias
            FROM vagas v
            INNER JOIN empresas e
                ON e.id = v.empresa_id
            LEFT JOIN vaga_competencia vc
                ON vc.vaga_id = v.id
            LEFT JOIN competencias c
                ON c.id = vc.competencia_id
            GROUP BY
                v.id,
                v.empresa_id,
                v.nome,
                v.descricao,
                v.cidade,
                v.estado,
                v.ativa,
                e.nome
            ORDER BY v.id
        '''

        connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.executeQuery().withCloseable { rs ->

                    List<Map> vagas = []

                    while (rs.next()) {
                        vagas.add(mapearVaga(rs))
                    }

                    return vagas
                }
            }
        }
    }

    @Override
    Map buscarPorId(Long id) {
        String sql = '''
            SELECT
                v.id,
                v.empresa_id,
                v.nome,
                v.descricao,
                v.cidade,
                v.estado,
                v.ativa,
                e.nome AS empresa,
                COALESCE(
                    STRING_AGG(c.nome, ', ' ORDER BY c.nome),
                    'Nenhuma competência cadastrada'
                ) AS competencias
            FROM vagas v
            INNER JOIN empresas e
                ON e.id = v.empresa_id
            LEFT JOIN vaga_competencia vc
                ON vc.vaga_id = v.id
            LEFT JOIN competencias c
                ON c.id = vc.competencia_id
            WHERE v.id = ?
            GROUP BY
                v.id,
                v.empresa_id,
                v.nome,
                v.descricao,
                v.cidade,
                v.estado,
                v.ativa,
                e.nome
        '''

        connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                stmt.executeQuery().withCloseable { rs ->

                    if (!rs.next()) {
                        return null
                    }

                    return mapearVaga(rs)
                }
            }
        }
    }

    @Override
    Long criar(Map vaga, List<String> competencias) {
        String sql = '''
            INSERT INTO vagas (
                empresa_id,
                nome,
                descricao,
                cidade,
                estado,
                ativa
            )
            VALUES (?, ?, ?, ?, ?, ?)
        '''

        Connection conexao = connectionProvider.conectar()

        try {
            conexao.autoCommit = false

            Long id

            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->

                preencherDados(stmt, vaga)
                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { chaves ->

                    if (!chaves.next()) {
                        throw new SQLException(
                                'Não foi possível obter o ID da vaga.'
                        )
                    }

                    id = chaves.getLong(1)
                }
            }

            associarCompetencias(conexao, id, competencias)

            conexao.commit()

            return id

        } catch (Exception e) {
            conexao.rollback()
            throw e

        } finally {
            conexao.close()
        }
    }

    @Override
    boolean atualizar(
            Long id,
            Map vaga,
            List<String> competencias
    ) {
        String sql = '''
            UPDATE vagas
            SET
                empresa_id = ?,
                nome = ?,
                descricao = ?,
                cidade = ?,
                estado = ?,
                ativa = ?
            WHERE id = ?
        '''

        Connection conexao = connectionProvider.conectar()

        try {
            conexao.autoCommit = false

            int linhasAfetadas

            conexao.prepareStatement(sql).withCloseable { stmt ->

                preencherDados(stmt, vaga)
                stmt.setLong(7, id)

                linhasAfetadas = stmt.executeUpdate()
            }

            if (linhasAfetadas == 0) {
                conexao.rollback()
                return false
            }

            removerCompetencias(conexao, id)
            associarCompetencias(conexao, id, competencias)

            conexao.commit()

            return true

        } catch (Exception e) {
            conexao.rollback()
            throw e

        } finally {
            conexao.close()
        }
    }

    @Override
    boolean excluir(Long id) {
        String sql = '''
            DELETE FROM vagas
            WHERE id = ?
        '''

        connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                return stmt.executeUpdate() > 0
            }
        }
    }

    private void associarCompetencias(
            Connection conexao,
            Long vagaId,
            List<String> competencias
    ) {
        if (!competencias) {
            return
        }

        String sql = '''
            INSERT INTO vaga_competencia (
                vaga_id,
                competencia_id
            )
            SELECT ?, id
            FROM competencias
            WHERE LOWER(nome) = LOWER(?)
            ON CONFLICT DO NOTHING
        '''

        conexao.prepareStatement(sql).withCloseable { stmt ->

            competencias.each { String competencia ->
                stmt.setLong(1, vagaId)
                stmt.setString(2, competencia)
                stmt.addBatch()
            }

            stmt.executeBatch()
        }
    }

    private void removerCompetencias(
            Connection conexao,
            Long vagaId
    ) {
        String sql = '''
            DELETE FROM vaga_competencia
            WHERE vaga_id = ?
        '''

        conexao.prepareStatement(sql).withCloseable { stmt ->
            stmt.setLong(1, vagaId)
            stmt.executeUpdate()
        }
    }

    private void preencherDados(
            PreparedStatement stmt,
            Map vaga
    ) {
        stmt.setLong(
                1,
                (vaga.empresa_id ?: vaga.empresaId) as Long
        )

        stmt.setString(2, vaga.nome)
        stmt.setString(3, vaga.descricao)
        stmt.setString(4, vaga.cidade)
        stmt.setString(5, vaga.estado)

        boolean ativa = vaga.ativa == null
                ? true
                : vaga.ativa as boolean

        stmt.setBoolean(6, ativa)
    }

    private Map mapearVaga(ResultSet rs) {
        return [
                id           : rs.getLong('id'),
                empresa_id   : rs.getLong('empresa_id'),
                nome         : rs.getString('nome'),
                descricao    : rs.getString('descricao'),
                cidade       : rs.getString('cidade'),
                estado       : rs.getString('estado'),
                ativa        : rs.getBoolean('ativa'),
                empresa      : rs.getString('empresa'),
                competencias : rs.getString('competencias')
        ]
    }
}
