
package repository

interface VagaRepositoryInterface {

    Long criar(Map vaga, List<String> competencias)

    List<Map> listar()

    Map buscarPorId(Long id)

    List<Map> listarAtivasAnonimas()

    List<Map> listarPorEmpresa(Long empresaId)

    List<Map> listarAutorizadas(Long recrutadorId, Long empresaId)

    boolean atualizar(Long id, Map vaga, List<String> competencias)

    boolean excluir(Long id)
}
