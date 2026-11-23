
package service

import repository.EmpresaRepositoryInterface
import security.PasswordHasher

class EmpresaService {

    private final EmpresaRepositoryInterface empresaRepository

    EmpresaService(EmpresaRepositoryInterface empresaRepository) {
        this.empresaRepository = Objects.requireNonNull(empresaRepository)
    }

    List<Map> listar() {
        return empresaRepository.listar()
    }

    Map buscarPorId(Long id) {
        validarId(id)
        return empresaRepository.buscarPorId(id)
    }

    Long cadastrar(Map empresa) {
        validarIdados(empresa)
        return empresaRepository.criar(prepararDadosParaPersistencia(empresa))
    }

    Map buscarCredenciaisPorEmail(String email) {
        return empresaRepository.buscarCredenciaisPorEmail(email)
    }

    boolean atualizar(Long id, Map empresa) {
        validarId(id)
        validarIdados(empresa)
        return empresaRepository.atualizar(id, empresa)
    }

    boolean excluir(Long id) {
        validarId(id)
        return empresaRepository.excluir(id)
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    'O ID da empresa deve ser válido.'
            )
        }
    }

    private void validarIdados(Map empresa) {
        if (empresa == null || empresa.isEmpty()) {
            throw new IllegalArgumentException(
                    'Os dados da empresa devem ser informados.'
            )
        }
    }

    private Map prepararDadosParaPersistencia(Map empresa) {
        Map resultado = new LinkedHashMap(empresa)
        String senha = resultado.remove('senha')?.toString()
        resultado.remove('senha_hash')

        if (senha) {
            if (senha.length() < 8 || senha.length() > 256) {
                throw new IllegalArgumentException(
                        'A senha deve ter entre 8 e 256 caracteres.'
                )
            }
            resultado.senha_hash = PasswordHasher.hash(senha)
        } else {
            resultado.senha_hash = null
        }

        return resultado
    }
}
