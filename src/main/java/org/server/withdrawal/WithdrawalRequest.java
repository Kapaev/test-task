package org.server.withdrawal;

import org.server.model.Status;

import java.math.BigDecimal;
import java.util.UUID;

import static org.server.withdrawal.WithdrawalService.*;

public class WithdrawalRequest {

    private UUID sourceUserID;
    private WithdrawalId withdrawalId;
    private BigDecimal amount;
    private Address address;
    private Status status;

    public WithdrawalRequest(UUID sourceUserID, WithdrawalId withdrawalId, BigDecimal amount, Address address, Status status) {
        this.sourceUserID = sourceUserID;
        this.withdrawalId = withdrawalId;
        this.amount = amount;
        this.address = address;
        this.status = status;
    }

    public UUID getSourceUserID() {
        return sourceUserID;
    }

    public WithdrawalId getWithdrawalId() {
        return withdrawalId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Address getAddress() {
        return address;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
