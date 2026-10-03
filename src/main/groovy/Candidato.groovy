class Candidato extends Pessoa {

    int idade
    List<String> competencias

    Candidato(String nome, String email, String cpf,
              int idade, String estado, String cep,
              String descricao, List<String> competencias) {

        super(nome, email, cpf, estado, cep, descricao)

        this.idade = idade
        this.competencias = competencias
    }

    @Override
    String toString() {
        return """
Nome: $nome
E-mail: $email
CPF: $documento
Idade: $idade
Estado: $estado
CEP: $cep
Descrição: $descricao
Competências: ${competencias.join(', ')}
"""
    }
}