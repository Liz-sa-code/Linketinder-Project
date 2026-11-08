
/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */


import view.Menu
import database.ConnectionFactory
import database.ConnectionProvider

import repository.CandidatoRepository
import repository.EmpresaRepository
import repository.VagaRepository
import repository.CompetenciaRepository

import service.CandidatoService
import service.EmpresaService
import service.VagaService
import service.CompetenciaService

import controller.CandidatoController
import controller.EmpresaController
import controller.VagaController
import controller.CompetenciaController

class Main {

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)

        try {
            iniciarAplicacao(scanner)
        } catch (Exception e) {
            println "Erro ao iniciar o Linketinder: ${e.message}"
            e.printStackTrace()
        } finally {
            scanner.close()
        }
    }

    private static void iniciarAplicacao(Scanner scanner) {

        ConnectionFactory fabrica = ConnectionFactory.getInstancia()
        ConnectionProvider conexaoBanco = fabrica.criar('postgresql')

        // REPOSITORIES
        CandidatoRepository candidatoRepository =
                new CandidatoRepository(conexaoBanco)

        EmpresaRepository empresaRepository =
                new EmpresaRepository(conexaoBanco)

        VagaRepository vagaRepository =
                new VagaRepository(conexaoBanco)

        CompetenciaRepository competenciaRepository =
                new CompetenciaRepository(conexaoBanco)

        // SERVICES
        CandidatoService candidatoService =
                new CandidatoService(candidatoRepository)

        EmpresaService empresaService =
                new EmpresaService(empresaRepository)

        VagaService vagaService =
                new VagaService(vagaRepository)

        CompetenciaService competenciaService =
                new CompetenciaService(competenciaRepository)

        // CONTROLLERS
        CandidatoController candidatoController =
                new CandidatoController(candidatoService)

        EmpresaController empresaController =
                new EmpresaController(empresaService)

        VagaController vagaController =
                new VagaController(vagaService)

        CompetenciaController competenciaController =
                new CompetenciaController(competenciaService)

        // VIEW
        Menu menu = new Menu(
                scanner,
                candidatoController,
                empresaController,
                vagaController,
                competenciaController
        )

        menu.executar()
    }
}
