package view

/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */

import controller.CandidatoController
import controller.EmpresaController
import controller.VagaController
import controller.CompetenciaController

class Menu {

    private final Scanner scanner
    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final VagaController vagaController
    private final CompetenciaController competenciaController

    Menu(
            Scanner scanner,
            CandidatoController candidatoController,
            EmpresaController empresaController,
            VagaController vagaController,
            CompetenciaController competenciaController
    ) {
        this.scanner = scanner
        this.candidatoController = candidatoController
        this.empresaController = empresaController
        this.vagaController = vagaController
        this.competenciaController = competenciaController
    }

    void executar() {
        int opcao = -1

        while (opcao != 0) {
            exibirMenu()
            opcao = lerInteiro("Escolha uma opção: ")

            if (opcao == 0) {
                break
            }

            try {
                executarOpcao(opcao)
            } catch (Exception e) {
                println "\nNão foi possível executar a operação."
                println "Detalhes: ${e.message}"
            }

            aguardarContinuacao()
        }

        println "\nEncerrando o Linketinder..."
    }

    private void exibirMenu() {
        println """
========== LINKETINDER ==========

--- CANDIDATOS ---
1  - Listar candidatos
2  - Cadastrar candidato
3  - Atualizar candidato
4  - Excluir candidato

--- EMPRESAS ---
5  - Listar empresas
6  - Cadastrar empresa
7  - Atualizar empresa
8  - Excluir empresa

--- VAGAS ---
9  - Listar vagas
10 - Cadastrar vaga
11 - Atualizar vaga
12 - Excluir vaga

--- COMPETÊNCIAS ---
13 - Listar competências
14 - Cadastrar competência
15 - Atualizar competência
16 - Excluir competência

0 - Sair
"""
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1: listarCandidatos(); break
            case 2: cadastrarCandidato(); break
            case 3: atualizarCandidato(); break
            case 4: excluirCandidato(); break

            case 5: listarEmpresas(); break
            case 6: cadastrarEmpresa(); break
            case 7: atualizarEmpresa(); break
            case 8: excluirEmpresa(); break

            case 9: listarVagas(); break
            case 10: cadastrarVaga(); break
            case 11: atualizarVaga(); break
            case 12: excluirVaga(); break

            case 13: listarCompetencias(); break
            case 14: cadastrarCompetencia(); break
            case 15: atualizarCompetencia(); break
            case 16: excluirCompetencia(); break

            default: println "Opção inválida."
        }
    }

    // CANDIDATOS

    private void listarCandidatos() {
        println "\n========== CANDIDATOS =========="

        List<Map> candidatos = candidatoController.listar()

        if (!candidatos) {
            println "Nenhum candidato cadastrado."
            return
        }

        candidatos.each { candidato ->
            println """
ID: ${candidato.id}
Nome: ${candidato.nome} ${candidato.sobrenome}
Data de nascimento: ${candidato.data_nascimento}
Idade: ${candidato.idade}
Email: ${candidato.email}
CPF: ${candidato.cpf}
País: ${candidato.pais}
CEP: ${candidato.cep}
Descrição: ${candidato.descricao}
Competências: ${candidato.competencias}
--------------------------------
"""
        }
    }

    private void cadastrarCandidato() {
        println "\n========== CADASTRAR CANDIDATO =========="

        Map candidato = [
                nome           : lerTexto("Nome: "),
                sobrenome      : lerTexto("Sobrenome: "),
                data_nascimento: lerTexto("Data de nascimento (AAAA-MM-DD): "),
                email          : lerTexto("Email: "),
                cpf            : lerTexto("CPF: "),
                pais           : lerTexto("País: "),
                cep            : lerTexto("CEP: "),
                descricao      : lerTexto("Descrição: ")
        ]

        List<String> competencias = lerCompetencias()

        Long id = candidatoController.cadastrar(candidato, competencias)

        println "\nCandidato cadastrado com sucesso! ID: ${id}"
    }

    private void atualizarCandidato() {
        println "\n========== ATUALIZAR CANDIDATO =========="

        Long id = lerLong("ID do candidato: ")
        Map existente = candidatoController.buscarPorId(id)

        if (!existente) {
            println "Candidato não encontrado."
            return
        }

        Map candidato = [
                nome           : lerTextoAtualizavel("Nome", existente.nome),
                sobrenome      : lerTextoAtualizavel("Sobrenome", existente.sobrenome),
                data_nascimento: lerTextoAtualizavel("Data de nascimento", existente.data_nascimento),
                email          : lerTextoAtualizavel("Email", existente.email),
                cpf            : lerTextoAtualizavel("CPF", existente.cpf),
                pais           : lerTextoAtualizavel("País", existente.pais),
                cep            : lerTextoAtualizavel("CEP", existente.cep),
                descricao      : lerTextoAtualizavel("Descrição", existente.descricao)
        ]

        println "Competências atuais: ${existente.competencias}"
        println "Digite as novas competências ou pressione Enter para manter."

        String entradaCompetencias = lerTexto("Competências: ")

        List<String> competencias = entradaCompetencias ?
                converterCompetencias(entradaCompetencias) :
                converterCompetenciasExistentes(existente.competencias)

        boolean atualizado = candidatoController.atualizar(
                id,
                candidato,
                competencias
        )

        println atualizado ?
                "Candidato atualizado com sucesso." :
                "Não foi possível atualizar o candidato."
    }

    private void excluirCandidato() {
        println "\n========== EXCLUIR CANDIDATO =========="

        Long id = lerLong("ID do candidato: ")
        Map candidato = candidatoController.buscarPorId(id)

        if (!candidato) {
            println "Candidato não encontrado."
            return
        }

        println "Candidato: ${candidato.nome} ${candidato.sobrenome}"

        if (!confirmar("Deseja realmente excluir?")) {
            println "Operação cancelada."
            return
        }

        boolean excluido = candidatoController.excluir(id)

        println excluido ?
                "Candidato excluído com sucesso." :
                "Não foi possível excluir o candidato."
    }

    // EMPRESAS

    private void listarEmpresas() {
        println "\n========== EMPRESAS =========="

        List<Map> empresas = empresaController.listar()

        if (!empresas) {
            println "Nenhuma empresa cadastrada."
            return
        }

        empresas.each { empresa ->
            println """
ID: ${empresa.id}
Nome: ${empresa.nome}
CNPJ: ${empresa.cnpj}
Email: ${empresa.email}
País: ${empresa.pais}
CEP: ${empresa.cep}
Descrição: ${empresa.descricao}
--------------------------------
"""
        }
    }

    private void cadastrarEmpresa() {
        println "\n========== CADASTRAR EMPRESA =========="

        Map empresa = [
                nome     : lerTexto("Nome: "),
                cnpj     : lerTexto("CNPJ: "),
                email    : lerTexto("Email: "),
                descricao: lerTexto("Descrição: "),
                pais     : lerTexto("País: "),
                cep      : lerTexto("CEP: ")
        ]

        Long id = empresaController.cadastrar(empresa)

        println "\nEmpresa cadastrada com sucesso! ID: ${id}"
    }

    private void atualizarEmpresa() {
        println "\n========== ATUALIZAR EMPRESA =========="

        Long id = lerLong("ID da empresa: ")
        Map existente = empresaController.buscarPorId(id)

        if (!existente) {
            println "Empresa não encontrada."
            return
        }

        Map empresa = [
                nome     : lerTextoAtualizavel("Nome", existente.nome),
                cnpj     : lerTextoAtualizavel("CNPJ", existente.cnpj),
                email    : lerTextoAtualizavel("Email", existente.email),
                descricao: lerTextoAtualizavel("Descrição", existente.descricao),
                pais     : lerTextoAtualizavel("País", existente.pais),
                cep      : lerTextoAtualizavel("CEP", existente.cep)
        ]

        boolean atualizado = empresaController.atualizar(id, empresa)

        println atualizado ?
                "Empresa atualizada com sucesso." :
                "Não foi possível atualizar a empresa."
    }

    private void excluirEmpresa() {
        println "\n========== EXCLUIR EMPRESA =========="

        Long id = lerLong("ID da empresa: ")
        Map empresa = empresaController.buscarPorId(id)

        if (!empresa) {
            println "Empresa não encontrada."
            return
        }

        println "Empresa: ${empresa.nome}"

        if (!confirmar("Deseja realmente excluir?")) {
            println "Operação cancelada."
            return
        }

        boolean excluida = empresaController.excluir(id)

        println excluida ?
                "Empresa excluída com sucesso." :
                "Não foi possível excluir a empresa."
    }

    // VAGAS

    private void listarVagas() {
        println "\n========== VAGAS =========="

        List<Map> vagas = vagaController.listar()

        if (!vagas) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.each { vaga ->
            println """
ID: ${vaga.id}
Nome: ${vaga.nome}
Empresa: ${vaga.empresa}
ID da empresa: ${vaga.empresa_id}
Descrição: ${vaga.descricao}
Cidade: ${vaga.cidade}
Estado: ${vaga.estado}
Ativa: ${vaga.ativa}
Competências: ${vaga.competencias}
--------------------------------
"""
        }
    }

    private void cadastrarVaga() {
        println "\n========== CADASTRAR VAGA =========="

        Long empresaId = lerLong("ID da empresa responsável: ")
        Map empresa = empresaController.buscarPorId(empresaId)

        if (!empresa) {
            println "Empresa não encontrada."
            return
        }

        Map vaga = [
                empresa_id: empresaId,
                nome      : lerTexto("Nome da vaga: "),
                descricao : lerTexto("Descrição: "),
                cidade    : lerTexto("Cidade: "),
                estado    : lerTexto("Estado: "),
                ativa     : lerBooleano("A vaga está ativa? (S/N): ")
        ]

        List<String> competencias = lerCompetencias()

        Long id = vagaController.cadastrar(vaga, competencias)

        println "\nVaga cadastrada com sucesso! ID: ${id}"
    }

    private void atualizarVaga() {
        println "\n========== ATUALIZAR VAGA =========="

        Long id = lerLong("ID da vaga: ")
        Map existente = vagaController.buscarPorId(id)

        if (!existente) {
            println "Vaga não encontrada."
            return
        }

        Long empresaId = lerLongAtualizavel(
                "ID da empresa responsável",
                existente.empresa_id
        )

        Map empresa = empresaController.buscarPorId(empresaId)

        if (!empresa) {
            println "Empresa não encontrada."
            return
        }

        Map vaga = [
                empresa_id: empresaId,
                nome      : lerTextoAtualizavel("Nome", existente.nome),
                descricao : lerTextoAtualizavel("Descrição", existente.descricao),
                cidade    : lerTextoAtualizavel("Cidade", existente.cidade),
                estado    : lerTextoAtualizavel("Estado", existente.estado),
                ativa     : lerBooleanoAtualizavel(existente.ativa)
        ]

        println "Competências atuais: ${existente.competencias}"
        println "Digite as novas competências ou pressione Enter para manter."

        String entradaCompetencias = lerTexto("Competências: ")

        List<String> competencias = entradaCompetencias ?
                converterCompetencias(entradaCompetencias) :
                converterCompetenciasExistentes(existente.competencias)

        boolean atualizado = vagaController.atualizar(
                id,
                vaga,
                competencias
        )

        println atualizado ?
                "Vaga atualizada com sucesso." :
                "Não foi possível atualizar a vaga."
    }

    private void excluirVaga() {
        println "\n========== EXCLUIR VAGA =========="

        Long id = lerLong("ID da vaga: ")
        Map vaga = vagaController.buscarPorId(id)

        if (!vaga) {
            println "Vaga não encontrada."
            return
        }

        println "Vaga: ${vaga.nome}"

        if (!confirmar("Deseja realmente excluir?")) {
            println "Operação cancelada."
            return
        }

        boolean excluida = vagaController.excluir(id)

        println excluida ?
                "Vaga excluída com sucesso." :
                "Não foi possível excluir a vaga."
    }

    // COMPETÊNCIAS

    private void listarCompetencias() {
        println "\n========== COMPETÊNCIAS =========="

        List<Map> competencias = competenciaController.listar()

        if (!competencias) {
            println "Nenhuma competência cadastrada."
            return
        }

        competencias.each { competencia ->
            println """
ID: ${competencia.id}
Nome: ${competencia.nome}
--------------------------------
"""
        }
    }

    private void cadastrarCompetencia() {
        println "\n========== CADASTRAR COMPETÊNCIA =========="

        String nome = lerTexto("Nome da competência: ")

        Long id = competenciaController.cadastrar(nome)

        println "\nCompetência cadastrada com sucesso! ID: ${id}"
    }

    private void atualizarCompetencia() {
        println "\n========== ATUALIZAR COMPETÊNCIA =========="

        Long id = lerLong("ID da competência: ")
        Map existente = competenciaController.buscarPorId(id)

        if (!existente) {
            println "Competência não encontrada."
            return
        }

        String nome = lerTextoAtualizavel("Nome", existente.nome)

        boolean atualizado = competenciaController.atualizar(id, nome)

        println atualizado ?
                "Competência atualizada com sucesso." :
                "Não foi possível atualizar a competência."
    }

    private void excluirCompetencia() {
        println "\n========== EXCLUIR COMPETÊNCIA =========="

        Long id = lerLong("ID da competência: ")
        Map competencia = competenciaController.buscarPorId(id)

        if (!competencia) {
            println "Competência não encontrada."
            return
        }

        println "Competência: ${competencia.nome}"

        if (!confirmar("Deseja realmente excluir?")) {
            println "Operação cancelada."
            return
        }

        boolean excluida = competenciaController.excluir(id)

        println excluida ?
                "Competência excluída com sucesso." :
                "Não foi possível excluir a competência."
    }

    // MÉTODOS AUXILIARES

    private String lerTexto(String mensagem) {
        print mensagem
        return scanner.nextLine().trim()
    }

    private String lerTextoAtualizavel(String campo, Object valorAtual) {
        print "${campo} [${valorAtual}]: "
        String valor = scanner.nextLine().trim()

        return valor ? valor : valorAtual?.toString()
    }

    private Integer lerInteiro(String mensagem) {
        while (true) {
            String valor = lerTexto(mensagem)

            try {
                return Integer.parseInt(valor)
            } catch (NumberFormatException ignored) {
                println "Digite um número inteiro válido."
            }
        }
    }

    private Long lerLong(String mensagem) {
        while (true) {
            String valor = lerTexto(mensagem)

            try {
                return Long.parseLong(valor)
            } catch (NumberFormatException ignored) {
                println "Digite um ID numérico válido."
            }
        }
    }

    private Long lerLongAtualizavel(String mensagem, Object valorAtual) {
        while (true) {
            String valor = lerTexto("${mensagem} [${valorAtual}]: ")

            if (!valor) {
                return valorAtual as Long
            }

            try {
                return Long.parseLong(valor)
            } catch (NumberFormatException ignored) {
                println "Digite um ID numérico válido."
            }
        }
    }

    private Boolean lerBooleano(String mensagem) {
        while (true) {
            String valor = lerTexto(mensagem).toUpperCase()

            if (valor == "S") return true
            if (valor == "N") return false

            println "Digite S para sim ou N para não."
        }
    }

    private Boolean lerBooleanoAtualizavel(Boolean valorAtual) {
        while (true) {
            String valor = lerTexto(
                    "A vaga está ativa? (S/N) [${valorAtual ? 'S' : 'N'}]: "
            ).toUpperCase()

            if (!valor) return valorAtual
            if (valor == "S") return true
            if (valor == "N") return false

            println "Digite S, N ou pressione Enter para manter."
        }
    }

    private List<String> lerCompetencias() {
        println "Digite as competências separadas por vírgula."
        println "Exemplo: Java, Python, SQL"

        String entrada = lerTexto("Competências: ")

        return converterCompetencias(entrada)
    }

    private List<String> converterCompetencias(String entrada) {
        if (!entrada) {
            return []
        }

        return entrada.split(",")
                .collect { it.trim() }
                .findAll { !it.isEmpty() }
                .unique { it.toLowerCase() }
    }

    private List<String> converterCompetenciasExistentes(Object existentes) {
        if (existentes == null) {
            return []
        }

        if (existentes instanceof Collection) {
            return existentes.collect { it.toString() }
        }

        return existentes.toString()
                .replaceAll(/^\[|\]$/, "")
                .split(",")
                .collect { it.trim() }
                .findAll { !it.isEmpty() }
    }

    private boolean confirmar(String mensagem) {
        while (true) {
            String resposta = lerTexto("${mensagem} (S/N): ").toUpperCase()

            if (resposta == "S") return true
            if (resposta == "N") return false

            println "Digite S para sim ou N para não."
        }
    }

    private void aguardarContinuacao() {
        println "\nPressione Enter para continuar..."
        scanner.nextLine()
    }
}
