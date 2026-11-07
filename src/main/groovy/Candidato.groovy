class Candidato extends Pessoa {

    Integer idade
    List<Competencia> competencias = []

    Candidato() {
        super()
    }

    Candidato(
            String nome,
            String email,
            String documento,
            String estado,
            String cep,
            String descricao,
            Integer idade,
            List<Competencia> competencias = []
    ) {
        super(nome, email, documento, estado, cep, descricao)
        this.idade = idade
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
Idade: ${idade}
Competências: ${competencias.join(', ')}
"""
    }
}
