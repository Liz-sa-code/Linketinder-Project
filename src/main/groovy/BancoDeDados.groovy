
import repository.CandidatoRepository
import repository.EmpresaRepository
import repository.VagaRepository
import repository.CompetenciaRepository

class BancoDeDados {

    private final CandidatoRepository candidatoRepository
    private final EmpresaRepository empresaRepository
    private final VagaRepository vagaRepository
    private final CompetenciaRepository competenciaRepository

    BancoDeDados() {
        this.candidatoRepository = new CandidatoRepository()
        this.empresaRepository = new EmpresaRepository()
        this.vagaRepository = new VagaRepository()
        this.competenciaRepository = new CompetenciaRepository()
    }

    // CANDIDATOS

    long criarCandidato(Map candidato, List<String> competencias = []) {
        return candidatoRepository.criar(candidato, competencias)
    }

    List<Map> listarCandidatos() {
        return candidatoRepository.listarCandidatos()
    }

    Map buscarCandidato(long id) {
        return candidatoRepository.buscarPorId(id)
    }

    boolean atualizarCandidato(
            long id,
            Map candidato,
            List<String> competencias = []
    ) {
        return candidatoRepository.atualizar(id, candidato, competencias)
    }

    boolean excluirCandidato(long id) {
        return candidatoRepository.excluir(id)
    }

    // EMPRESAS

    long criarEmpresa(Map empresa) {
        return empresaRepository.criar(empresa)
    }

    List<Map> listarEmpresas() {
        return empresaRepository.listar()
    }

    Map buscarEmpresa(long id) {
        return empresaRepository.buscarPorId(id)
    }

    boolean atualizarEmpresa(long id, Map empresa) {
        return empresaRepository.atualizar(id, empresa)
    }

    boolean excluirEmpresa(long id) {
        return empresaRepository.excluir(id)
    }

    // VAGAS

    long criarVaga(Map vaga, List<String> competencias = []) {
        return vagaRepository.criar(vaga, competencias)
    }

    List<Map> listarVagas() {
        return vagaRepository.listar()
    }

    Map buscarVaga(long id) {
        return vagaRepository.buscarPorId(id)
    }

    boolean atualizarVaga(
            long id,
            Map vaga,
            List<String> competencias = []
    ) {
        return vagaRepository.atualizar(id, vaga, competencias)
    }

    boolean excluirVaga(long id) {
        return vagaRepository.excluir(id)
    }

    // COMPETÊNCIAS

    long criarCompetencia(String nome) {
        return competenciaRepository.criar(nome)
    }

    List<Map> listarCompetencias() {
        return competenciaRepository.listar()
    }

    boolean atualizarCompetencia(long id, String nome) {
        return competenciaRepository.atualizar(id, nome)
    }

    boolean excluirCompetencia(long id) {
        return competenciaRepository.excluir(id)
    }
}
