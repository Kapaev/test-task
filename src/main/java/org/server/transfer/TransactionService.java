package org.server.transfer;

import org.server.model.Transaction;
import org.server.model.Status;
import org.server.model.User;
import org.server.dao.Dao;

import java.util.function.Consumer;

public class TransactionService implements Consumer<Transaction> {

    private final Dao dao;

    public TransactionService(Dao dao) {
        this.dao = dao;
    }

    @Override
    public void accept(Transaction transaction) {
        User sourceUser = dao.getUser(transaction.getSourceUserID());
        User destinationUser = dao.getUser(transaction.getDestinationUserID());

        if (sourceUser == null || destinationUser == null) {
            transaction.setStatus(Status.FAILED);
            return;
        }
        if (sourceUser.getBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(Status.FAILED);
            return;
        }

        Status status = dao.processTransaction(sourceUser, destinationUser, transaction.getAmount());

        transaction.setStatus(status);
    }
}
