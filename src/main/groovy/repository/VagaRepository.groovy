
package repository

import database.ConexaoBanco

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class VagaRepository {

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

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet rs = stmt.executeQuery()

            List<Map> vagas = []

            while (rs.next()) {
                vagas.add(mapearVaga(rs))
            }

            rs.close()
            stmt.close()

            return vagas
        } finally {
            conexao.close()
        }
    }

    Map buscarPorId(long id) {
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

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setLong(1, id)

            ResultSet rs = stmt.executeQuery()

            if (!rs.next()) {
                rs.close()
                stmt.close()
                return null
            }

            Map vaga = mapearVaga(rs)

            rs.close()
            stmt.close()

            return vaga
        } finally {
            conexao.close()
        }
    }

    long criar(Map vaga, List<String> competencias = []) {
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

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.autoCommit = false

            PreparedStatement stmt = conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )

            preencherDados(stmt, vaga)
            stmt.executeUpdate()

            ResultSet chaves = stmt.generatedKeys

            if (!chaves.next()) {
                throw new SQLException(
                        'Não foi possível obter o ID da vaga.'
                )
            }

            long id = chaves.getLong(1)

            chaves.close()
            stmt.close()

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

    boolean atualizar(
            long id,
            Map vaga,
            List<String> competencias = []
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

        Connection conexao = ConexaoBanco.conectar()

        try {
            conexao.autoCommit = false

            PreparedStatement stmt = conexao.prepareStatement(sql)

            preencherDados(stmt, vaga)
            stmt.setLong(7, id)

            int linhasAfetadas = stmt.executeUpdate()
            stmt.close()

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

    boolean excluir(long id) {
        String sql = '''
            DELETE FROM vagas
            WHERE id = ?
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setLong(1, id)

            int linhasAfetadas = stmt.executeUpdate()

            stmt.close()

            return linhasAfetadas > 0
        } finally {
            conexao.close()
        }
    }


    private void associarCompetencias(
            Connection conexao,
            long vagaId,
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

        PreparedStatement stmt = conexao.prepareStatement(sql)

        competencias.each { String competencia ->
            stmt.setLong(1, vagaId)
            stmt.setString(2, competencia)
            stmt.addBatch()
        }

        stmt.executeBatch()
        stmt.close()
    }

    private void removerCompetencias(
            Connection conexao,
            long vagaId
    ) {
        String sql = '''
            DELETE FROM vaga_competencia
            WHERE vaga_id = ?
        '''

        PreparedStatement stmt = conexao.prepareStatement(sql)
        stmt.setLong(1, vagaId)

        stmt.executeUpdate()
        stmt.close()
    }

    /**
     * Preenche os parâmetros do INSERT e UPDATE.
     */
    private void preencherDados(
            PreparedStatement stmt,
            Map vaga
    ) {
        stmt.setLong(1, (vaga.empresa_id ?: vaga.empresaId) as Long)
        stmt.setString(2, vaga.nome)
        stmt.setString(3, vaga.descricao)
        stmt.setString(4, vaga.cidade)
        stmt.setString(5, vaga.estado)

        boolean ativa = vaga.ativa == null ? true : vaga.ativa as boolean
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
