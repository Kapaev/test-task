package org.server.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Transaction {

    private final UUID transactionID;
    private final UUID sourceUserID;
    private final UUID destinationUserID;
    private final BigDecimal amount;
    private Status status;

    public Transaction(UUID transactionID, UUID sourceUserID, UUID destinationUserID, BigDecimal amount, Status status) {
        this.transactionID = transactionID;
        this.sourceUserID = sourceUserID;
        this.destinationUserID = destinationUserID;
        this.amount = amount;
        this.status = status;
    }

    public UUID getTransactionID() {
        return transactionID;
    }

    public UUID getSourceUserID() {
        return sourceUserID;
    }

    public UUID getDestinationUserID() {
        return destinationUserID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionID, that.transactionID) && Objects.equals(sourceUserID, that.sourceUserID) && Objects.equals(destinationUserID, that.destinationUserID) && Objects.equals(amount, that.amount) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionID, sourceUserID, destinationUserID, amount, status);
    }
}
