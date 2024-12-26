package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.user.account.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;

import java.util.UUID;

/**
 * a quick and easy way to gather transactions. Used for displaying transaction, erasing transaction and
 * calculating account balances.
 */
public class TransactionManager {
    private App app;

    public TransactionManager(App app) {
        this.app = app;
    }

    /** Gathering process in short:
     * the transaction list is cleared (to avoid to add all transactions again)
     * a generic transaction is used to find the userId's transaction directory
     * each file is 'transaction.fetchData()' (see Transaction class) and resulting transaction added to the list
     * Error-handling accordingly
     */
    private void transactionGatherer() {
        app.getActiveTransactionList().clear();

        ResultSet resultSet = fetch5LatestTransactions();
        if (resultSet == null) {
            System.out.println("DEBUG: resultset = null");
            return;
        }
        try {
            while (resultSet.next()) {
                UUID id = (UUID) resultSet.getObject("id");
                TransactionType transactionType = (TransactionType) resultSet.getObject("transactiontype");
                Account fromAccount = transactionAccountCheck((UUID) resultSet.getObject("accountid"));
                Date date = resultSet.getDate("date");
                Double amount = resultSet.getDouble("amount");
                String comment = resultSet.getString("comment");
                Transaction transaction = new Transaction(app, app.getActiveUser(), id, transactionType, fromAccount, date,
                        amount, comment);
                app.getActiveTransactionList().add(transaction);
            }
        } catch (SQLException e) {
            e.getMessage();
        }

    }

    private ResultSet fetch5LatestTransactions() {
        ResultSet resultSet = null;
        try (PreparedStatement select5Transactions = app.getConnection().prepareStatement(
                "SELECT * FROM transactions JOINS accounts ON transactions.accountid = accounts.accountid WHERE accountid = ? ORDER BY transactions.date LIMIT 5;")) {
            select5Transactions.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
            resultSet = select5Transactions.executeQuery();
        } catch (SQLException e){
            System.err.println("Issue with fetch5LatestTransactions. " + e.getMessage());
        }
        return resultSet;
    }

    private Account transactionAccountCheck(UUID accountId) {
        for (Account account : app.getActiveUser().getAcountList()){
            if (account.getAccountId().equals(accountId)){
                return account;
            }
        }
        return app.getActiveUser().getAcountList().getFirst();
    }
}
