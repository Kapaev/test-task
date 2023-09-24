package org.server.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class User {
    private UUID userID;
    private String userName;
    private BigDecimal balance;

    public User(String userName, BigDecimal initialBalance) {
        this.userID = UUID.randomUUID();
        this.userName = userName;
        this.balance = initialBalance;
    }

    public User(UUID userID, String userName, BigDecimal initialBalance) {
        this.userID = userID;
        this.userName = userName;
        this.balance = initialBalance;
    }
    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userID, user.userID) && Objects.equals(userName, user.userName) && Objects.equals(balance, user.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, userName, balance);
    }
}