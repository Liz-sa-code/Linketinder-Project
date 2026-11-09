
package api.handler

import controller.VagaController

class VagaHandler extends JsonRequestHandler {

    private final VagaController vagaController

    VagaHandler(VagaController vagaController) {
        this.vagaController = Objects.requireNonNull(
                vagaController
        )
    }

    @Override
    protected Map executar(Map dados) {

        if (!(dados.vaga instanceof Map) || dados.vaga.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da vaga devem ser informados.'
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

        Long id = vagaController.cadastrar(
                dados.vaga as Map,
                competencias.collect { it.trim() }
        )

        if (id == null || id <= 0) {
            throw new IllegalStateException(
                    'Não foi possível cadastrar a vaga.'
            )
        }

        return [
                mensagem: 'Vaga cadastrada com sucesso.',
                id: id
        ]
    }
}
