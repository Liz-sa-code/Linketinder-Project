
package api.handler

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.json.JsonException

import java.nio.charset.StandardCharsets
import java.util.Locale

abstract class JsonRequestHandler implements HttpHandler {

    @Override
    void handle(HttpExchange exchange) {

        try {
            if (exchange.requestMethod != 'POST') {
                exchange.responseHeaders.set('Allow', 'POST')

                responder(exchange, 405, [
                        erro: 'Método HTTP não permitido.'
                ])
                return
            }

            String contentType = exchange.requestHeaders
                    .getFirst('Content-Type')

            if (contentType == null ||
                    !contentType.toLowerCase(Locale.ROOT)
                            .startsWith('application/json')) {

                responder(exchange, 415, [
                        erro: 'O Content-Type deve ser application/json.'
                ])
                return
            }

            int status = 201
            Map resposta

            try {
                def conteudo = new JsonSlurper().parse(
                        exchange.requestBody
                )

                if (!(conteudo instanceof Map)) {
                    throw new IllegalArgumentException(
                            'O corpo da requisição deve ser um objeto JSON.'
                    )
                }

                resposta = executar(conteudo as Map)

            } catch (JsonException | IllegalArgumentException e) {

                status = 400
                resposta = [
                        erro: e.message ?: 'Requisição inválida.'
                ]

            } catch (Exception e) {

                status = 500
                resposta = [
                        erro: 'Erro interno do servidor.'
                ]

                e.printStackTrace()
            }

            responder(exchange, status, resposta)

        } finally {
            exchange.close()
        }
    }

    protected abstract Map executar(Map dados)

    private void responder(
            HttpExchange exchange,
            int status,
            Map resposta
    ) {
        byte[] conteudo = JsonOutput.toJson(resposta)
                .getBytes(StandardCharsets.UTF_8)

        exchange.responseHeaders.set(
                'Content-Type',
                'application/json; charset=UTF-8'
        )

        exchange.sendResponseHeaders(status, conteudo.length)

        exchange.responseBody.write(conteudo)
        exchange.responseBody.close()
    }
}
