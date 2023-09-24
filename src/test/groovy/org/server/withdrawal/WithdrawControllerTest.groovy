package org.server.withdrawal

import com.sun.net.httpserver.HttpExchange
import spock.lang.Shared
import spock.lang.Specification

class WithdrawControllerTest extends Specification {

    @Shared
    WithdrawController withdrawController

    HttpExchange httpExchange = Mock()
    WithdrawalCreator withdrawalCreator = Mock()

    ByteArrayOutputStream responseBody = new ByteArrayOutputStream()

    def setup() {
        withdrawController = new WithdrawController(withdrawalCreator)
    }

    def "should return 405 for non-POST methods"() {
        given:
        httpExchange.getRequestMethod() >> "GET"
        httpExchange.getResponseBody() >> responseBody

        when:
        withdrawController.handle(httpExchange)

        then:
        1 * httpExchange.sendResponseHeaders(405, _)
    }

    def "should process withdrawal request successfully"() {
        given:
        httpExchange.getRequestMethod() >> "POST"
        UUID validUUID = UUID.randomUUID()
        httpExchange.getRequestURI() >> URI.create("/withdraw?sourceUserID=${validUUID}&address=value2&amount=123.45")
        httpExchange.getResponseBody() >> responseBody

        UUID mockUUID = UUID.randomUUID()
        withdrawalCreator.createWithdrawal(validUUID, new WithdrawalService.Address("value2"), new BigDecimal("123.45")) >> mockUUID

        when:
        withdrawController.handle(httpExchange)

        then:
        1 * httpExchange.sendResponseHeaders(200, _)
        responseBody.toString().contains("Withdrawal request submitted successfully. Withdrawal ID: ${mockUUID}")
    }

    def "should handle withdrawal request error"() {
        given:
        httpExchange.getRequestMethod() >> "POST"
        UUID validUUID = UUID.randomUUID()
        httpExchange.getRequestURI() >> URI.create("/withdraw?sourceUserID=${validUUID}&address=value2&amount=123.45")
        httpExchange.getResponseBody() >> responseBody

        withdrawalCreator.createWithdrawal(validUUID, new WithdrawalService.Address("value2"), new BigDecimal("123.45")) >> { throw new Exception("mock exception") }

        when:
        withdrawController.handle(httpExchange)

        then:
        1 * httpExchange.sendResponseHeaders(500, _)
        responseBody.toString().contains("Withdrawal failed: mock exception")
    }

}
