
package repository

import database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.sql.Date

class CandidatoRepository implements CandidatoInterfaceRepository {

    private final ConnectionProvider connectionProvider

    CandidatoRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider)
    }

    @Override
    List<Map> listarCandidatos() {
        String sql = '''
            SELECT
                c.id,
                c.nome,
                c.sobrenome,
                c.data_nascimento,
                EXTRACT(
                    YEAR FROM AGE(CURRENT_DATE, c.data_nascimento)
                )::INTEGER AS idade,
                c.email,
                c.cpf,
                c.pais,
                c.cep,
                c.descricao,
                COALESCE(
                    STRING_AGG(comp.nome, ', ' ORDER BY comp.nome),
                    'Nenhuma competência cadastrada'
                ) AS competencias
            FROM candidatos c
            LEFT JOIN candidato_competencia cc
                ON cc.candidato_id = c.id
            LEFT JOIN competencias comp
                ON comp.id = cc.competencia_id
            GROUP BY
                c.id,
                c.nome,
                c.sobrenome,
                c.data_nascimento,
                c.email,
                c.cpf,
                c.pais,
                c.cep,
                c.descricao
            ORDER BY c.id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.executeQuery().withCloseable { rs ->

                    List<Map> candidatos = []

                    while (rs.next()) {
                        candidatos.add([
                                id              : rs.getLong('id'),
                                nome            : rs.getString('nome'),
                                sobrenome       : rs.getString('sobrenome'),
                                data_nascimento : rs.getDate('data_nascimento'),
                                idade           : rs.getInt('idade'),
                                email           : rs.getString('email'),
                                cpf             : rs.getString('cpf'),
                                pais            : rs.getString('pais'),
                                cep             : rs.getString('cep'),
                                descricao       : rs.getString('descricao'),
                                competencias    : rs.getString('competencias')
                        ])
                    }

                    return candidatos
                }
            }
        }
    }

    @Override
    Map buscarPorId(Long id) {
        String sql = '''
            SELECT
                c.id,
                c.nome,
                c.sobrenome,
                c.data_nascimento,
                EXTRACT(
                    YEAR FROM AGE(CURRENT_DATE, c.data_nascimento)
                )::INTEGER AS idade,
                c.email,
                c.cpf,
                c.pais,
                c.cep,
                c.descricao,
                COALESCE(
                    STRING_AGG(comp.nome, ', ' ORDER BY comp.nome),
                    ''
                ) AS competencias
            FROM candidatos c
            LEFT JOIN candidato_competencia cc
                ON cc.candidato_id = c.id
            LEFT JOIN competencias comp
                ON comp.id = cc.competencia_id
            WHERE c.id = ?
            GROUP BY c.id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                stmt.executeQuery().withCloseable { rs ->

                    if (!rs.next()) {
                        return null
                    }

                    return [
                            id              : rs.getLong('id'),
                            nome            : rs.getString('nome'),
                            sobrenome       : rs.getString('sobrenome'),
                            data_nascimento : rs.getDate('data_nascimento'),
                            idade           : rs.getInt('idade'),
                            email           : rs.getString('email'),
                            cpf             : rs.getString('cpf'),
                            pais            : rs.getString('pais'),
                            cep             : rs.getString('cep'),
                            descricao       : rs.getString('descricao'),
                            competencias    : rs.getString('competencias')
                    ]
                }
            }
        }
    }

    @Override
    Long criar(Map dados, List<String> competencias) {
        String sql = '''
            INSERT INTO candidatos (
                nome,
                sobrenome,
                data_nascimento,
                email,
                cpf,
                pais,
                cep,
                descricao
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        '''

        Connection conexao = connectionProvider.conectar()

        try {
            conexao.autoCommit = false

            Long candidatoId

            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->

                preencherDados(stmt, dados)

                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { chaves ->

                    if (!chaves.next()) {
                        throw new SQLException(
                                'Não foi possível obter o ID do candidato.'
                        )
                    }

                    candidatoId = chaves.getLong(1)
                }
            }

            associarCompetencias(conexao, candidatoId, competencias)

            conexao.commit()

            return candidatoId

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
            Map dados,
            List<String> competencias
    ) {
        String sql = '''
            UPDATE candidatos
            SET
                nome = ?,
                sobrenome = ?,
                data_nascimento = ?,
                email = ?,
                cpf = ?,
                pais = ?,
                cep = ?,
                descricao = ?
            WHERE id = ?
        '''

        Connection conexao = connectionProvider.conectar()

        try {
            conexao.autoCommit = false

            int linhasAfetadas

            conexao.prepareStatement(sql).withCloseable { stmt ->

                preencherDados(stmt, dados)
                stmt.setLong(9, id)

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
        String sql = 'DELETE FROM candidatos WHERE id = ?'

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                return stmt.executeUpdate() > 0
            }
        }
    }

    private void associarCompetencias(
            Connection conexao,
            Long candidatoId,
            List<String> competencias
    ) {
        if (!competencias) {
            return
        }

        String sql = '''
            INSERT INTO candidato_competencia (
                candidato_id,
                competencia_id
            )
            SELECT ?, id
            FROM competencias
            WHERE LOWER(nome) = LOWER(?)
            ON CONFLICT DO NOTHING
        '''

        conexao.prepareStatement(sql).withCloseable { stmt ->

            competencias.each { String competencia ->
                stmt.setLong(1, candidatoId)
                stmt.setString(2, competencia)
                stmt.addBatch()
            }

            stmt.executeBatch()
        }
    }

    private void removerCompetencias(
            Connection conexao,
            Long candidatoId
    ) {
        String sql = '''
            DELETE FROM candidato_competencia
            WHERE candidato_id = ?
        '''

        conexao.prepareStatement(sql).withCloseable { stmt ->
            stmt.setLong(1, candidatoId)
            stmt.executeUpdate()
        }
    }

    private void preencherDados(
            PreparedStatement stmt,
            Map dados
    ) {
        stmt.setString(1, dados.nome)
        stmt.setString(2, dados.sobrenome)

        stmt.setDate(
                3,
                converterData(dados.data_nascimento ?: dados.dataNascimento)
        )

        stmt.setString(4, dados.email)
        stmt.setString(5, dados.cpf)
        stmt.setString(6, dados.pais)
        stmt.setString(7, dados.cep)
        stmt.setString(8, dados.descricao)
    }

    private Date converterData(def data) {
        if (data == null) {
            throw new IllegalArgumentException(
                    'A data de nascimento é obrigatória.'
            )
        }

        if (data instanceof Date) {
            return data
        }

        if (data instanceof java.util.Date) {
            return new Date(data.time)
        }

        return Date.valueOf(data.toString())
    }
}
