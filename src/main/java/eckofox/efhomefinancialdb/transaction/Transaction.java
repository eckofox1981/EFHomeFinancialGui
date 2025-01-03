package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.user.User;
import eckofox.efhomefinancialdb.user.account.Account;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class Transaction implements DataBaseManager {
    protected App app;
    protected User user;
    protected UUID id;
    protected TransactionType transactionType;
    protected Account fromAccount;
    protected Date date;
    protected double amount;
    protected String comment;

    public Transaction(App app) {
        this.app = app;
        user = app.getActiveUser();
    }

    public Transaction(App app, User user, UUID id, TransactionType transactionType, Account fromAccount,
                       Date date, double amount, String comment) {
        this.app = app;
        this.user = user;
        this.id = id;
        this.transactionType = transactionType;
        this.fromAccount = fromAccount;
        this.date = date;
        if ((transactionType.equals(TransactionType.WITHDRAWAL) || transactionType.equals(TransactionType.TRANSFER)) && amount > 0) {
            this.amount = -amount;
        } else {
            this.amount = amount;
        }
        this.comment = comment;
    }

    public Transaction(App app, Date date, double amount, String comment) {
        this.app = app;
        this.date = date;
        this.amount = amount;
        this.comment = comment;
    }

    /**
     * saves transaction in SQL database
     */
    @Override
    public void saving() {
        try (PreparedStatement savingTransactionStatements = app.getConnection().prepareStatement(
                "INSERT INTO transactions (id, date, transactiontype, amount, comment, accountid) " +
                "VALUES (?, ?, ?, ?, ?, ?);")) {
                savingTransactionStatements.setObject(1, id);
                savingTransactionStatements.setDate(2, new java.sql.Date(date.getTime()));
                savingTransactionStatements.setString(3, String.valueOf(transactionType));
                savingTransactionStatements.setDouble(4, amount);
                savingTransactionStatements.setString(5, comment);
                savingTransactionStatements.setObject(6, fromAccount.getAccountId());
                savingTransactionStatements.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Issue saving Transaction. " + e.getMessage());
        }
    }

    /**edits changes in transactions table
     */
    @Override
    public void insertUpdateData() {
        try (PreparedStatement updateTransactionStatement = app.getConnection().prepareStatement(
                "UPDATE transactions SET date = ?, transactiontype = ?, amount = ?, comment = ?, accountid = ? " +
                        "WHERE id = ?;")) {
            updateTransactionStatement.setDate(1, new java.sql.Date(date.getTime()));
            updateTransactionStatement.setString(2, String.valueOf(transactionType));
            updateTransactionStatement.setDouble(3, amount);
            updateTransactionStatement.setString(4, comment);
            updateTransactionStatement.setObject(5, fromAccount.getAccountId());
            updateTransactionStatement.setObject(6, id);
            updateTransactionStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Issue with upDateTransactionStatement. " + e.getMessage());
        }
    }

    @Override
    public void fetchData() { /** NOT USED IN THIS CLASS, transaction info fetched in TransactionFilter*/

    }

    /**
     * deletes the transaction from the database
     */
    @Override
    public void deleteData() {
        try (PreparedStatement deleteStatement = app.getConnection().prepareStatement(
                "DELETE FROM transactions WHERE transactions.id = ?;")) {
            deleteStatement.setObject(1, id);
            System.out.println("check if " + id.toString() + " deleted");
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Issue deleting transaction. " + e.getMessage());
        }
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public java.util.Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public DoubleProperty amountProperty() {
        return new SimpleDoubleProperty(amount);
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
