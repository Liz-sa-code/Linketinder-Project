
package service

import repository.EmpresaRepositoryInterface

class EmpresaService {

    private final EmpresaRepositoryInterface empresaRepository

    EmpresaService(EmpresaRepositoryInterface empresaRepository) {
        this.empresaRepository = Objects.requireNonNull(empresaRepository)
    }

    List<Map> listar() {
        return empresaRepository.listar()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return empresaRepository.buscarPorId(id)
    }

    Long cadastrar(Map empresa) {
        validarIdados(empresa)
        return empresaRepository.criar(empresa)
    }

    boolean atualizar(Long id, Map empresa) {
        validarId(id)
        validarIdados(empresa)
        return empresaRepository.atualizar(id, empresa)
    }

    boolean excluir(Long id) {
        validarId(id)
        return empresaRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID da empresa deve ser válido.'
            )
        }
    }

    private void validarIdados(Map empresa) {
        if (empresa == null || empresa.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da empresa devem ser informados.'
            )
        }
    }
}
