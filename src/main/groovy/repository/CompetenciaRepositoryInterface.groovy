package repository

interface CompetenciaRepositoryInterface {

    Long criar(String nome)

    List<Map> listar()

    Map buscarPorId(Long id)

    boolean atualizar(Long id, String nome)

    boolean excluir(Long id)
}