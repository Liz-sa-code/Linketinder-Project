package repository

import database.ConnectionProvider

class MatchRepository {

    private final ConnectionProvider connectionProvider

    MatchRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider)
    }

    List<Map> listarParaRecrutador(Long recrutadorId) {
        String sql = '''
            SELECT
                m.id AS match_id,
                v.id AS vaga_id,
                v.nome AS vaga_nome,
                v.descricao AS vaga_descricao,
                v.cidade,
                v.estado,
                c.descricao AS perfil_descricao,
                COALESCE(
                    ARRAY_AGG(DISTINCT comp.nome ORDER BY comp.nome)
                        FILTER (WHERE comp.nome IS NOT NULL),
                    ARRAY[]::VARCHAR[]
                ) AS competencias,
                COALESCE(
                    (
                        SELECT ARRAY_AGG(
                            DISTINCT CONCAT_WS(
                                ' — ',
                                f.nome,
                                cf.instituicao,
                                cf.nivel
                            )
                            ORDER BY CONCAT_WS(
                                ' — ',
                                f.nome,
                                cf.instituicao,
                                cf.nivel
                            )
                        )
                        FROM candidato_formacao cf
                        INNER JOIN formacoes f ON f.id = cf.formacao_id
                        WHERE cf.candidato_id = c.id
                    ),
                    ARRAY[]::TEXT[]
                ) AS formacoes
            FROM matches_anonimos m
            INNER JOIN vagas v ON v.id = m.vaga_id
            INNER JOIN candidatos c ON c.id = m.candidato_id
            LEFT JOIN candidato_competencia cc
                ON cc.candidato_id = c.id
            LEFT JOIN competencias comp ON comp.id = cc.competencia_id
            WHERE m.recrutador_id = ?
            GROUP BY m.id, v.id, c.id
            ORDER BY m.id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, recrutadorId)
                stmt.executeQuery().withCloseable { rs ->
                    List<Map> matches = []
                    while (rs.next()) {
                        matches.add([
                                id: rs.getLong('match_id'),
                                vaga: [
                                        id: rs.getLong('vaga_id'),
                                        nome: rs.getString('vaga_nome'),
                                        descricao: rs.getString('vaga_descricao'),
                                        cidade: rs.getString('cidade'),
                                        estado: rs.getString('estado')
                                ],
                                candidato: [
                                        descricao: rs.getString('perfil_descricao'),
                                        competencias: (rs.getArray('competencias')?.array
                                                as String[] ?: []).toList(),
                                        formacoes: (rs.getArray('formacoes')?.array
                                                as String[] ?: []).toList()
                                ]
                        ])
                    }
                    return matches
                }
            }
        }
    }

    List<Map> listarParaCandidato(Long candidatoId) {
        String sql = '''
            SELECT
                m.id AS match_id,
                v.id AS vaga_id,
                v.nome AS vaga_nome,
                v.descricao AS vaga_descricao,
                v.cidade,
                v.estado
            FROM matches_anonimos m
            INNER JOIN vagas v ON v.id = m.vaga_id
            WHERE m.candidato_id = ?
            ORDER BY m.id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.setLong(1, candidatoId)
                stmt.executeQuery().withCloseable { rs ->
                    List<Map> matches = []
                    while (rs.next()) {
                        matches.add([
                                id: rs.getLong('match_id'),
                                vaga: [
                                        id: rs.getLong('vaga_id'),
                                        nome: rs.getString('vaga_nome'),
                                        descricao: rs.getString('vaga_descricao'),
                                        cidade: rs.getString('cidade'),
                                        estado: rs.getString('estado')
                                ]
                        ])
                    }
                    return matches
                }
            }
        }
    }
}
