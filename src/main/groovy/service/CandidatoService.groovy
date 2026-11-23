package service

import repository.CandidatoInterfaceRepository
import security.PasswordHasher

class CandidatoService {

    private final CandidatoInterfaceRepository candidatoRepository

    CandidatoService(CandidatoInterfaceRepository candidatoRepository) {
        this.candidatoRepository = Objects.requireNonNull(candidatoRepository)
    }

    List<Map> listar() {
        return candidatoRepository.listarCandidatos()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return candidatoRepository.buscarPorId(id)
    }

    Long cadastrar(Map dados, List<String> competencias) {
        validarDados(dados)
        Map dadosPersistencia = prepararDadosParaPersistencia(dados)

        return candidatoRepository.criar(
                dadosPersistencia,
                competencias ?: []
        )
    }

    Map buscarCredenciaisPorEmail(String email) {
        return candidatoRepository.buscarCredenciaisPorEmail(email)
    }

    List<Map> listarPerfisAnonimosPorEmpresa(Long empresaId) {
        validarId(empresaId)
        return candidatoRepository.listarPerfisAnonimosPorEmpresa(empresaId)
    }

    boolean registrarInteresse(Long candidatoId, Long vagaId) {
        validarId(candidatoId)
        validarId(vagaId)
        return candidatoRepository.registrarInteresse(candidatoId, vagaId)
    }

    boolean atualizar(Long id, Map dados, List<String> competencias) {
        validarId(id)
        validarDados(dados)

        return candidatoRepository.atualizar(
                id,
                dados,
                competencias ?: []
        )
    }

    boolean excluir(Long id) {
        validarId(id)
        return candidatoRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID do candidato deve ser válido.'
            )
        }
    }

    private void validarDados(Map dados) {
        if (dados == null || dados.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados do candidato devem ser informados.'
            )
        }
    }

    private Map prepararDadosParaPersistencia(Map dados) {
        Map resultado = new LinkedHashMap(dados)
        String senha = resultado.remove('senha')?.toString()
        resultado.remove('senha_hash')

        if (senha) {
            validarSenha(senha)
            resultado.senha_hash = PasswordHasher.hash(senha)
        } else {
            resultado.senha_hash = null
        }

        return resultado
    }

    private void validarSenha(String senha) {
        if (senha.length() < 8 || senha.length() > 256) {
            throw new IllegalArgumentException(
                    'A senha deve ter entre 8 e 256 caracteres.'
            )
        }
    }
}