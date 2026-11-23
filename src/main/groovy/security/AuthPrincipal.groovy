package security

class AuthPrincipal {

    final String tipo
    final Long id
    final Long empresaId

    AuthPrincipal(String tipo, Long id, Long empresaId = null) {
        this.tipo = Objects.requireNonNull(tipo)
        this.id = Objects.requireNonNull(id)
        this.empresaId = empresaId
    }
}
