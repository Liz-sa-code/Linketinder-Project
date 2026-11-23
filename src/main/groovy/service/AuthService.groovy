package service

import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import repository.RecrutadorRepositoryInterface
import security.AuthPrincipal
import security.AuthSessionStore
import security.PasswordHasher

class AuthService {

    private final CandidatoInterfaceRepository candidatoRepository
    private final EmpresaRepositoryInterface empresaRepository
    private final RecrutadorRepositoryInterface recrutadorRepository
    private final AuthSessionStore sessoes

    AuthService(
            CandidatoInterfaceRepository candidatoRepository,
            EmpresaRepositoryInterface empresaRepository,
            RecrutadorRepositoryInterface recrutadorRepository,
            AuthSessionStore sessoes = new AuthSessionStore()
    ) {
        this.candidatoRepository = Objects.requireNonNull(candidatoRepository)
        this.empresaRepository = Objects.requireNonNull(empresaRepository)
        this.recrutadorRepository = Objects.requireNonNull(recrutadorRepository)
        this.sessoes = Objects.requireNonNull(sessoes)
    }

    Map entrar(String tipo, String email, String senha) {
        if (!tipo || !email || !senha) {
            throw new IllegalArgumentException(
                    'Tipo de conta, e-mail e senha são obrigatórios.'
            )
        }

        Map credenciais
        AuthPrincipal principal

        switch (tipo.toLowerCase(Locale.ROOT)) {
            case 'candidato':
                credenciais = candidatoRepository.buscarCredenciaisPorEmail(email)
                if (credenciais != null) {
                    principal = new AuthPrincipal(
                            'candidato',
                            credenciais.id as Long
                    )
                }
                break

            case 'empresa':
                credenciais = empresaRepository.buscarCredenciaisPorEmail(email)
                if (credenciais != null) {
                    principal = new AuthPrincipal(
                            'empresa',
                            credenciais.id as Long,
                            credenciais.id as Long
                    )
                }
                break

            case 'recrutador':
                credenciais = recrutadorRepository.buscarCredenciaisPorEmail(email)
                if (credenciais != null) {
                    principal = new AuthPrincipal(
                            'recrutador',
                            credenciais.id as Long,
                            credenciais.empresaId as Long
                    )
                }
                break

            default:
                throw new IllegalArgumentException('Tipo de conta inválido.')
        }

        if (credenciais == null ||
                !PasswordHasher.verificar(senha, credenciais.senhaHash)) {
            throw new SecurityException('Credenciais inválidas.')
        }

        return [
                token: sessoes.criar(principal),
                tipo: principal.tipo
        ]
    }

    AuthPrincipal autenticar(String token) {
        AuthPrincipal principal = sessoes.obterPrincipal(token)
        if (principal == null) {
            throw new SecurityException('Autenticação obrigatória.')
        }
        return principal
    }

    String criarReferenciaCandidato(String token, Long candidatoId) {
        return sessoes.criarReferenciaCandidato(token, candidatoId)
    }

    Long resolverReferenciaCandidato(String token, String referencia) {
        return sessoes.resolverReferenciaCandidato(token, referencia)
    }

    AuthSessionStore getSessoes() {
        return sessoes
    }
}
