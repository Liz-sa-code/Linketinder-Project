package repository

interface RecrutadorRepositoryInterface {

    Long criar(Map dados, Long empresaId)

    Map buscarCredenciaisPorEmail(String email)

    List<Map> listarDaEmpresa(Long empresaId)

    boolean concederPermissao(Long recrutadorId, Long vagaId, Long empresaId)

    boolean revogarPermissao(Long recrutadorId, Long vagaId, Long empresaId)

    boolean possuiPermissao(Long recrutadorId, Long vagaId, Long empresaId)

    boolean registrarInteresse(
            Long recrutadorId,
            Long candidatoId,
            Long vagaId,
            Long empresaId
    )
}
