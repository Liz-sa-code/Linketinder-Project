
package api.handler

import controller.CandidatoController

class CandidatoHandler extends JsonRequestHandler {

    private final CandidatoController candidatoController

    CandidatoHandler(CandidatoController candidatoController) {
        this.candidatoController = Objects.requireNonNull(
                candidatoController
        )
    }

    @Override
    protected Map executar(Map dados) {

        if (!(dados.dados instanceof Map) || dados.dados.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados do candidato devem ser informados.'
            )
        }

        def competencias = dados.competencias ?: []

        if (!(competencias instanceof List)) {
            throw new IllegalArgumentException(
                    'As competências devem ser uma lista.'
            )
        }

        if (!competencias.every {
            it instanceof String && !it.trim().isEmpty()
        }) {
            throw new IllegalArgumentException(
                    'Todas as competências devem ser textos não vazios.'
            )
        }

        Long id = candidatoController.cadastrar(
                dados.dados as Map,
                competencias.collect { it.trim() }
        )

        if (id == null || id <= 0) {
            throw new IllegalStateException(
                    'Não foi possível cadastrar o candidato.'
            )
        }

        return [
                mensagem: 'Candidato cadastrado com sucesso.',
                id: id
        ]
    }
}
