package repository

import database.ConnectionProvider

import java.sql.Date
import java.sql.Statement
import java.time.LocalDate

class RecrutadorRepository implements RecrutadorRepositoryInterface {

    private final ConnectionProvider connectionProvider

    RecrutadorRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider)
    }

    @Override
    Long criar(Map dados, Long empresaId) {
        String sql = '''
            INSERT INTO recrutadores (
                empresa_id,
                nome,
                area_recrutamento,
                descricao,
                email,
                cpf,
                data_nascimento,
                senha_hash
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->
                stmt.setLong(1, empresaId)
                stmt.setString(2, dados.nome)
                stmt.setString(3, dados.areaRecrutamento)
                stmt.setString(4, dados.descricao)
                stmt.setString(5, dados.email)
                stmt.setString(6, dados.cpf)
                stmt.setDate(7, converterData(dados.dataNascimento))
                stmt.setString(8, dados.senha_hash)
                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { keys ->
                    if (!keys.next()) {
                        throw new IllegalStateException(
                                'Não foi possível cadastrar o Recrutador.'
                        )
                    }
                    return keys.getLong(1)
                }
            }
        }
    }

    @Override
    Map buscarCredenciaisPorEmail(String email) {
        String sql = '''
            SELECT id, empresa_id, senha_hash
            FROM recrutadores
            WHERE LOWER(email) = LOWER(?)
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setString(1, email)
                stmt.executeQuery().withCloseable { rs ->
                    if (!rs.next()) {
                        return null
                    }

                    return [
                            id: rs.getLong('id'),
                            empresaId: rs.getLong('empresa_id'),
                            senhaHash: rs.getString('senha_hash')
                    ]
                }
            }
        }
    }

    @Override
    List<Map> listarDaEmpresa(Long empresaId) {
        String sql = '''
            SELECT id, nome, area_recrutamento, email
            FROM recrutadores
            WHERE empresa_id = ?
            ORDER BY id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, empresaId)
                stmt.executeQuery().withCloseable { rs ->
                    List<Map> recrutadores = []
                    while (rs.next()) {
                        recrutadores.add([
                                id: rs.getLong('id'),
                                nome: rs.getString('nome'),
                                areaRecrutamento: rs.getString(
                                        'area_recrutamento'
                                ),
                                email: rs.getString('email')
                        ])
                    }
                    return recrutadores
                }
            }
        }
    }

    @Override
    boolean concederPermissao(
            Long recrutadorId,
            Long vagaId,
            Long empresaId
    ) {
        String sql = '''
            INSERT INTO permissoes_recrutador_vaga (
                recrutador_id,
                vaga_id,
                empresa_id
            )
            SELECT r.id, v.id, r.empresa_id
            FROM recrutadores r
            INNER JOIN vagas v
                ON v.id = ?
               AND v.empresa_id = r.empresa_id
            WHERE r.id = ?
              AND r.empresa_id = ?
            ON CONFLICT (recrutador_id, vaga_id) DO NOTHING
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, vagaId)
                stmt.setLong(2, recrutadorId)
                stmt.setLong(3, empresaId)
                stmt.executeUpdate()
                permissaoExisteNaConexao(
                        conexao,
                        recrutadorId,
                        vagaId,
                        empresaId
                )
            }
        }
    }

    @Override
    boolean revogarPermissao(
            Long recrutadorId,
            Long vagaId,
            Long empresaId
    ) {
        String sql = '''
            DELETE FROM permissoes_recrutador_vaga
            WHERE recrutador_id = ?
              AND vaga_id = ?
              AND empresa_id = ?
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, recrutadorId)
                stmt.setLong(2, vagaId)
                stmt.setLong(3, empresaId)
                stmt.executeUpdate() > 0
            }
        }
    }

    @Override
    boolean possuiPermissao(
            Long recrutadorId,
            Long vagaId,
            Long empresaId
    ) {
        return connectionProvider.conectar().withCloseable { conexao ->
            permissaoExisteNaConexao(
                    conexao,
                    recrutadorId,
                    vagaId,
                    empresaId
            )
        }
    }

    @Override
    boolean registrarInteresse(
            Long recrutadorId,
            Long candidatoId,
            Long vagaId,
            Long empresaId
    ) {
        def conexao = connectionProvider.conectar()
        try {
            conexao.autoCommit = false

            String validacaoSql = '''
                SELECT EXISTS (
                    SELECT 1
                    FROM recrutadores r
                    INNER JOIN vagas v
                        ON v.id = ?
                       AND v.empresa_id = r.empresa_id
                    INNER JOIN permissoes_recrutador_vaga p
                        ON p.recrutador_id = r.id
                       AND p.vaga_id = v.id
                       AND p.empresa_id = r.empresa_id
                    INNER JOIN curtidas_candidato_vaga cv
                        ON cv.candidato_id = ?
                       AND cv.vaga_id = v.id
                    WHERE r.id = ?
                      AND r.empresa_id = ?
                )
            '''

            boolean permitido
            conexao.prepareStatement(validacaoSql).withCloseable { stmt ->
                stmt.setLong(1, vagaId)
                stmt.setLong(2, candidatoId)
                stmt.setLong(3, recrutadorId)
                stmt.setLong(4, empresaId)
                stmt.executeQuery().withCloseable { rs ->
                    rs.next()
                    permitido = rs.getBoolean(1)
                }
            }

            if (!permitido) {
                throw new SecurityException(
                        'A ação não está autorizada para este Recrutador e vaga.'
                )
            }

            String interesseSql = '''
                INSERT INTO interesses_recrutador_candidato (
                    recrutador_id,
                    candidato_id,
                    vaga_id,
                    empresa_id
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT (recrutador_id, candidato_id, vaga_id)
                DO NOTHING
            '''

            conexao.prepareStatement(interesseSql).withCloseable { stmt ->
                stmt.setLong(1, recrutadorId)
                stmt.setLong(2, candidatoId)
                stmt.setLong(3, vagaId)
                stmt.setLong(4, empresaId)
                stmt.executeUpdate()
            }

            String matchSql = '''
                INSERT INTO matches_anonimos (
                    candidato_id,
                    vaga_id,
                    recrutador_id,
                    empresa_id
                )
                SELECT ?, ?, ?, ?
                WHERE EXISTS (
                    SELECT 1
                    FROM curtidas_candidato_vaga cv
                    WHERE cv.candidato_id = ?
                      AND cv.vaga_id = ?
                )
                ON CONFLICT (candidato_id, vaga_id, recrutador_id)
                DO NOTHING
            '''

            conexao.prepareStatement(matchSql).withCloseable { stmt ->
                stmt.setLong(1, candidatoId)
                stmt.setLong(2, vagaId)
                stmt.setLong(3, recrutadorId)
                stmt.setLong(4, empresaId)
                stmt.setLong(5, candidatoId)
                stmt.setLong(6, vagaId)
                stmt.executeUpdate()
            }

            String existeMatchSql = '''
                SELECT EXISTS (
                    SELECT 1 FROM matches_anonimos
                    WHERE candidato_id = ?
                      AND vaga_id = ?
                      AND recrutador_id = ?
                )
            '''

            boolean matchCriado
            conexao.prepareStatement(existeMatchSql).withCloseable { stmt ->
                stmt.setLong(1, candidatoId)
                stmt.setLong(2, vagaId)
                stmt.setLong(3, recrutadorId)
                stmt.executeQuery().withCloseable { rs ->
                    rs.next()
                    matchCriado = rs.getBoolean(1)
                }
            }

            conexao.commit()
            return matchCriado
        } catch (Exception e) {
            conexao.rollback()
            throw e
        } finally {
            conexao.close()
        }
    }

    private boolean permissaoExisteNaConexao(
            java.sql.Connection conexao,
            Long recrutadorId,
            Long vagaId,
            Long empresaId
    ) {
        String sql = '''
            SELECT EXISTS (
                SELECT 1
                FROM permissoes_recrutador_vaga
                WHERE recrutador_id = ?
                  AND vaga_id = ?
                  AND empresa_id = ?
            )
        '''

        return conexao.prepareStatement(sql).withCloseable { stmt ->
            stmt.setLong(1, recrutadorId)
            stmt.setLong(2, vagaId)
            stmt.setLong(3, empresaId)
            stmt.executeQuery().withCloseable { rs ->
                rs.next()
                rs.getBoolean(1)
            }
        }
    }

    private Date converterData(def data) {
        if (data instanceof LocalDate) {
            return Date.valueOf(data)
        }
        if (data instanceof Date) {
            return data
        }
        if (data == null) {
            throw new IllegalArgumentException(
                    'A data de nascimento é obrigatória.'
            )
        }
        return Date.valueOf(data.toString())
    }
}
