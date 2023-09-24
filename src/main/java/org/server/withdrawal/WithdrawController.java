package org.server.withdrawal;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static org.server.withdrawal.WithdrawalService.*;
import static util.HttpUtil.*;

public class WithdrawController implements HttpHandler {

    private final WithdrawalCreator withdrawalCreator;

    public WithdrawController(WithdrawalCreator withdrawalCreator) {
        this.withdrawalCreator = withdrawalCreator;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePostRequest(exchange);
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            UUID sourceUserID = UUID.fromString(getQueryParam(exchange, 0));
            Address address = new Address(getQueryParam(exchange, 1));
            BigDecimal amount = new BigDecimal(getQueryParam(exchange, 2));

            UUID id = withdrawalCreator.createWithdrawal(sourceUserID, address, amount);
            String response = "Withdrawal request submitted successfully. Withdrawal ID: " + id;
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Withdrawal failed: " + e.getMessage());
        }
    }
}

