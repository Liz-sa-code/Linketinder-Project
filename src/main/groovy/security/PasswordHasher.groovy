package security

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class PasswordHasher {

    private static final String ALGORITHM = 'PBKDF2WithHmacSHA256'
    private static final int ITERATIONS = 210_000
    private static final int KEY_LENGTH = 256
    private static final int SALT_LENGTH = 16
    private static final SecureRandom RANDOM = new SecureRandom()

    static String hash(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException('A senha é obrigatória.')
        }

        byte[] salt = new byte[SALT_LENGTH]
        RANDOM.nextBytes(salt)
        byte[] derived = derivar(senha, salt, ITERATIONS)

        return [
                'pbkdf2-sha256',
                ITERATIONS.toString(),
                Base64.encoder.encodeToString(salt),
                Base64.encoder.encodeToString(derived)
        ].join('$')
    }

    static boolean verificar(String senha, String hash) {
        if (!senha || !hash) {
            return false
        }

        String[] partes = hash.split('\\$', -1)
        if (partes.length != 4 || partes[0] != 'pbkdf2-sha256') {
            return false
        }

        try {
            int iteracoes = Integer.parseInt(partes[1])
            if (iteracoes < 100_000 || iteracoes > 1_000_000) {
                return false
            }

            byte[] salt = Base64.decoder.decode(partes[2])
            byte[] esperado = Base64.decoder.decode(partes[3])
            byte[] obtido = derivar(senha, salt, iteracoes)
            return MessageDigest.isEqual(esperado, obtido)
        } catch (IllegalArgumentException ignored) {
            return false
        }
    }

    private static byte[] derivar(
            String senha,
            byte[] salt,
            int iteracoes
    ) {
        char[] caracteres = senha.toCharArray()
        PBEKeySpec spec = new PBEKeySpec(
                caracteres,
                salt,
                iteracoes,
                KEY_LENGTH
        )

        try {
            return SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(spec)
                    .encoded
        } finally {
            spec.clearPassword()
            Arrays.fill(caracteres, (char) 0)
        }
    }
}
