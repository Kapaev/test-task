package org.server


import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Stepwise
class SimpleServerIntegrationSpec extends Specification {


    @Shared
    SimpleServer server

    @Shared
    HttpClient client

    @Shared
    UUID userUUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001")

    @Shared
    UUID userUUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002")

    def setupSpec() {
        server = new SimpleServer(9876)
        client = HttpClient.newHttpClient()
    }

    def "Should handle transfer request successfully even with incorrect user UUID"() {
        given: "A POST request to /transfer endpoint with valid parameters"
        UUID sourceUserID = UUID.randomUUID()
        UUID destinationUserID = UUID.randomUUID()
        BigDecimal amount = new BigDecimal(100)

        URI uri = new URI("http://localhost:9876/transfer?sourceUserID=${sourceUserID}&destinationUserID=${destinationUserID}&amount=${amount}")
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()

        when: "We send the request with random user UUIDs"
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString())

        then: "We get a 200 OK response and a transaction ID"
        response.statusCode() == 200
        response.body().startsWith("Transfer registered. Transaction ID:")

        when: "We send status request for the transaction ID"
        def transactionId = response.body().split(":")[1].trim()
        uri = new URI("http://localhost:9876/status?operationType=transfer&operationId=${transactionId}")
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build()
        response = client.send(request, HttpResponse.BodyHandlers.ofString())

        then:
        response.statusCode() == 200
        response.body().startsWith("Transfer status: FAILED")

        when: "we send request for existent users"
        uri = new URI("http://localhost:9876/transfer?sourceUserID=${userUUID_1}&destinationUserID=${userUUID_2}&amount=${amount}")
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build()
        response = client.send(request, HttpResponse.BodyHandlers.ofString())

        then: "we get a 200 OK response and a transaction ID"
        response.statusCode() == 200
        response.body().startsWith("Transfer registered. Transaction ID:")

        when: "We send status request for the transaction ID"
        transactionId = response.body().split(":")[1].trim()
        uri = new URI("http://localhost:9876/status?operationType=transfer&operationId=${transactionId}")
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build()
        response = client.send(request, HttpResponse.BodyHandlers.ofString())

        then:
        response.statusCode() == 200
        response.body().startsWith("Transfer status: COMPLETED")
    }


    def cleanupSpec() {
        server?.shutdown()
    }
}