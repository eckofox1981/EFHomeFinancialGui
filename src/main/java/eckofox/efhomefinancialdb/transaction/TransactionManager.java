package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.date.DateUtility;
import eckofox.efhomefinancialdb.user.account.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
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

    public void transactionFilter (LocalDate datePicker, String searchWord, boolean earningCheckBox, boolean spendingCheckBox,
                                   boolean dayCheckBox, boolean weekCheckBox, boolean monthCheckBox, boolean yearCheckBox) {
        app.getFilteredTransactionList().clear();
        String standardSelect = "SELECT transactions.id, transactions.date, transactions.transactiontype, " +
                "transactions.amount, transactions.comment, transactions.accountid FROM transactions " +
                "JOIN accounts ON transactions.accountid = accounts.accountid WHERE accounts.accountid = ? ";
        if (datePicker == null) {
            System.out.println("datepicker null");
        } else {
            System.out.println(datePicker.toString());
        }
        String dateSelect = dateSelect(datePicker, dayCheckBox, weekCheckBox, monthCheckBox, yearCheckBox);
        String typeSelect = typeSelect(earningCheckBox, spendingCheckBox);
        String searchTermSelect = searchTermSelect(searchWord);
        String andDate = isAndWord(dateSelect);
        String andType = isAndWord(typeSelect);
        String andSearch = isAndWord(searchTermSelect);

        //hämtar baserad på kriterier
        try (PreparedStatement selectFilteredTransactionsStatement = app.getConnection().prepareStatement(
                standardSelect + andDate + dateSelect + andType +typeSelect + andSearch + searchTermSelect + "ORDER BY transactions.date DESC;"
        )) {;
            selectFilteredTransactionsStatement.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
            System.out.println("DEBUG filterQUERY: " + selectFilteredTransactionsStatement);
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

    private String isAndWord (String selectStatementPart) {
        if (selectStatementPart != "") {
            return "AND ";
        }
        return "";
    }

    private String dateSelect (LocalDate datePicker, boolean dayCheckBox, boolean weekCheckBox, boolean monthCheckBox, boolean yearCheckBox) {

        if (datePicker == null) {
            return "";
        }
        System.out.println("dateselect running");
        String firstDay = datePicker.toString();
        if (dayCheckBox) {
            return "transactions.date = '" + firstDay + "'";
        }

        Calendar firstDayC = DateUtility.stringToCalendar(firstDay);
        Calendar lastDayC = (Calendar) firstDayC.clone(); //learned from last project YEPOS !!
        lastDayC = addPeriodAccordingly(lastDayC, weekCheckBox, monthCheckBox, yearCheckBox);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String lastDay = dateFormat.format(lastDayC.getTime());

        System.out.println(lastDay);
        return "transactions.date BETWEEN '" + firstDay + "' AND '" + lastDay + "' ";
    }

    private Calendar addPeriodAccordingly(Calendar firstDay, boolean weekCheckBox, boolean monthCheckBox, boolean yearCheckBox) {
        Calendar lastDay = firstDay; //had to initialize
        if (weekCheckBox) {
            lastDay = DateUtility.addACalendarWeek(firstDay);
        } else if (monthCheckBox) {
            lastDay = DateUtility.addACalendarMonth(firstDay);
        } else if (yearCheckBox) {
            lastDay = DateUtility.addACalendarYear(firstDay);
        }
        return lastDay;
    }

    private String typeSelect (boolean earningCheckBox, boolean spendingCheckBox) {
        if ((earningCheckBox && spendingCheckBox) || (!earningCheckBox && !spendingCheckBox)) {
            return "";
        }
        if (earningCheckBox) {
            return "transactions.transactiontype = 'DEPOSIT' ";
        }
        if (spendingCheckBox) {
            return "transactions.transactiontype = 'WITHDRAWAL' ";
        }
        return "";
    }

    private String searchTermSelect (String searchWord) {
        if (searchWord == "") {
            return "";
        }
        return "(transactions.comment = '" + searchWord + "' OR transactions.transactiontype = '" + searchWord +
                "' OR accounts.name = '" + searchWord + "') ";
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
