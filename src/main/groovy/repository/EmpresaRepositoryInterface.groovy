
package repository

interface EmpresaRepositoryInterface {

    Long criar(Map empresa)

    List<Map> listar()

    Map buscarPorId(Long id)

    boolean atualizar(Long id, Map empresa)

    boolean excluir(Long id)
}
