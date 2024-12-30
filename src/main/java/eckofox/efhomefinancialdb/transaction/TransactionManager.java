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
    public void gatherAllTransactions () {
        app.getAllTransactionsList().clear();
        //hämtar allt
        try (PreparedStatement selectAllTransactionsStatement = app.getConnection().prepareStatement(
                "SELECT transactions.id, transactions.date, transactions.transactiontype, " +
                        "transactions.amount, transactions.comment, transactions.accountid FROM transactions " +
                        "JOIN accounts ON transactions.accountid = accounts.accountid " +
                        "WHERE accounts.accountid = ? ORDER BY transactions.date DESC"
        )) {
            selectAllTransactionsStatement.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
            ResultSet resultSet = selectAllTransactionsStatement.executeQuery();
            try{
                while (resultSet.next()) {
                    UUID id = (UUID) resultSet.getObject("id");
                    TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transactiontype"));
                    Account fromAccount = transactionAccountCheck((UUID) resultSet.getObject("accountid"));
                    Date date = resultSet.getDate("date");
                    Double amount = resultSet.getDouble("amount");
                    String comment = resultSet.getString("comment");
                    Transaction transaction = new Transaction(app, app.getActiveUser(), id, transactionType, fromAccount, date,
                            amount, comment);
                    app.getAllTransactionsList().add(transaction);
                }
            } catch (SQLException ex) {
                System.err.println("Issue with gathering all transactions in gatherAllTransactions. " + ex.getMessage());
            }

        }  catch (SQLException e) {
            System.err.println("Issue with selectAllTransactionsStatement. " + e.getMessage());
        }

    }

    public void transactionFilter (/* vilkor*/) {
        app.getFilteredTransactionList().clear();
        String standardSelect = "SELECT transactions.id, transactions.date, transactions.transactiontype, " +
                "transactions.amount, transactions.comment, transactions.accountid FROM transactions " +
                "JOIN accounts ON transactions.accountid = accounts.accountid ";
        String dateSelect = "";
        String typeSelect = "";
        //hämtar baserad på kriterier
        try (PreparedStatement selectFilteredTransactionsStatement = app.getConnection().prepareStatement(
                standardSelect + "WHERE accounts.accountid = ? ORDER BY transactions.date DESC"
        )) {
            selectFilteredTransactionsStatement.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
            ResultSet resultSet = selectFilteredTransactionsStatement.executeQuery();
            try{
                while (resultSet.next()) {
                    UUID id = (UUID) resultSet.getObject("id");
                    TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transactiontype"));
                    Account fromAccount = transactionAccountCheck((UUID) resultSet.getObject("accountid"));
                    Date date = resultSet.getDate("date");
                    Double amount = resultSet.getDouble("amount");
                    String comment = resultSet.getString("comment");
                    Transaction transaction = new Transaction(app, app.getActiveUser(), id, transactionType, fromAccount, date,
                            amount, comment);
                    app.getFilteredTransactionList().add(transaction);
                }
            } catch (SQLException ex) {
                System.err.println("Issue with gathering all transactions in transactionFilter. " + ex.getMessage());
            }

        }  catch (SQLException e) {
            System.err.println("Issue with selectAllTransactionsStatement. " + e.getMessage());
        }

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
