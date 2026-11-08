package controller

import service.CandidatoService

class CandidatoController {

    private final CandidatoService candidatoService

    CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = Objects.requireNonNull(candidatoService)
    }

    List<Map> listar() {
        return candidatoService.listar()
    }

    Map buscarPorId(Long id) {
        return candidatoService.buscarPorId(id)
    }

    Long cadastrar(Map dados, List<String> competencias) {
        return candidatoService.cadastrar(dados, competencias)
    }

    boolean atualizar(Long id, Map dados, List<String> competencias) {
        return candidatoService.atualizar(id, dados, competencias)
    }

    boolean excluir(Long id) {
        return candidatoService.excluir(id)
    }
}