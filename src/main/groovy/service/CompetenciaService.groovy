package service

import repository.CompetenciaRepositoryInterface

class CompetenciaService {

    private final CompetenciaRepositoryInterface competenciaRepository

    CompetenciaService(CompetenciaRepositoryInterface competenciaRepository) {
        this.competenciaRepository = Objects.requireNonNull(competenciaRepository)
    }

    List<Map> listar() {
        return competenciaRepository.listar()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return competenciaRepository.buscarPorId(id)
    }

    Long cadastrar(String nome) {
        validarNome(nome)
        return competenciaRepository.criar(nome.trim())
    }

    boolean atualizar(Long id, String nome) {
        validarId(id)
        validarNome(nome)

        return competenciaRepository.atualizar(id, nome.trim())
    }

    boolean excluir(Long id) {
        validarId(id)
        return competenciaRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID da competência deve ser válido.'
            )
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    'O nome da competência deve ser informado.'
            )
        }
    }
}