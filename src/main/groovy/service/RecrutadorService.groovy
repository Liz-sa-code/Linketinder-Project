package service

import repository.CandidatoInterfaceRepository
import repository.MatchRepository
import repository.RecrutadorRepositoryInterface
import repository.VagaRepositoryInterface
import security.AuthPrincipal
import security.PasswordHasher

import java.time.LocalDate

class RecrutadorService {

    private final RecrutadorRepositoryInterface recrutadorRepository
    private final CandidatoInterfaceRepository candidatoRepository
    private final VagaRepositoryInterface vagaRepository
    private final MatchRepository matchRepository
    private final AuthService authService

    RecrutadorService(
            RecrutadorRepositoryInterface recrutadorRepository,
            CandidatoInterfaceRepository candidatoRepository,
            VagaRepositoryInterface vagaRepository,
            MatchRepository matchRepository,
            AuthService authService
    ) {
        this.recrutadorRepository =
                Objects.requireNonNull(recrutadorRepository)
        this.candidatoRepository =
                Objects.requireNonNull(candidatoRepository)
        this.vagaRepository = Objects.requireNonNull(vagaRepository)
        this.matchRepository = Objects.requireNonNull(matchRepository)
        this.authService = Objects.requireNonNull(authService)
    }

    Long cadastrar(Map dados, AuthPrincipal principal) {
        exigirTipo(principal, 'empresa')
        validarDados(dados)

        String senha = dados.senha?.toString()
        if (senha == null || senha.length() < 8 || senha.length() > 256) {
            throw new IllegalArgumentException(
                    'A senha deve ter entre 8 e 256 caracteres.'
            )
        }

        String dataNascimento = (
                dados.dataNascimento ?: dados.data_nascimento
        )?.toString()
        if (!dataNascimento) {
            throw new IllegalArgumentException(
                    'A data de nascimento é obrigatória.'
            )
        }

        Map seguro = [
                nome: dados.nome,
                areaRecrutamento: dados.areaRecrutamento
                        ?: dados.area_recrutamento,
                descricao: dados.descricao,
                email: dados.email,
                cpf: dados.cpf,
                dataNascimento: LocalDate.parse(dataNascimento),
                senha_hash: PasswordHasher.hash(senha)
        ]

        validarTexto(seguro.nome, 'O nome')
        validarTexto(seguro.areaRecrutamento, 'A área de recrutamento')
        validarTexto(seguro.descricao, 'A descrição')
        validarTexto(seguro.email, 'O e-mail')
        validarTexto(seguro.cpf, 'O CPF')

        return recrutadorRepository.criar(seguro, principal.empresaId)
    }

    boolean concederPermissao(
            Long recrutadorId,
            Long vagaId,
            AuthPrincipal principal
    ) {
        exigirTipo(principal, 'empresa')
        validarId(recrutadorId, 'Recrutador')
        validarId(vagaId, 'vaga')
        return recrutadorRepository.concederPermissao(
                recrutadorId,
                vagaId,
                principal.empresaId
        )
    }

    boolean revogarPermissao(
            Long recrutadorId,
            Long vagaId,
            AuthPrincipal principal
    ) {
        exigirTipo(principal, 'empresa')
        validarId(recrutadorId, 'Recrutador')
        validarId(vagaId, 'vaga')
        return recrutadorRepository.revogarPermissao(
                recrutadorId,
                vagaId,
                principal.empresaId
        )
    }

    List<Map> listarDaEmpresa(AuthPrincipal principal) {
        exigirTipo(principal, 'empresa')
        return recrutadorRepository.listarDaEmpresa(principal.empresaId)
    }

    List<Map> listarVagasAutorizadas(AuthPrincipal principal) {
        exigirTipo(principal, 'recrutador')
        return vagaRepository.listarAutorizadas(
                principal.id,
                principal.empresaId
        )
    }

    List<Map> listarCandidatosAnonimos(
            AuthPrincipal principal,
            String token
    ) {
        exigirTipo(principal, 'recrutador')
        return candidatoRepository
                .listarPerfisAnonimosPorEmpresa(principal.empresaId)
                .collect { Map perfil ->
                    [
                            referencia: authService.criarReferenciaCandidato(
                                    token,
                                    perfil.candidatoId as Long
                            ),
                            descricao: perfil.descricao,
                            competencias: perfil.competencias,
                            formacoes: perfil.formacoes ?: []
                    ]
                }
    }

    boolean registrarInteresse(
            AuthPrincipal principal,
            String token,
            String referencia,
            Long vagaId
    ) {
        exigirTipo(principal, 'recrutador')
        validarId(vagaId, 'vaga')

        Long candidatoId = authService.resolverReferenciaCandidato(
                token,
                referencia
        )
        if (candidatoId == null) {
            throw new SecurityException(
                    'Perfil não disponível para esta sessão.'
            )
        }

        return recrutadorRepository.registrarInteresse(
                principal.id,
                candidatoId,
                vagaId,
                principal.empresaId
        )
    }

    List<Map> listarMatches(AuthPrincipal principal) {
        if (principal?.tipo == 'recrutador') {
            return matchRepository.listarParaRecrutador(principal.id)
        }
        if (principal?.tipo == 'candidato') {
            return matchRepository.listarParaCandidato(principal.id)
        }
        throw new SecurityException('Acesso não autorizado.')
    }

    private static void exigirTipo(AuthPrincipal principal, String tipo) {
        if (principal?.tipo != tipo || principal.id == null) {
            throw new SecurityException('Acesso não autorizado.')
        }
        if (tipo == 'empresa' && principal.empresaId != principal.id) {
            throw new SecurityException('Acesso não autorizado.')
        }
        if (tipo == 'recrutador' && principal.empresaId == null) {
            throw new SecurityException('Acesso não autorizado.')
        }
    }

    private static void validarDados(Map dados) {
        if (dados == null || dados.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados do Recrutador devem ser informados.'
            )
        }
    }

    private static void validarTexto(def valor, String campo) {
        if (!(valor instanceof String) || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("${campo} é obrigatório.")
        }
    }

    private static void validarId(Long id, String entidade) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID de ${entidade} deve ser válido."
            )
        }
    }
}
