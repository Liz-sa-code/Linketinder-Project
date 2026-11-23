
package controller

import service.VagaService

class VagaController {

    private final VagaService vagaService

    VagaController(VagaService vagaService) {
        this.vagaService = Objects.requireNonNull(vagaService)
    }

    List<Map> listar() {
        return vagaService.listar()
    }

    List<Map> listarAtivasAnonimas() {
        return vagaService.listarAtivasAnonimas()
    }

    List<Map> listarPorEmpresa(Long empresaId) {
        return vagaService.listarPorEmpresa(empresaId)
    }

    List<Map> listarAutorizadas(Long recrutadorId, Long empresaId) {
        return vagaService.listarAutorizadas(recrutadorId, empresaId)
    }

    Map buscarPorId(Long id) {
        return vagaService.buscarPorId(id)
    }

    Long cadastrar(Map vaga, List<String> competencias) {
        return vagaService.cadastrar(vaga, competencias)
    }

    boolean atualizar(
            Long id,
            Map vaga,
            List<String> competencias
    ) {
        return vagaService.atualizar(id, vaga, competencias)
    }

    boolean excluir(Long id) {
        return vagaService.excluir(id)
    }
}
