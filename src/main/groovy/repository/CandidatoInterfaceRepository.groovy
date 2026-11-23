
package repository

interface CandidatoInterfaceRepository {

    Long criar(Map dados, List<String> competencias)

    Map buscarCredenciaisPorEmail(String email)

    List<Map> listarCandidatos()

    Map buscarPorId(Long id)

    List<Map> listarPerfisAnonimosPorEmpresa(Long empresaId)

    boolean registrarInteresse(Long candidatoId, Long vagaId)

    boolean atualizar(Long id, Map dados, List<String> competencias)

    boolean excluir(Long id)
}
