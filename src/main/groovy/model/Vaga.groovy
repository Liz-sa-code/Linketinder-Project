package model

class Vaga {

    Long id
    String titulo
    String descricao
    String localidade
    BigDecimal salario
    Empresa empresa
    List<Competencia> competencias = []

    Vaga() {
    }

    Vaga(
            String titulo,
            String descricao,
            String localidade,
            BigDecimal salario,
            Empresa empresa,
            List<Competencia> competencias = []
    ) {
        this.titulo = titulo
        this.descricao = descricao
        this.localidade = localidade
        this.salario = salario
        this.empresa = empresa
        this.competencias = competencias ?: []
    }

    void adicionarCompetencia(Competencia competencia) {
        if (competencia != null && !competencias.contains(competencia)) {
            competencias.add(competencia)
        }
    }

    void removerCompetencia(Competencia competencia) {
        competencias.remove(competencia)
    }

    @Override
    String toString() {
        return """
Vaga: ${titulo}
Descrição: ${descricao}
Localidade: ${localidade}
Salário: ${salario}
Empresa: ${empresa?.nome}
Competências: ${competencias.join(', ')}
"""
    }
}
