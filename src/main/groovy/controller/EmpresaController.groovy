
package controller

import service.EmpresaService

class EmpresaController {

    private final EmpresaService empresaService

    EmpresaController(EmpresaService empresaService) {
        this.empresaService = Objects.requireNonNull(empresaService)
    }

    List<Map> listar() {
        return empresaService.listar()
    }

    Map buscarPorId(Long id) {
        return empresaService.buscarPorId(id)
    }

    Long cadastrar(Map empresa) {
        return empresaService.cadastrar(empresa)
    }

    boolean atualizar(Long id, Map empresa) {
        return empresaService.atualizar(id, empresa)
    }

    boolean excluir(Long id) {
        return empresaService.excluir(id)
    }
}
