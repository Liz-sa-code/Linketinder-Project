package model

import java.time.LocalDate

class Recrutador {

    Long id
    Long empresaId
    String nome
    String areaRecrutamento
    String descricao
    String email
    String cpf
    LocalDate dataNascimento
    String senhaHash

    Recrutador() {
    }

    Recrutador(
            Long empresaId,
            String nome,
            String areaRecrutamento,
            String descricao,
            String email,
            String cpf,
            LocalDate dataNascimento,
            String senhaHash
    ) {
        this.empresaId = empresaId
        this.nome = nome
        this.areaRecrutamento = areaRecrutamento
        this.descricao = descricao
        this.email = email
        this.cpf = cpf
        this.dataNascimento = dataNascimento
        this.senhaHash = senhaHash
    }
}
