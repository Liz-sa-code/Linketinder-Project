package repository

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Date

class CandidatoRepository {

    private Connection conectar() {
        String url = System.getenv('DB_URL')
        String usuario = System.getenv('DB_USER')
        String senha = System.getenv('DB_PASSWORD')

        if (!url || !usuario || !senha) {
            throw new IllegalStateException(
                    'Configure as variáveis DB_URL, DB_USER e DB_PASSWORD.'
            )
        }

        return DriverManager.getConnection(url, usuario, senha)
    }

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

        Connection conexao = conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            ResultSet rs = stmt.executeQuery()

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

            rs.close()
            stmt.close()

            return candidatos

        } finally {
            conexao.close()
        }
    }

    /**
     * Busca um candidato pelo ID.
     */
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
                c.descricao
            FROM candidatos c
            WHERE c.id = ?
        '''

        Connection conexao = conectar()

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setLong(1, id)

            ResultSet rs = stmt.executeQuery()

            if (!rs.next()) {
                return null
            }

            Map candidato = [
                    id              : rs.getLong('id'),
                    nome            : rs.getString('nome'),
                    sobrenome       : rs.getString('sobrenome'),
                    data_nascimento : rs.getDate('data_nascimento'),
                    idade           : rs.getInt('idade'),
                    email           : rs.getString('email'),
                    cpf             : rs.getString('cpf'),
                    pais            : rs.getString('pais'),
                    cep             : rs.getString('cep'),
                    descricao       : rs.getString('descricao')
            ]

            rs.close()
            stmt.close()

            return candidato

        } finally {
            conexao.close()
        }
    }

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
                descricao,
                senha_hash
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        '''

        Connection conexao = conectar()

        try {
            conexao.autoCommit = false

            PreparedStatement stmt = conexao.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )

            stmt.setString(1, dados.nome)
            stmt.setString(2, dados.sobrenome)
            stmt.setDate(3, converterData(dados.data_nascimento ?: dados.dataNascimento))
            stmt.setString(4, dados.email)
            stmt.setString(5, dados.cpf)
            stmt.setString(6, dados.pais)
            stmt.setString(7, dados.cep)
            stmt.setString(8, dados.descricao)
            stmt.setString(9, dados.senha_hash ?: dados.senhaHash)

            stmt.executeUpdate()

            ResultSet chaves = stmt.generatedKeys

            if (!chaves.next()) {
                throw new SQLException('Não foi possível obter o ID do candidato.')
            }

            Long candidatoId = chaves.getLong(1)

            chaves.close()
            stmt.close()

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

    boolean atualizar(Long id, Map dados, List<String> competencias) {
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

        Connection conexao = conectar()

        try {
            conexao.autoCommit = false

            PreparedStatement stmt = conexao.prepareStatement(sql)

            stmt.setString(1, dados.nome)
            stmt.setString(2, dados.sobrenome)
            stmt.setDate(3, converterData(dados.data_nascimento ?: dados.dataNascimento))
            stmt.setString(4, dados.email)
            stmt.setString(5, dados.cpf)
            stmt.setString(6, dados.pais)
            stmt.setString(7, dados.cep)
            stmt.setString(8, dados.descricao)
            stmt.setLong(9, id)

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

    boolean excluir(Long id) {
        String sql = 'DELETE FROM candidatos WHERE id = ?'

        Connection conexao = conectar()

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

        PreparedStatement stmt = conexao.prepareStatement(sql)

        competencias.each { String competencia ->
            stmt.setLong(1, candidatoId)
            stmt.setString(2, competencia)
            stmt.addBatch()
        }

        stmt.executeBatch()
        stmt.close()
    }

    /**
     * Remove as competências associadas ao candidato.
     */
    private void removerCompetencias(
            Connection conexao,
            Long candidatoId
    ) {
        String sql = '''
            DELETE FROM candidato_competencia
            WHERE candidato_id = ?
        '''

        PreparedStatement stmt = conexao.prepareStatement(sql)
        stmt.setLong(1, candidatoId)
        stmt.executeUpdate()
        stmt.close()
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