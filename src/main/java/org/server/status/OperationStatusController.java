package org.server.status;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.server.dao.Dao;
import org.server.model.Status;

import java.io.IOException;
import java.util.UUID;

import static util.HttpUtil.getQueryParam;
import static util.HttpUtil.sendResponse;

public class OperationStatusController implements HttpHandler {

    private final Dao dao;

    public OperationStatusController(Dao dao) {
        this.dao = dao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            String operationType = getQueryParam(exchange, 0);
            UUID operationId = UUID.fromString(getQueryParam(exchange, 1));

            String response = determineResponse(operationType, operationId);

            if (response == null) {
                sendResponse(exchange, 400, "Invalid operation type.");
                return;
            }

            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Server error: " + e.getMessage());
        }
    }

    private String determineResponse(String operationType, UUID operationId) {
        if ("withdrawal".equalsIgnoreCase(operationType)) {
            Status state = dao.getWithdrawalRequestState(operationId);
            return "Withdrawal status: " + state.name();
        } else if ("transfer".equalsIgnoreCase(operationType)) {
            Status status = dao.getTransactionStatus(operationId);
            return "Transfer status: " + status;
        } else {
            return null;
        }
    }

}
