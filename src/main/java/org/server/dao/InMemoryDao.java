package org.server.dao;

import org.server.model.Status;
import org.server.model.Transaction;
import org.server.model.User;
import org.server.withdrawal.WithdrawalRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDao implements Dao {

    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();
    private final Map<UUID, WithdrawalRequest> withdrawals = new ConcurrentHashMap<>();

    private final Set<Object> unfinishedOperations = ConcurrentHashMap.newKeySet();


    public void addUser(User user) {
        users.put(user.getUserID(), user);
    }

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTransactionID(), transaction);
    }

    public User getUser(UUID userID) {
        return users.get(userID);
    }

    public Transaction getTransaction(UUID transactionID) {
        return transactions.get(transactionID);
    }

    public void addWithdrawalRequest(WithdrawalRequest withdrawalRequest) {
        withdrawals.put(withdrawalRequest.getWithdrawalId().value(), withdrawalRequest);
    }

    public Status getWithdrawalRequestState(UUID withdrawalId) {
        return withdrawals.get(withdrawalId).getStatus();
    }

    public Status getTransactionStatus(UUID operationId) {
        return transactions.get(operationId).getStatus();
    }

    public Status processTransaction(User sourceUser, User destinationUser, BigDecimal amount) {
        sourceUser.setBalance(sourceUser.getBalance().subtract(amount));
        destinationUser.setBalance(destinationUser.getBalance().add(amount));

        return Status.COMPLETED;
    }

    public Status subtractBalance(User sourceUser, BigDecimal amount) {
        sourceUser.setBalance(sourceUser.getBalance().subtract(amount));

        return Status.COMPLETED;
    }

    public Status addBalance(User destinationUser, BigDecimal amount) {
        destinationUser.setBalance(destinationUser.getBalance().add(amount));

        return Status.COMPLETED;
    }

    public void storeUnfinishedTasks (Collection<Object> tasks) {
        unfinishedOperations.addAll(tasks);
    }
}
