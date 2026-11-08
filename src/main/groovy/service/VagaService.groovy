
package service

import repository.VagaRepositoryInterface

class VagaService {

    private final VagaRepositoryInterface vagaRepository

    VagaService(VagaRepositoryInterface vagaRepository) {
        this.vagaRepository = Objects.requireNonNull(vagaRepository)
    }

    List<Map> listar() {
        return vagaRepository.listar()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return vagaRepository.buscarPorId(id)
    }

    Long cadastrar(Map vaga, List<String> competencias) {
        validarDados(vaga)
        return vagaRepository.criar(vaga, competencias ?: [])
    }

    boolean atualizar(
            Long id,
            Map vaga,
            List<String> competencias
    ) {
        validarId(id)
        validarDados(vaga)

        return vagaRepository.atualizar(
                id,
                vaga,
                competencias ?: []
        )
    }

    boolean excluir(Long id) {
        validarId(id)
        return vagaRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID da vaga deve ser válido.'
            )
        }
    }

    private void validarDados(Map vaga) {
        if (vaga == null || vaga.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da vaga devem ser informados.'
            )
        }
    }
}
