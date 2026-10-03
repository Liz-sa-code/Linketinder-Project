abstract class Pessoa implements PessoaInterface {

    String nome
    String email
    String documento
    String estado
    String cep
    String descricao

    Pessoa(String nome, String email, String documento,
           String estado, String cep, String descricao) {
        this.nome = nome
        this.email = email
        this.documento = documento
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
    }

    @Override
    String getNome() {
        return nome
    }

    @Override
    String getEmail() {
        return email
    }

    @Override
    String getDocumento() {
        return documento
    }
}