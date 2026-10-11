
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.sql.SQLException
import java.time.LocalDate

class BancoDeDados {

    private Connection conectar() {
        String url = System.getenv("DB_URL") ?:
                "jdbc:postgresql://localhost:5432/linketinder"
        String usuario = System.getenv("DB_USER")
        String senha = System.getenv("DB_PASSWORD")

        if (!usuario || senha == null) {
            throw new IllegalStateException(
                    "Configure DB_USER e DB_PASSWORD."
            )
        }

        return DriverManager.getConnection(url, usuario, senha)
    }

    // CONVERSÃO DE DATA PARA O POSTGRESQL

    private java.sql.Date converterData(Object valor) {
        if (valor == null) {
            throw new IllegalArgumentException(
                    "A data de nascimento é obrigatória."
            )
        }

        if (valor instanceof java.sql.Date) {
            return (java.sql.Date) valor
        }

        if (valor instanceof LocalDate) {
            return java.sql.Date.valueOf((LocalDate) valor)
        }

        try {
            LocalDate data = LocalDate.parse(valor.toString().trim())
            return java.sql.Date.valueOf(data)
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Data inválida. Utilize AAAA-MM-DD, por exemplo 2004-09-29."
            )
        }
    }

    private def transacao(Closure operacao) {
        Connection conn = conectar()

        try {
            conn.autoCommit = false
            def resultado = operacao.call(conn)
            conn.commit()
            return resultado
        } catch (Exception e) {
            conn.rollback()
            throw e
        } finally {
            conn.close()
        }
    }

    private List<Map> consultar(
            Connection conn, String sql, List valores = []
    ) {
        PreparedStatement stmt = conn.prepareStatement(sql)

        try {
            valores.eachWithIndex { valor, i ->
                stmt.setObject(i + 1, valor)
            }

            ResultSet rs = stmt.executeQuery()

            try {
                List<Map> linhas = []

                while (rs.next()) {
                    Map linha = [:]

                    for (int i = 1; i <= rs.metaData.columnCount; i++) {
                        linha[rs.metaData.getColumnLabel(i)] =
                                rs.getObject(i)
                    }

                    linhas.add(linha)
                }

                return linhas
            } finally {
                rs.close()
            }
        } finally {
            stmt.close()
        }
    }

    private int executar(
            Connection conn, String sql, List valores = []
    ) {
        PreparedStatement stmt = conn.prepareStatement(sql)

        try {
            valores.eachWithIndex { valor, i ->
                stmt.setObject(i + 1, valor)
            }

            return stmt.executeUpdate()
        } finally {
            stmt.close()
        }
    }

    private long inserir(
            Connection conn, String sql, List valores = []
    ) {
        PreparedStatement stmt = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
        )

        try {
            valores.eachWithIndex { valor, i ->
                stmt.setObject(i + 1, valor)
            }

            stmt.executeUpdate()

            ResultSet rs = stmt.generatedKeys

            try {
                if (rs.next()) {
                    return rs.getLong(1)
                }

                throw new SQLException(
                        "ID não retornado pelo PostgreSQL."
                )
            } finally {
                rs.close()
            }
        } finally {
            stmt.close()
        }
    }

    // COMPETÊNCIAS

    private long obterOuCriarCompetencia(
            Connection conn, String nome
    ) {
        String competencia = nome?.trim()

        if (!competencia) {
            throw new IllegalArgumentException(
                    "O nome da competência não pode ficar vazio."
            )
        }

        executar(
                conn,
                """INSERT INTO competencias (nome) VALUES (?)
                   ON CONFLICT (nome) DO NOTHING""",
                [competencia]
        )

        return consultar(
                conn,
                "SELECT id FROM competencias WHERE nome = ?",
                [competencia]
        )[0].id as long
    }

    private void definirCompetenciasCandidato(
            Connection conn, long candidatoId, List<String> nomes
    ) {
        executar(
                conn,
                "DELETE FROM candidato_competencia WHERE candidato_id = ?",
                [candidatoId]
        )

        (nomes ?: []).unique().each { nome ->
            long competenciaId =
                    obterOuCriarCompetencia(conn, nome)

            executar(
                    conn,
                    """INSERT INTO candidato_competencia
                       (candidato_id, competencia_id)
                       VALUES (?, ?)""",
                    [candidatoId, competenciaId]
            )
        }
    }

    private void definirCompetenciasVaga(
            Connection conn, long vagaId, List<String> nomes
    ) {
        executar(
                conn,
                "DELETE FROM vaga_competencia WHERE vaga_id = ?",
                [vagaId]
        )

        (nomes ?: []).unique().each { nome ->
            long competenciaId =
                    obterOuCriarCompetencia(conn, nome)

            executar(
                    conn,
                    """INSERT INTO vaga_competencia
                       (vaga_id, competencia_id)
                       VALUES (?, ?)""",
                    [vagaId, competenciaId]
            )
        }
    }

    long criarCompetencia(String nome) {
        return transacao { Connection conn ->
            obterOuCriarCompetencia(conn, nome)
        }
    }

    List<Map> listarCompetencias() {
        Connection conn = conectar()

        try {
            return consultar(
                    conn,
                    "SELECT id, nome FROM competencias ORDER BY nome"
            )
        } finally {
            conn.close()
        }
    }

    boolean atualizarCompetencia(long id, String nome) {
        Connection conn = conectar()

        try {
            return executar(
                    conn,
                    "UPDATE competencias SET nome = ? WHERE id = ?",
                    [nome, id]
            ) > 0
        } finally {
            conn.close()
        }
    }

    boolean excluirCompetencia(long id) {
        Connection conn = conectar()

        try {
            return executar(
                    conn,
                    "DELETE FROM competencias WHERE id = ?",
                    [id]
            ) > 0
        } finally {
            conn.close()
        }
    }

    // CANDIDATOS

    long criarCandidato(Map c, List<String> competencias = []) {
        return transacao { Connection conn ->

            java.sql.Date dataNascimento =
                    converterData(c.data_nascimento)

            long id = inserir(
                    conn,
                    """INSERT INTO candidatos
                       (nome, sobrenome, data_nascimento, email, cpf,
                        pais, cep, descricao, senha_hash)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                    [
                            c.nome,
                            c.sobrenome,
                            dataNascimento,
                            c.email,
                            c.cpf,
                            c.pais,
                            c.cep,
                            c.descricao,
                            c.senha_hash
                    ]
            )

            definirCompetenciasCandidato(
                    conn, id, competencias
            )

            return id
        }
    }

    List<Map> listarCandidatos() {
        Connection conn = conectar()

        try {
            return consultar(
                    conn,
                    """SELECT
                       c.id,
                       c.nome,
                       c.sobrenome,
                       c.data_nascimento,
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
                       c.id, c.nome, c.sobrenome,
                       c.data_nascimento, c.email, c.cpf,
                       c.pais, c.cep, c.descricao
                   ORDER BY c.id"""
            )
        } finally {
            conn.close()
        }
    }

    Map buscarCandidato(long id) {
        Connection conn = conectar()

        try {
            List<Map> resultado = consultar(
                    conn,
                    """SELECT id, nome, sobrenome, data_nascimento,
                              email, cpf, pais, cep, descricao
                       FROM candidatos WHERE id = ?""",
                    [id]
            )

            return resultado ? resultado[0] : null
        } finally {
            conn.close()
        }
    }

    boolean atualizarCandidato(
            long id, Map c, List<String> competencias
    ) {
        return transacao { Connection conn ->

            java.sql.Date dataNascimento =
                    converterData(c.data_nascimento)

            int alterados = executar(
                    conn,
                    """UPDATE candidatos
                       SET nome = ?, sobrenome = ?,
                           data_nascimento = ?, email = ?, cpf = ?,
                           pais = ?, cep = ?, descricao = ?
                       WHERE id = ?""",
                    [
                            c.nome,
                            c.sobrenome,
                            dataNascimento,
                            c.email,
                            c.cpf,
                            c.pais,
                            c.cep,
                            c.descricao,
                            id
                    ]
            )

            if (alterados == 0) {
                return false
            }

            if (c.senha_hash) {
                executar(
                        conn,
                        """UPDATE candidatos
                           SET senha_hash = ? WHERE id = ?""",
                        [c.senha_hash, id]
                )
            }

            definirCompetenciasCandidato(
                    conn, id, competencias
            )

            return true
        }
    }

    boolean excluirCandidato(long id) {
        Connection conn = conectar()

        try {
            return executar(
                    conn,
                    "DELETE FROM candidatos WHERE id = ?",
                    [id]
            ) > 0
        } finally {
            conn.close()
        }
    }

    // EMPRESAS

    long criarEmpresa(Map e) {
        Connection conn = conectar()

        try {
            return inserir(
                    conn,
                    """INSERT INTO empresas
                       (nome, cnpj, email, descricao, pais, cep, senha_hash)
                       VALUES (?, ?, ?, ?, ?, ?, ?)""",
                    [
                            e.nome,
                            e.cnpj,
                            e.email,
                            e.descricao,
                            e.pais,
                            e.cep,
                            e.senha_hash
                    ]
            )
        } finally {
            conn.close()
        }
    }

    List<Map> listarEmpresas() {
        Connection conn = conectar()

        try {
            return consultar(
                    conn,
                    """SELECT id, nome, cnpj, email, descricao, pais, cep
                       FROM empresas ORDER BY id"""
            )
        } finally {
            conn.close()
        }
    }

    Map buscarEmpresa(long id) {
        Connection conn = conectar()

        try {
            List<Map> resultado = consultar(
                    conn,
                    """SELECT id, nome, cnpj, email, descricao, pais, cep
                       FROM empresas WHERE id = ?""",
                    [id]
            )

            return resultado ? resultado[0] : null
        } finally {
            conn.close()
        }
    }

    boolean atualizarEmpresa(long id, Map e) {
        Connection conn = conectar()

        try {
            int alterados = executar(
                    conn,
                    """UPDATE empresas
                       SET nome = ?, cnpj = ?, email = ?,
                           descricao = ?, pais = ?, cep = ?
                       WHERE id = ?""",
                    [
                            e.nome,
                            e.cnpj,
                            e.email,
                            e.descricao,
                            e.pais,
                            e.cep,
                            id
                    ]
            )

            if (alterados > 0 && e.senha_hash) {
                executar(
                        conn,
                        """UPDATE empresas
                           SET senha_hash = ? WHERE id = ?""",
                        [e.senha_hash, id]
                )
            }

            return alterados > 0
        } finally {
            conn.close()
        }
    }

    boolean excluirEmpresa(long id) {
        Connection conn = conectar()

        try {
            return executar(
                    conn,
                    "DELETE FROM empresas WHERE id = ?",
                    [id]
            ) > 0
        } finally {
            conn.close()
        }
    }

    // VAGAS

    long criarVaga(Map v, List<String> competencias = []) {
        return transacao { Connection conn ->

            long id = inserir(
                    conn,
                    """INSERT INTO vagas
                       (empresa_id, nome, descricao, cidade, estado, ativa)
                       VALUES (?, ?, ?, ?, ?, ?)""",
                    [
                            v.empresa_id,
                            v.nome,
                            v.descricao,
                            v.cidade,
                            v.estado,
                            v.ativa == null ? true : v.ativa
                    ]
            )

            definirCompetenciasVaga(conn, id, competencias)

            return id
        }
    }

    List<Map> listarVagas() {
        Connection conn = conectar()

        try {
            return consultar(
                    conn,
                    """SELECT v.id, v.empresa_id, e.nome AS empresa,
                              v.nome, v.descricao, v.cidade, v.estado, v.ativa
                       FROM vagas v
                       JOIN empresas e ON e.id = v.empresa_id
                       ORDER BY v.id"""
            )
        } finally {
            conn.close()
        }
    }

    Map buscarVaga(long id) {
        Connection conn = conectar()

        try {
            List<Map> resultado = consultar(
                    conn,
                    """SELECT id, empresa_id, nome, descricao,
                              cidade, estado, ativa
                       FROM vagas WHERE id = ?""",
                    [id]
            )

            return resultado ? resultado[0] : null
        } finally {
            conn.close()
        }
    }

    boolean atualizarVaga(
            long id, Map v, List<String> competencias
    ) {
        return transacao { Connection conn ->

            int alterados = executar(
                    conn,
                    """UPDATE vagas
                       SET empresa_id = ?, nome = ?, descricao = ?,
                           cidade = ?, estado = ?, ativa = ?
                       WHERE id = ?""",
                    [
                            v.empresa_id,
                            v.nome,
                            v.descricao,
                            v.cidade,
                            v.estado,
                            v.ativa,
                            id
                    ]
            )

            if (alterados == 0) {
                return false
            }

            definirCompetenciasVaga(conn, id, competencias)

            return true
        }
    }

    boolean excluirVaga(long id) {
        Connection conn = conectar()

        try {
            return executar(
                    conn,
                    "DELETE FROM vagas WHERE id = ?",
                    [id]
            ) > 0
        } finally {
            conn.close()
        }
    }
}
