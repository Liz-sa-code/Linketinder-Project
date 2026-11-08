package controller

import service.CompetenciaService

class CompetenciaController {

    private final CompetenciaService competenciaService

    CompetenciaController(CompetenciaService competenciaService) {
        this.competenciaService = Objects.requireNonNull(competenciaService)
    }

    List<Map> listar() {
        return competenciaService.listar()
    }

    Map buscarPorId(Long id) {
        return competenciaService.buscarPorId(id)
    }

    Long cadastrar(String nome) {
        return competenciaService.cadastrar(nome)
    }

    boolean atualizar(Long id, String nome) {
        return competenciaService.atualizar(id, nome)
    }

    boolean excluir(Long id) {
        return competenciaService.excluir(id)
    }
}