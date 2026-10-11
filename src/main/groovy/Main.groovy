
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

                    case 9:
                        listarVagas(banco)
                        break

                    case 10:
                        cadastrarVaga(scanner, banco)
                        break

                    case 11:
                        atualizarVaga(scanner, banco)
                        break

                    case 12:
                        excluirVaga(scanner, banco)
                        break

                    case 13:
                        listarCompetencias(banco)
                        break

                    case 14:
                        cadastrarCompetencia(scanner, banco)
                        break

                    case 15:
                        atualizarCompetencia(scanner, banco)
                        break

                    case 16:
                        excluirCompetencia(scanner, banco)
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

    // CANDIDATOS
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
Data de nascimento: ${candidato.data_nascimento}
E-mail: ${candidato.email}
CPF: ${candidato.cpf}
País: ${candidato.pais}
CEP: ${candidato.cep}
Descrição: ${candidato.descricao}
-----------------------------
"""
        }
    }

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
                id,
                candidato,
                competencias
        )

        if (atualizado) {
            println "Candidato atualizado com sucesso!"
        } else {
            println "Nenhum candidato encontrado com o ID ${id}."
        }
    }

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
            println "Solicitação de exclusão enviada."
        } else {
            println "Exclusão cancelada."
        }
    }

    // EMPRESAS

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

        println "Solicitação de atualização enviada."
    }

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
            println "Solicitação de exclusão enviada."
        } else {
            println "Exclusão cancelada."
        }
    }

    // VAGAS

    static void listarVagas(BancoDeDados banco) {
        println "\n===== VAGAS ====="

        def vagas = banco.listarVagas()

        if (!vagas) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.each { vaga ->
            println """
ID: ${vaga.id}
Empresa ID: ${vaga.empresa_id}
Nome: ${vaga.nome}
Descrição: ${vaga.descricao}
Cidade: ${vaga.cidade}
Estado: ${vaga.estado}
Ativa: ${vaga.ativa}
Competências: ${vaga.competencias}
-----------------------------
"""
        }
    }

    static void cadastrarVaga(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== CADASTRAR VAGA ====="

        Map vaga = [
                empresa_id: perguntar(
                        scanner,
                        "ID da empresa responsável: "
                ).toLong(),
                nome      : perguntar(scanner, "Nome da vaga: "),
                descricao : perguntar(scanner, "Descrição da vaga: "),
                cidade    : perguntar(scanner, "Cidade: "),
                estado    : perguntar(scanner, "Estado (UF): "),
                ativa     : true
        ]

        List<String> competencias = lerCompetencias(scanner)

        banco.criarVaga(vaga, competencias)

        println "Vaga cadastrada com sucesso!"
    }

    static void atualizarVaga(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== ATUALIZAR VAGA ====="

        long id = perguntar(scanner, "ID da vaga: ").toLong()

        Map vaga = [
                empresa_id: perguntar(
                        scanner,
                        "Novo ID da empresa responsável: "
                ).toLong(),
                nome      : perguntar(scanner, "Novo nome da vaga: "),
                descricao : perguntar(scanner, "Nova descrição: "),
                cidade    : perguntar(scanner, "Nova cidade: "),
                estado    : perguntar(scanner, "Novo estado (UF): "),
                ativa     : perguntar(
                        scanner,
                        "A vaga está ativa? (s/n): "
                ).equalsIgnoreCase("s")
        ]

        List<String> competencias = lerCompetencias(scanner)

        boolean atualizada = banco.atualizarVaga(
                id,
                vaga,
                competencias
        )

        if (atualizada) {
            println "Vaga atualizada com sucesso!"
        } else {
            println "Nenhuma vaga encontrada com o ID ${id}."
        }
    }

    static void excluirVaga(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== EXCLUIR VAGA ====="

        long id = perguntar(
                scanner,
                "ID da vaga que deseja excluir: "
        ).toLong()

        String confirmacao = perguntar(
                scanner,
                "Confirma a exclusão? (s/n): "
        )

        if (confirmacao.equalsIgnoreCase("s")) {
            banco.excluirVaga(id)
            println "Solicitação de exclusão enviada."
        } else {
            println "Exclusão cancelada."
        }
    }

    // COMPETÊNCIAS

    static void listarCompetencias(BancoDeDados banco) {
        println "\n===== COMPETÊNCIAS ====="

        def competencias = banco.listarCompetencias()

        if (!competencias) {
            println "Nenhuma competência cadastrada."
            return
        }

        competencias.each { competencia ->
            println """
ID: ${competencia.id}
Nome: ${competencia.nome}
-----------------------------
"""
        }
    }

    static void cadastrarCompetencia(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== CADASTRAR COMPETÊNCIA ====="

        String nome = perguntar(
                scanner,
                "Nome da competência: "
        )

        banco.criarCompetencia(nome)

        println "Competência cadastrada com sucesso!"
    }

    static void atualizarCompetencia(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== ATUALIZAR COMPETÊNCIA ====="

        long id = perguntar(
                scanner,
                "ID da competência: "
        ).toLong()

        String nome = perguntar(
                scanner,
                "Novo nome da competência: "
        )

        boolean atualizada = banco.atualizarCompetencia(id, nome)

        if (atualizada) {
            println "Competência atualizada com sucesso!"
        } else {
            println "Nenhuma competência encontrada com o ID ${id}."
        }
    }

    static void excluirCompetencia(
            Scanner scanner,
            BancoDeDados banco
    ) {
        println "\n===== EXCLUIR COMPETÊNCIA ====="

        long id = perguntar(
                scanner,
                "ID da competência que deseja excluir: "
        ).toLong()

        String confirmacao = perguntar(
                scanner,
                "Confirma a exclusão? (s/n): "
        )

        if (confirmacao.equalsIgnoreCase("s")) {
            banco.excluirCompetencia(id)
            println "Solicitação de exclusão enviada."
        } else {
            println "Exclusão cancelada."
        }
    }


    // MÉTODOS AUXILIARES

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
