package service

import repository.CandidatoInterfaceRepository

class CandidatoService {

    private final CandidatoInterfaceRepository candidatoRepository

    CandidatoService(CandidatoInterfaceRepository candidatoRepository) {
        this.candidatoRepository = Objects.requireNonNull(candidatoRepository)
    }

    List<Map> listar() {
        return candidatoRepository.listarCandidatos()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return candidatoRepository.buscarPorId(id)
    }

    Long cadastrar(Map dados, List<String> competencias) {
        validarDados(dados)

        return candidatoRepository.criar(
                dados,
                competencias ?: []
        )
    }

    boolean atualizar(Long id, Map dados, List<String> competencias) {
        validarId(id)
        validarDados(dados)

        return candidatoRepository.atualizar(
                id,
                dados,
                competencias ?: []
        )
    }

    boolean excluir(Long id) {
        validarId(id)
        return candidatoRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID do candidato deve ser válido.'
            )
        }
    }

    private void validarDados(Map dados) {
        if (dados == null || dados.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados do candidato devem ser informados.'
            )
        }
    }
}