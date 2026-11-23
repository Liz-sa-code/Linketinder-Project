package security

import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap
import java.util.Base64

class AuthSessionStore {

    private static final long DURACAO_SESSAO_MS = 8L * 60 * 60 * 1000
    private static final SecureRandom RANDOM = new SecureRandom()
    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>()

    String criar(AuthPrincipal principal) {
        byte[] bytes = new byte[32]
        RANDOM.nextBytes(bytes)
        String token = Base64.urlEncoder.withoutPadding()
                .encodeToString(bytes)
        sessoes.put(token, new Sessao(principal))
        return token
    }

    AuthPrincipal obterPrincipal(String token) {
        Sessao sessao = sessaoValida(token)
        return sessao?.principal
    }

    String criarReferenciaCandidato(String token, Long candidatoId) {
        Sessao sessao = sessaoValida(token)
        if (sessao == null || sessao.principal.tipo != 'recrutador') {
            throw new SecurityException('Sessão não autorizada.')
        }

        String referencia = UUID.randomUUID().toString()
        sessao.referenciasCandidato.put(referencia, candidatoId)
        return referencia
    }

    Long resolverReferenciaCandidato(String token, String referencia) {
        Sessao sessao = sessaoValida(token)
        if (sessao == null || sessao.principal.tipo != 'recrutador') {
            return null
        }
        return sessao.referenciasCandidato.get(referencia)
    }

    private Sessao sessaoValida(String token) {
        if (!token) {
            return null
        }

        Sessao sessao = sessoes.get(token)
        if (sessao != null && sessao.expiraEm <= System.currentTimeMillis()) {
            sessoes.remove(token, sessao)
            return null
        }
        return sessao
    }

    private static class Sessao {
        final AuthPrincipal principal
        final long expiraEm
        final Map<String, Long> referenciasCandidato =
                new ConcurrentHashMap<>()

        Sessao(AuthPrincipal principal) {
            this.principal = principal
            this.expiraEm = System.currentTimeMillis() + DURACAO_SESSAO_MS
        }
    }
}
