/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */


import java.util.Scanner

class Main {

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)
        BancoDeDados banco = new BancoDeDados()
        int opcao = -1

        while (opcao != 0) {
            println """
===== LINKETINDER =====
1 - Listar candidatos
2 - Listar empresas
3 - Cadastrar candidato
4 - Atualizar candidato
5 - Excluir candidato
6 - Cadastrar empresa
7 - Atualizar empresa
8 - Excluir empresa
0 - Sair
"""
            print "Escolha uma opção: "

            String entrada = scanner.nextLine().trim()

            try {
                opcao = Integer.parseInt(entrada)

                switch (opcao) {
                    case 1:
                        listarCandidatos(banco)
                        break

                    case 2:
                        listarEmpresas(banco)
                        break

                    case 3:
                        cadastrarCandidato(scanner, banco)
                        break

                    case 4:
                        atualizarCandidato(scanner, banco)
                        break

                    case 5:
                        excluirCandidato(scanner, banco)
                        break

                    case 6:
                        cadastrarEmpresa(scanner, banco)
                        break

                    case 7:
                        atualizarEmpresa(scanner, banco)
                        break

                    case 8:
                        excluirEmpresa(scanner, banco)
                        break

                    case 0:
                        println "Encerrando o Linketinder..."
                        break

                    default:
                        println "Opção inválida!"
                }
            } catch (NumberFormatException e) {
                println "Digite um número válido."
            } catch (Exception e) {
                println "Erro: ${e.message}"
            }

            if (opcao != 0) {
                println "\nPressione Enter para continuar..."
                scanner.nextLine()
            }
        }

        scanner.close()
    }

    // LISTAR CANDIDATOS
    static void listarCandidatos(BancoDeDados banco) {
        println "\n===== CANDIDATOS ====="

        def candidatos = banco.listarCandidatos()

        if (!candidatos) {
            println "Nenhum candidato cadastrado."
            return
        }

        candidatos.each { candidato ->
            println """
ID: ${candidato.id}
Nome: ${candidato.nome} ${candidato.sobrenome}
E-mail: ${candidato.email}
CPF: ${candidato.cpf}
País: ${candidato.pais}
CEP: ${candidato.cep}
Descrição: ${candidato.descricao}
Competências: ${candidato.competencias}
-----------------------------
"""

        }
    }

    // LISTAR EMPRESAS
    static void listarEmpresas(BancoDeDados banco) {
        println "\n===== EMPRESAS ====="

        def empresas = banco.listarEmpresas()

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        empresas.each { empresa ->
            println """
ID: ${empresa.id}
Nome: ${empresa.nome}
CNPJ: ${empresa.cnpj}
E-mail: ${empresa.email}
País: ${empresa.pais}
CEP: ${empresa.cep}
Descrição: ${empresa.descricao}
-----------------------------
"""
        }
    }

    // CADASTRAR CANDIDATO
    static void cadastrarCandidato(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== CADASTRAR CANDIDATO ====="

        Map candidato = [
                nome           : perguntar(scanner, "Nome: "),
                sobrenome      : perguntar(scanner, "Sobrenome: "),
                data_nascimento: perguntar(
                        scanner,
                        "Data de nascimento (AAAA-MM-DD): "
                ),
                email          : perguntar(scanner, "E-mail: "),
                cpf            : perguntar(scanner, "CPF: "),
                pais           : perguntar(scanner, "País: "),
                cep            : perguntar(scanner, "CEP: "),
                descricao      : perguntar(scanner, "Descrição: "),
                senha_hash     : perguntar(scanner, "Senha ou hash da senha: ")
        ]

        List<String> competencias = lerCompetencias(scanner)

        banco.criarCandidato(candidato, competencias)

        println "Candidato cadastrado com sucesso!"
    }

    // ATUALIZAR CANDIDATO
    static void atualizarCandidato(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== ATUALIZAR CANDIDATO ====="

        long id = perguntar(scanner, "ID do candidato: ").toLong()

        Map candidato = [
                nome           : perguntar(scanner, "Novo nome: "),
                sobrenome      : perguntar(scanner, "Novo sobrenome: "),
                data_nascimento: perguntar(
                        scanner,
                        "Data de nascimento (AAAA-MM-DD): "
                ),
                email          : perguntar(scanner, "Novo e-mail: "),
                cpf            : perguntar(scanner, "Novo CPF: "),
                pais           : perguntar(scanner, "País: "),
                cep            : perguntar(scanner, "CEP: "),
                descricao      : perguntar(scanner, "Descrição: "),
                senha_hash     : perguntar(scanner, "Senha ou hash da senha: ")
        ]

        List<String> competencias = lerCompetencias(scanner)

        boolean atualizado = banco.atualizarCandidato(
                id, candidato, competencias
        )

        if (atualizado) {
            println "Candidato atualizado com sucesso!"
        } else {
            println "Nenhum candidato encontrado com o ID ${id}."
        }

        println "Solicitação de atualização concluída."
    }

    // EXCLUIR CANDIDATO
    static void excluirCandidato(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== EXCLUIR CANDIDATO ====="

        long id = perguntar(
                scanner,
                "ID do candidato que deseja excluir: "
        ).toLong()

        String confirmacao = perguntar(
                scanner,
                "Confirma a exclusão? (s/n): "
        )

        if (confirmacao.equalsIgnoreCase("s")) {
            banco.excluirCandidato(id)
            println "Solicitação de exclusão concluída."
        } else {
            println "Exclusão cancelada."
        }
    }

    // CADASTRAR EMPRESA
    static void cadastrarEmpresa(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== CADASTRAR EMPRESA ====="

        Map empresa = [
                nome      : perguntar(scanner, "Nome da empresa: "),
                cnpj      : perguntar(scanner, "CNPJ: "),
                email     : perguntar(scanner, "E-mail: "),
                descricao : perguntar(scanner, "Descrição: "),
                pais      : perguntar(scanner, "País: "),
                cep       : perguntar(scanner, "CEP: "),
                senha_hash: perguntar(scanner, "Senha ou hash da senha: ")
        ]

        banco.criarEmpresa(empresa)

        println "Empresa cadastrada com sucesso!"
    }

    // ATUALIZAR EMPRESA
    static void atualizarEmpresa(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== ATUALIZAR EMPRESA ====="

        long id = perguntar(scanner, "ID da empresa: ").toLong()

        Map empresa = [
                nome      : perguntar(scanner, "Novo nome: "),
                cnpj      : perguntar(scanner, "Novo CNPJ: "),
                email     : perguntar(scanner, "Novo e-mail: "),
                descricao : perguntar(scanner, "Nova descrição: "),
                pais      : perguntar(scanner, "País: "),
                cep       : perguntar(scanner, "CEP: "),
                senha_hash: perguntar(scanner, "Senha ou hash da senha: ")
        ]

        banco.atualizarEmpresa(id, empresa)

        println "Solicitação de atualização concluída."
    }

    // EXCLUIR EMPRESA
    static void excluirEmpresa(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== EXCLUIR EMPRESA ====="

        long id = perguntar(
                scanner,
                "ID da empresa que deseja excluir: "
        ).toLong()

        String confirmacao = perguntar(
                scanner,
                "Confirma a exclusão? (s/n): "
        )

        if (confirmacao.equalsIgnoreCase("s")) {
            banco.excluirEmpresa(id)
            println "Solicitação de exclusão concluída."
        } else {
            println "Exclusão cancelada."
        }
    }

    static String perguntar(
            Scanner scanner,
            String mensagem
    ) {
        print mensagem
        return scanner.nextLine().trim()
    }

    static List<String> lerCompetencias(Scanner scanner) {
        String entrada = perguntar(
                scanner,
                "Competências separadas por vírgula (ou Enter para nenhuma): "
        )

        if (!entrada) {
            return []
        }

        return entrada.split(",")
                .collect { it.trim() }
                .findAll { !it.isEmpty() }
                .unique()
    }
}
