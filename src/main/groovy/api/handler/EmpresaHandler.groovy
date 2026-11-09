
package api.handler

import controller.EmpresaController

class EmpresaHandler extends JsonRequestHandler {

    private final EmpresaController empresaController

    EmpresaHandler(EmpresaController empresaController) {
        this.empresaController = Objects.requireNonNull(
                empresaController
        )
    }

    @Override
    protected Map executar(Map dados) {

        if (dados == null || dados.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da empresa devem ser informados.'
            )
        }

        Long id = empresaController.cadastrar(dados)

        if (id == null || id <= 0) {
            throw new IllegalStateException(
                    'Não foi possível cadastrar a empresa.'
            )
        }

        return [
                mensagem: 'Empresa cadastrada com sucesso.',
                id: id
        ]
    }
}
