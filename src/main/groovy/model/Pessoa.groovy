package model

abstract class Pessoa implements PessoaInterface {

    String nome
    String email
    String documento
    String estado
    String cep
    String descricao

    Pessoa() {}

    Pessoa(
            String nome,
            String email,
            String documento,
            String estado,
            String cep,
            String descricao
    ) {
        this.nome = nome
        this.email = email
        this.documento = documento
        this.estado = estado
        this.cep = cep
        this.descricao = descricao
    }

    @Override
    String toString() {
        return """
Nome: ${nome}
Email: ${email}
Documento: ${documento}
Estado: ${estado}
CEP: ${cep}
Descrição: ${descricao}
"""
    }
}