
import java.util.Scanner

class Menu {

    private final Scanner scanner
    private final BancoDeDados bancoDeDados

    Menu(Scanner scanner, BancoDeDados bancoDeDados) {
        this.scanner = scanner
        this.bancoDeDados = bancoDeDados
    }

    void executar() {
        int opcao = -1

        while (opcao != 0) {
            exibirMenu()
            opcao = lerOpcao()

            if (opcao != 0) {
                executarOpcao(opcao)
                aguardarContinuacao()
            }
        }

        println "Encerrando o Linketinder..."
    }

    private void exibirMenu() {
        println """
===== LINKETINDER =====
1  - Listar candidatos
2  - Listar empresas
3  - Cadastrar candidato
4  - Atualizar candidato
5  - Excluir candidato
6  - Cadastrar empresa
7  - Atualizar empresa
8  - Excluir empresa
9  - Listar vagas
10 - Cadastrar vaga
11 - Atualizar vaga
12 - Excluir vaga
13 - Listar competências
14 - Cadastrar competência
15 - Atualizar competência
16 - Excluir competência
0  - Sair
"""
    }

    private int lerOpcao() {
        print "Escolha uma opção: "

        try {
            return Integer.parseInt(scanner.nextLine().trim())
        } catch (NumberFormatException ignored) {
            println "Digite um número válido."
            return -1
        }
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                listarCandidatos()
                break

            case 2:
                listarEmpresas()
                break

            case 9:
                listarVagas()
                break

            case 13:
                listarCompetencias()
                break

            default:
                println "Esta opção ainda não foi implementada."
        }
    }

    private void listarCandidatos() {
        println "\n===== CANDIDATOS ====="

        List<Map> candidatos = bancoDeDados.listarCandidatos()

        if (!candidatos) {
            println "Nenhum candidato cadastrado."
            return
        }

        candidatos.each { candidato ->
            println """
ID: ${candidato.id}
Nome: ${candidato.nome}
Email: ${candidato.email}
CPF: ${candidato.cpf}
Idade: ${candidato.idade}
Estado: ${candidato.estado}
CEP: ${candidato.cep}
Descrição: ${candidato.descricao}
------------------------------
"""
        }
    }

    private void listarEmpresas() {
        println "\n===== EMPRESAS ====="

        List<Map> empresas = bancoDeDados.listarEmpresas()

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        empresas.each { empresa ->
            println """
ID: ${empresa.id}
Nome: ${empresa.nome}
Email: ${empresa.email}
CNPJ: ${empresa.cnpj}
Estado: ${empresa.estado}
CEP: ${empresa.cep}
Descrição: ${empresa.descricao}
------------------------------
"""
        }
    }

    private void listarVagas() {
        println "\n===== VAGAS ====="

        List<Map> vagas = bancoDeDados.listarVagas()

        if (!vagas) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.each { vaga ->
            println """
ID: ${vaga.id}
Título: ${vaga.titulo}
Descrição: ${vaga.descricao}
Localidade: ${vaga.localidade}
Salário: ${vaga.salario}
Empresa: ${vaga.empresa ?: vaga.id_empresa}
------------------------------
"""
        }
    }

    private void listarCompetencias() {
        println "\n===== COMPETÊNCIAS ====="

        List<Map> competencias = bancoDeDados.listarCompetencias()

        if (!competencias) {
            println "Nenhuma competência cadastrada."
            return
        }

        competencias.each { competencia ->
            println """
ID: ${competencia.id}
Nome: ${competencia.nome}
------------------------------
"""
        }
    }

    private void aguardarContinuacao() {
        println "\nPressione Enter para continuar..."
        scanner.nextLine()
    }
}
