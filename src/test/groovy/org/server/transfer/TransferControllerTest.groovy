package org.server.transfer

import com.sun.net.httpserver.HttpExchange
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class TransferControllerTest extends Specification {

    TransferController transferController
    TransactionCreator transactionCreator = Mock()
    HttpExchange httpExchange = Mock()
    UUID validUUID1 = UUID.randomUUID()
    UUID validUUID2 = UUID.randomUUID()
    ByteArrayInputStream requestBody = new ByteArrayInputStream(("sourceUUID=" + validUUID1 + "&destinationUUID=" + validUUID2 + "&amount=100.0").getBytes(StandardCharsets.UTF_8))
    ByteArrayOutputStream responseBody = new ByteArrayOutputStream()

    def setup() {
        transferController = new TransferController(transactionCreator)
    }

    def "should handle POST request successfully"() {
        given:
        UUID expectedUUID = UUID.randomUUID()
        httpExchange.getRequestURI() >> URI.create("/someEndpoint?sourceUUID=" + validUUID1 + "&destinationUUID=" + validUUID2 + "&amount=100.0")
        httpExchange.getRequestMethod() >> "POST"
        httpExchange.getRequestBody() >> requestBody
        httpExchange.getResponseBody() >> responseBody
        transactionCreator.createTransaction(_, _, _) >> expectedUUID

        when:
        transferController.handle(httpExchange)

        then:
        responseBody.toString() == "Transfer registered. Transaction ID: ${expectedUUID}"
    }

    def "should handle POST request with failure"() {
        given:
        httpExchange.getRequestURI() >> URI.create("/someEndpoint?sourceUUID=" + validUUID1 + "&destinationUUID=" + validUUID2 + "&amount=100.0")
        httpExchange.getRequestMethod() >> "POST"
        httpExchange.getRequestBody() >> requestBody
        httpExchange.getResponseBody() >> responseBody
        transactionCreator.createTransaction(_, _, _) >> { throw new Exception("Some error") }

        when:
        transferController.handle(httpExchange)

        then:
        responseBody.toString() == "Transfer failed: Some error"
    }

    def "should return 405 for non-POST methods"() {
        given:
        httpExchange.getRequestMethod() >> "GET"
        httpExchange.getRequestURI() >> URI.create("/someEndpoint")
        httpExchange.getResponseBody() >> responseBody
        httpExchange.sendResponseHeaders(405, -1)

        when:
        transferController.handle(httpExchange)

        then:
        1 * httpExchange.sendResponseHeaders(405, 18)
    }
}
