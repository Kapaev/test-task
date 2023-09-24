package org.server;

import com.sun.net.httpserver.HttpServer;
import org.server.dao.Dao;
import org.server.dao.InMemoryDao;
import org.server.model.Transaction;
import org.server.model.User;
import org.server.processor.ProcessingService;
import org.server.status.OperationStatusController;
import org.server.withdrawal.*;
import org.server.transfer.TransactionCreator;
import org.server.transfer.TransactionService;
import org.server.transfer.TransferController;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

public class SimpleServer {

    private static HttpServer server;
    private static ProcessingService processingService;

    public SimpleServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        Dao dao = new InMemoryDao();
        UUID uuid_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID uuid_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

        dao.addUser(new User(uuid_1,"Bob", new BigDecimal("1000.0")));
        dao.addUser(new User(uuid_2,"Alice", new BigDecimal("1000.0")));

        TransactionService transactionService = new TransactionService(dao);
        ValidationWithdrawalService validationWithdrawalService = new ValidationWithdrawalService(dao);

        ProcessingService processingService = new ProcessingService(1000,
                Map.of(Transaction.class, transactionService,
                        WithdrawalRequest.class, validationWithdrawalService), dao);

        TransactionCreator transactionCreator = new TransactionCreator(dao, processingService);
        WithdrawalCreator withdrawalCreator = new WithdrawalCreator(dao, processingService);

        server.createContext("/transfer", new TransferController(transactionCreator));
        server.createContext("/withdraw", new WithdrawController(withdrawalCreator));
        server.createContext("/status", new OperationStatusController(dao));
        server.setExecutor(null);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

    }

    public void shutdown() throws InterruptedException {
        if (server != null) {
            server.stop(1);
        }

        if (processingService != null) {
            processingService.awaitTermination();
        }

        System.out.println("Server shut down gracefully");
    }

    public static void main(String[] args) throws IOException {
        new SimpleServer(9876);

    }

}