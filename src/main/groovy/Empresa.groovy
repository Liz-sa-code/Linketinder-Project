class Empresa extends Pessoa {

    String pais
    List<String> competencias

    Empresa(String nome, String emailCorporativo, String cnpj,
            String pais, String estado, String cep,
            String descricao, List<String> competencias) {

        super(nome, emailCorporativo, cnpj, estado, cep, descricao)

        this.pais = pais
        this.competencias = competencias
    }

    @Override
    String toString() {
        return """
Empresa: $nome
E-mail corporativo: $email
CNPJ: $documento
País: $pais
Estado: $estado
CEP: $cep
Descrição: $descricao
Competências procuradas: ${competencias.join(', ')}
"""
    }
}