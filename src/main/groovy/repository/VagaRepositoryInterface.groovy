
package repository

interface VagaRepositoryInterface {

    Long criar(Map vaga, List<String> competencias)

    List<Map> listar()

    Map buscarPorId(Long id)

    boolean atualizar(Long id, Map vaga, List<String> competencias)

    boolean excluir(Long id)
}
