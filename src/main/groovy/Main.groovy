
/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */

import java.util.Scanner

import database.ConexaoBanco

import repository.CandidatoRepository
import repository.EmpresaRepository
import repository.VagaRepository
import repository.CompetenciaRepository

class Main {

    static void main(String[] args) {

        Scanner scanner = new Scanner(System.in)

        try {
            iniciarAplicacao(scanner)

        } catch (Exception e) {
            println "Erro ao iniciar o Linketinder: ${e.message}"

        } finally {
            scanner.close()
        }
    }

    private static void iniciarAplicacao(Scanner scanner) {

        // Provedor de conexão
        ConexaoBanco conexaoBanco = new ConexaoBanco()

        // Repositórios
        CandidatoRepository candidatoRepository =
                new CandidatoRepository(conexaoBanco)

        EmpresaRepository empresaRepository =
                new EmpresaRepository(conexaoBanco)

        VagaRepository vagaRepository =
                new VagaRepository(conexaoBanco)

        CompetenciaRepository competenciaRepository =
                new CompetenciaRepository(conexaoBanco)

        // Fachada de acesso aos dados
        BancoDeDados bancoDeDados = new BancoDeDados(
                candidatoRepository,
                empresaRepository,
                vagaRepository,
                competenciaRepository
        )

        // Interface com o usuário
        Menu menu = new Menu(scanner, bancoDeDados)
        menu.executar()
    }
}
