package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class Account implements DataBaseManager {
    protected App app;
    private UUID accountId;
    private String name;
    private User user;
    private UUID userId;
    private double balance;

    public Account(App app, String name, User user) {
        this.app = app;
        this.name = name;
        this.user = user;
        accountId = UUID.randomUUID();
        userId = user.getUserID();
        setBalance(0.0); //TODO proper fetching
    }

    public void saving(){
        try (PreparedStatement newAccountStatement =
                     app.getConnection().prepareStatement("INSERT INTO accounts (accountid, name, balance, userid) VALUES (?, ?, ?, ?);")){
            newAccountStatement.setObject(1, accountId);
            newAccountStatement.setString(2, name);
            newAccountStatement.setDouble(3, 0);
            newAccountStatement.setObject(4, userId);
            newAccountStatement.executeUpdate(); //executeUpdate => DELETE, UPDATE, INSERT INTO
        } catch (SQLException e) {
            System.err.println("Issue saving account in SQL database: " + e.getMessage());
        }
    }

    /**
     * after calling file creation, account data is appended onto on file
     */
    @Override
    public void insertData() {
        try (PreparedStatement updateBalanceStatement = app.getConnection().prepareStatement(
                "UPDATE accounts SET balance = ? where accountid = ?;")) {
            updateBalanceStatement.setDouble(1, balance);
            updateBalanceStatement.setObject(2, accountId);
            updateBalanceStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Issue with updating account balance statement. " + e.getMessage());
        }
    }

    /**
     * gather account data read from account file.
     */
    @Override
    public void fetchData() {
        try (PreparedStatement selectAccountDataStatement = app.getConnection().prepareStatement("SELECT * FROM accounts WHERE userid = ? AND name = ?;")){
            selectAccountDataStatement.setObject(1, userId, java.sql.Types.OTHER);
            selectAccountDataStatement.setString(2, name);

            try {
                ResultSet result = selectAccountDataStatement.executeQuery();
                if (result.next()){
                    accountId = (UUID) result.getObject("accountid");
                    balance = result.getDouble("balance");
                    userId = (UUID) result.getObject("userid");
                }
            } catch (SQLException exception){
                System.err.println("Issue with fetching " + name + " data. " + exception.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Issue with account fetch data statement=> " + name + ". " + e.getMessage());
        }

    }

    @Override
    public void deleteData() {
        try (PreparedStatement deleteStatement = app.getConnection().prepareStatement(
                "DELETE FROM accounts WHERE accountid = ?;")) {
            deleteStatement.setObject(1, accountId);
            System.out.println("check if " + accountId.toString() + " deleted");
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Issue deleting account " + name + ". " + e.getMessage());
        }
    }

    /**
     * setBalance will set the balance from the account but needs to be implemented differently
     * for Transfer-type transactions.
     */
    public void setBalanceFromTransactions() {
        double updatedBalance = 0.0;
        for (Transaction transaction : app.getAllTransactionsList()) {
            if (transaction.getFromAccount().getAccountId().equals(accountId)) {
                updatedBalance += transaction.getAmount();
            }
            if (transaction.getTransactionType().equals(TransactionType.TRANSFER) &&
            !transaction.getFromAccount().getAccountId().equals(accountId)) {
                updatedBalance += -transaction.getAmount();
            }
        }

        balance = updatedBalance;

        insertData();
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
