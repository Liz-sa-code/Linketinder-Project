
package repository

import database.ConexaoBanco

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class EmpresaRepository {


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

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet rs = stmt.executeQuery()

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

            rs.close()
            stmt.close()

            return empresas
        } finally {
            conexao.close()
        }
    }

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

            Map empresa = [
                    id        : rs.getLong('id'),
                    nome      : rs.getString('nome'),
                    cnpj      : rs.getString('cnpj'),
                    email     : rs.getString('email'),
                    descricao : rs.getString('descricao'),
                    pais      : rs.getString('pais'),
                    cep       : rs.getString('cep')
            ]

            rs.close()
            stmt.close()

            return empresa
        } finally {
            conexao.close()
        }
    }

    Long criar(Map empresa) {
        String sql = '''
            INSERT INTO empresas (
                nome,
                cnpj,
                email,
                descricao,
                pais,
                cep,
                senha_hash
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        '''

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )

            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.cnpj)
            stmt.setString(3, empresa.email)
            stmt.setString(4, empresa.descricao)
            stmt.setString(5, empresa.pais)
            stmt.setString(6, empresa.cep)
            stmt.setString(
                    7,
                    empresa.senha_hash ?: empresa.senhaHash
            )

            stmt.executeUpdate()

            ResultSet chaves = stmt.generatedKeys

            if (!chaves.next()) {
                throw new SQLException(
                        'Não foi possível obter o ID da empresa.'
                )
            }

            Long id = chaves.getLong(1)

            chaves.close()
            stmt.close()

            return id
        } finally {
            conexao.close()
        }
    }

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

        Connection conexao = ConexaoBanco.conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)

            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.cnpj)
            stmt.setString(3, empresa.email)
            stmt.setString(4, empresa.descricao)
            stmt.setString(5, empresa.pais)
            stmt.setString(6, empresa.cep)
            stmt.setLong(7, id)

            int linhasAfetadas = stmt.executeUpdate()

            stmt.close()

            return linhasAfetadas > 0
        } finally {
            conexao.close()
        }
    }

    boolean excluir(Long id) {
        String sql = '''
            DELETE FROM empresas
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
}
