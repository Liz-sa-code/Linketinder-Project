
package repository

import database.ConnectionProvider

import java.sql.Statement
import java.sql.SQLException

class EmpresaRepository implements EmpresaRepositoryInterface {

    private final ConnectionProvider connectionProvider

    EmpresaRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider
    }

    @Override
    List<Map> listar() {
        String sql = '''
            SELECT
                id,
                nome,
                cnpj,
                email,
                descricao,
                pais,
                cep
            FROM empresas
            ORDER BY id
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->
                stmt.executeQuery().withCloseable { rs ->

                    List<Map> empresas = []

                    while (rs.next()) {
                        empresas.add([
                                id        : rs.getLong('id'),
                                nome      : rs.getString('nome'),
                                cnpj      : rs.getString('cnpj'),
                                email     : rs.getString('email'),
                                descricao : rs.getString('descricao'),
                                pais      : rs.getString('pais'),
                                cep       : rs.getString('cep')
                        ])
                    }

                    return empresas
                }
            }
        }
    }

    @Override
    Map buscarPorId(Long id) {
        String sql = '''
            SELECT
                id,
                nome,
                cnpj,
                email,
                descricao,
                pais,
                cep
            FROM empresas
            WHERE id = ?
        '''

        return connectionProvider.conectar().withCloseable { conexao ->
            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                stmt.executeQuery().withCloseable { rs ->

                    if (!rs.next()) {
                        return null
                    }

                    return [
                            id        : rs.getLong('id'),
                            nome      : rs.getString('nome'),
                            cnpj      : rs.getString('cnpj'),
                            email     : rs.getString('email'),
                            descricao : rs.getString('descricao'),
                            pais      : rs.getString('pais'),
                            cep       : rs.getString('cep')
                    ]
                }
            }
        }
    }

    @Override
    Long criar(Map empresa) {
        String sql = '''
            INSERT INTO empresas (
                nome,
                cnpj,
                email,
                descricao,
                pais,
                cep
            )
            VALUES (?, ?, ?, ?, ?, ?)
        '''

        return connectionProvider.conectar().withCloseable { conexao ->

            conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            ).withCloseable { stmt ->

                stmt.setString(1, empresa.nome)
                stmt.setString(2, empresa.cnpj)
                stmt.setString(3, empresa.email)
                stmt.setString(4, empresa.descricao)
                stmt.setString(5, empresa.pais)
                stmt.setString(6, empresa.cep)

                stmt.executeUpdate()

                stmt.generatedKeys.withCloseable { chaves ->

                    if (!chaves.next()) {
                        throw new SQLException(
                                'Não foi possível obter o ID da empresa.'
                        )
                    }

                    return chaves.getLong(1)
                }
            }
        }
    }

    @Override
    boolean atualizar(Long id, Map empresa) {
        String sql = '''
            UPDATE empresas
            SET
                nome = ?,
                cnpj = ?,
                email = ?,
                descricao = ?,
                pais = ?,
                cep = ?
            WHERE id = ?
        '''

        return connectionProvider.conectar().withCloseable { conexao ->

            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setString(1, empresa.nome)
                stmt.setString(2, empresa.cnpj)
                stmt.setString(3, empresa.email)
                stmt.setString(4, empresa.descricao)
                stmt.setString(5, empresa.pais)
                stmt.setString(6, empresa.cep)
                stmt.setLong(7, id)

                return stmt.executeUpdate() > 0
            }
        }
    }

    @Override
    boolean excluir(Long id) {
        String sql = '''
            DELETE FROM empresas
            WHERE id = ?
        '''

        return connectionProvider.conectar().withCloseable { conexao ->

            conexao.prepareStatement(sql).withCloseable { stmt ->

                stmt.setLong(1, id)

                return stmt.executeUpdate() > 0
            }
        }
    }
}
