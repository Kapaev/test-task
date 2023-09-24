package org.server.transfer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import static util.HttpUtil.*;

public class TransferController implements HttpHandler {

    private final TransactionCreator transactionCreator;

    public TransferController(TransactionCreator transactionCreator) {
        this.transactionCreator = transactionCreator;
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
        UUID sourceUserID = UUID.fromString(getQueryParam(exchange, 0));
        UUID destinationUserID = UUID.fromString(getQueryParam(exchange, 1));
        BigDecimal amount = new BigDecimal(getQueryParam(exchange, 2));

        try {
            UUID transactionId = transactionCreator.createTransaction(sourceUserID, destinationUserID, amount);
            String response = "Transfer registered. Transaction ID: " + transactionId;
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Transfer failed: " + e.getMessage());
        }
    }

}
