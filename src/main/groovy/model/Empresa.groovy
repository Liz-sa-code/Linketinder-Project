package model

class Empresa extends Pessoa {

    String pais
    List<Competencia> competencias = []

    Empresa() {
        super()
    }

    Empresa(
            String nome,
            String email,
            String documento,
            String estado,
            String cep,
            String descricao,
            String pais,
            List<Competencia> competencias = []
    ) {
        super(nome, email, documento, estado, cep, descricao)
        this.pais = pais
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
${super.toString()}
País: ${pais}
Competências: ${competencias.join(', ')}
"""
    }
}