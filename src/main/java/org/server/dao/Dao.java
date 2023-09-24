package org.server.dao;

import org.server.model.Status;
import org.server.model.Transaction;
import org.server.model.User;
import org.server.withdrawal.WithdrawalRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public interface Dao {

    void addUser(User user);

    void addTransaction(Transaction transaction);

    User getUser(UUID userID);

    Transaction getTransaction(UUID transactionID);

    void addWithdrawalRequest(WithdrawalRequest withdrawalRequest);

    Status getWithdrawalRequestState(UUID withdrawalId);

    Status getTransactionStatus(UUID operationId);

    Status processTransaction(User sourceUser, User destinationUser, BigDecimal amount);

    Status subtractBalance(User sourceUser, BigDecimal amount);

    Status addBalance(User destinationUser, BigDecimal amount);

    void storeUnfinishedTasks (Collection<Object> tasks);


}
