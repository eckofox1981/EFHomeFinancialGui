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
        if (transactionType.equals(TransactionType.WITHDRAWAL) || transactionType.equals(TransactionType.TRANSFER)) {
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


    @Override
    public void insertData() {

    }

    /**
     * reads of transaction file and sets the transaction fields.
     */
    @Override
    public void fetchData() {

    }

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


    /**
     * withdrawal are set as negative as to make it userId-friendlier (userId doesn't have to type -amount)
     */
    private void amountIsPositive() {
        if (transactionType.equals(TransactionType.WITHDRAWAL)) {
            this.amount = Math.abs(this.amount) * -1;
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public App getApplication() {
        return app;
    }

    public User getUser() {
        return user;
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

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public DoubleProperty amountProperty() {
        return new SimpleDoubleProperty(amount);
    }



}
