import repository.CandidatoInterfaceRepository
import repository.EmpresaRepositoryInterface
import repository.VagaRepositoryInterface
import repository.CompetenciaRepositoryInterface

class BancoDeDados {

    private final CandidatoInterfaceRepository candidatoRepository
    private final EmpresaRepositoryInterface empresaRepository
    private final VagaRepositoryInterface vagaRepository
    private final CompetenciaRepositoryInterface competenciaRepository

    BancoDeDados(
            CandidatoInterfaceRepository candidatoRepository,
            EmpresaRepositoryInterface empresaRepository,
            VagaRepositoryInterface vagaRepository,
            CompetenciaRepositoryInterface competenciaRepository
    ) {
        this.candidatoRepository = Objects.requireNonNull(candidatoRepository)
        this.empresaRepository = Objects.requireNonNull(empresaRepository)
        this.vagaRepository = Objects.requireNonNull(vagaRepository)
        this.competenciaRepository = Objects.requireNonNull(competenciaRepository)
    }

    // CANDIDATOS

    Long criarCandidato(Map candidato, List<String> competencias = []) {
        return candidatoRepository.criar(candidato, competencias)
    }

    List<Map> listarCandidatos() {
        return candidatoRepository.listarCandidatos()
    }

    Map buscarCandidato(Long id) {
        return candidatoRepository.buscarPorId(id)
    }

    boolean atualizarCandidato(
            Long id,
            Map candidato,
            List<String> competencias = []
    ) {
        return candidatoRepository.atualizar(id, candidato, competencias)
    }

    boolean excluirCandidato(Long id) {
        return candidatoRepository.excluir(id)
    }

    // EMPRESAS

    Long criarEmpresa(Map empresa) {
        return empresaRepository.criar(empresa)
    }

    List<Map> listarEmpresas() {
        return empresaRepository.listar()
    }

    Map buscarEmpresa(Long id) {
        return empresaRepository.buscarPorId(id)
    }

    boolean atualizarEmpresa(Long id, Map empresa) {
        return empresaRepository.atualizar(id, empresa)
    }

    boolean excluirEmpresa(Long id) {
        return empresaRepository.excluir(id)
    }

    // VAGAS

    Long criarVaga(Map vaga, List<String> competencias = []) {
        return vagaRepository.criar(vaga, competencias)
    }

    List<Map> listarVagas() {
        return vagaRepository.listar()
    }

    Map buscarVaga(Long id) {
        return vagaRepository.buscarPorId(id)
    }

    boolean atualizarVaga(
            Long id,
            Map vaga,
            List<String> competencias = []
    ) {
        return vagaRepository.atualizar(id, vaga, competencias)
    }

    boolean excluirVaga(Long id) {
        return vagaRepository.excluir(id)
    }

    // COMPETÊNCIAS

    Long criarCompetencia(String nome) {
        return competenciaRepository.criar(nome)
    }

    List<Map> listarCompetencias() {
        return competenciaRepository.listar()
    }

    Map buscarCompetencia(Long id) {
        return competenciaRepository.buscarPorId(id)
    }

    boolean atualizarCompetencia(Long id, String nome) {
        return competenciaRepository.atualizar(id, nome)
    }

    boolean excluirCompetencia(Long id) {
        return competenciaRepository.excluir(id)
    }
}
