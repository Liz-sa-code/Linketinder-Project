package model

class Competencia {

    Long id
    String nome

    Competencia() {
    }

    Competencia(String nome) {
        this.nome = nome
    }

    Competencia(Long id, String nome) {
        this.id = id
        this.nome = nome
    }

    @Override
    String toString() {
        return nome
    }
}
