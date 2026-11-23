
package api

import com.sun.net.httpserver.HttpServer

import controller.CandidatoController
import controller.EmpresaController
import controller.RecrutadorController
import controller.VagaController

import api.handler.CandidatoHandler
import api.handler.EmpresaHandler
import api.handler.VagaHandler
import api.handler.RecrutamentoApiHandler
import service.AuthService

import java.net.InetSocketAddress
import java.net.InetAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ApiServer {

    private final CandidatoController candidatoController
    private final EmpresaController empresaController
    private final VagaController vagaController
    private final RecrutadorController recrutadorController
    private final AuthService authService

    private HttpServer servidor
    private ExecutorService executor

    ApiServer(
            CandidatoController candidatoController,
            EmpresaController empresaController,
            VagaController vagaController,
            RecrutadorController recrutadorController,
            AuthService authService
    ) {
        this.candidatoController = Objects.requireNonNull(candidatoController)
        this.empresaController = Objects.requireNonNull(empresaController)
        this.vagaController = Objects.requireNonNull(vagaController)
        this.recrutadorController =
                Objects.requireNonNull(recrutadorController)
        this.authService = Objects.requireNonNull(authService)
    }

    void iniciar() {

        if (servidor != null) {
            throw new IllegalStateException(
                    'O servidor já foi iniciado.'
            )
        }

        servidor = HttpServer.create(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), 8080),
                0
        )

        executor = Executors.newFixedThreadPool(4)
        servidor.setExecutor(executor)

        servidor.createContext(
                '/candidatos',
                new CandidatoHandler(candidatoController)
        )

        servidor.createContext(
                '/empresas',
                new EmpresaHandler(empresaController)
        )

        servidor.createContext(
                '/vagas',
                new VagaHandler(vagaController, authService)
        )

        servidor.createContext(
                '/api',
                new RecrutamentoApiHandler(
                        authService,
                        candidatoController,
                        empresaController,
                        recrutadorController,
                        vagaController
                )
        )

        servidor.start()

        println '======================================'
        println '       LINKETINDER - API REST'
        println '======================================'
        println 'Servidor iniciado em: http://localhost:8080'
        println ''
        println 'Endpoints disponíveis:'
        println 'POST http://localhost:8080/candidatos'
        println 'POST http://localhost:8080/empresas'
        println 'POST http://localhost:8080/vagas'
        println 'API da funcionalidade: http://localhost:8080/api'
    }

    void parar() {
        servidor?.stop(0)
        executor?.shutdown()

        servidor = null
        executor = null

        println 'Servidor da API encerrado.'
    }
}
