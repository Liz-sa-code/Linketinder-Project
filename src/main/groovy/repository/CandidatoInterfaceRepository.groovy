
package repository

interface CandidatoInterfaceRepository {

    Long criar(Map dados, List<String> competencias)

    List<Map> listarCandidatos()

    Map buscarPorId(Long id)

    boolean atualizar(Long id, Map dados, List<String> competencias)

    boolean excluir(Long id)
}
