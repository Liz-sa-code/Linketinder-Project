package security

class PasswordPolicy {

    static void exigirSenha(def senha) {
        if (!(senha instanceof String) || senha.trim().isEmpty()) {
            throw new IllegalArgumentException('A senha é obrigatória.')
        }

        if (senha.length() < 8 || senha.length() > 256) {
            throw new IllegalArgumentException(
                    'A senha deve ter entre 8 e 256 caracteres.'
            )
        }
    }
}
