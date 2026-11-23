package controller

import security.AuthPrincipal
import service.RecrutadorService

class RecrutadorController {

    private final RecrutadorService recrutadorService

    RecrutadorController(RecrutadorService recrutadorService) {
        this.recrutadorService =
                Objects.requireNonNull(recrutadorService)
    }

    Long cadastrar(Map dados, AuthPrincipal principal) {
        return recrutadorService.cadastrar(dados, principal)
    }

    boolean concederPermissao(
            Long recrutadorId,
            Long vagaId,
            AuthPrincipal principal
    ) {
        return recrutadorService.concederPermissao(
                recrutadorId,
                vagaId,
                principal
        )
    }

    boolean revogarPermissao(
            Long recrutadorId,
            Long vagaId,
            AuthPrincipal principal
    ) {
        return recrutadorService.revogarPermissao(
                recrutadorId,
                vagaId,
                principal
        )
    }

    List<Map> listarDaEmpresa(AuthPrincipal principal) {
        return recrutadorService.listarDaEmpresa(principal)
    }

    List<Map> listarVagasAutorizadas(AuthPrincipal principal) {
        return recrutadorService.listarVagasAutorizadas(principal)
    }

    List<Map> listarCandidatosAnonimos(
            AuthPrincipal principal,
            String token
    ) {
        return recrutadorService.listarCandidatosAnonimos(principal, token)
    }

    boolean registrarInteresse(
            AuthPrincipal principal,
            String token,
            String referencia,
            Long vagaId
    ) {
        return recrutadorService.registrarInteresse(
                principal,
                token,
                referencia,
                vagaId
        )
    }

    List<Map> listarMatches(AuthPrincipal principal) {
        return recrutadorService.listarMatches(principal)
    }
}
