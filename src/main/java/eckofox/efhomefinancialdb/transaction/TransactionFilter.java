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

import java.util.Objects;
import java.util.UUID;

/**
 * a quick and easy way to gather transactions. Used for displaying transaction, erasing transaction and
 * calculating account balances.
 */
public class TransactionFilter {
    private App app;

    public TransactionFilter(App app) {
        this.app = app;
    }

    /**
     * Gathering process in short:
     * the allTransactionsList is cleared (to avoid to add all transactions again)
     * A SQL query gathers all items from the 'transactions' table
     * resulting resulset is used to make new Transaction which are added directly to the filteredTransactionList
     * Error-handling accordingly
     */
    public void gatherAllTransactions() {
        app.getAllTransactionsList().clear();

        try (PreparedStatement selectAllTransactionsStatement = app.getConnection().prepareStatement(
                "SELECT transactions.id, transactions.date, transactions.transactiontype, " +
                        "transactions.amount, transactions.comment, transactions.accountid FROM transactions " +
                        "JOIN accounts ON transactions.accountid = accounts.accountid " +
                        "WHERE accounts.accountid = ? OR accounts.accountid = ? ORDER BY transactions.date DESC"
        )) {
            selectAllTransactionsStatement.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
            selectAllTransactionsStatement.setObject(2, app.getActiveUser().getAcountList().getLast().getAccountId());

            ResultSet resultSet = selectAllTransactionsStatement.executeQuery();

            try {
                while (resultSet.next()) {

                    UUID id = (UUID) resultSet.getObject("id");
                    TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transactiontype"));
                    Account fromAccount = transactionAccountCheck((UUID) resultSet.getObject("accountid"));
                    Date date = resultSet.getDate("date");
                    double amount = resultSet.getDouble("amount");
                    String comment = resultSet.getString("comment");

                    Transaction transaction = new Transaction(app, app.getActiveUser(), id, transactionType, fromAccount, date,
                            amount, comment);

                    app.getAllTransactionsList().add(transaction);
                }
            } catch (SQLException ex) {
                System.err.println("Issue with gathering all transactions in gatherAllTransactions. " + ex.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Issue with selectAllTransactionsStatement. " + e.getMessage());
        }
    }

    /** filters transactions based on user requirement
     * the filteredTransactionsList is cleared to avoid adding all transactions to it again
     * The SQL query is put together depending on the parameters, gathered in the
     * MainScreenController.filteringTransaction()
     * @param datePicker sets the firstdate of time period (or the day for single day filter)
     * @param searchWord allows a word to be searched in various column
     * @param account sets which active account the transaction was made from (blank returns all)
     * @param earningCheckBox for  deposits, no choices -> all types
     * @param spendingCheckBox for  withdrawals, no choices -> all types
     * @param transferCheckBox for  transfers, no choices -> all types
     * @param dayCheckBox for transaction on the date set above
     * @param weekCheckBox for the week following the date set above
     * @param monthCheckBox for the month following the date above
     * @param yearCheckBox for the year following the date set above
     */
    public void transactionFilter(LocalDate datePicker, String searchWord, Account account, boolean earningCheckBox, boolean spendingCheckBox, boolean transferCheckBox,
                                  boolean dayCheckBox, boolean weekCheckBox, boolean monthCheckBox, boolean yearCheckBox) {
        app.getFilteredTransactionList().clear();

        String standardSelect = "SELECT transactions.id, transactions.date, transactions.transactiontype, " +
                "transactions.amount, transactions.comment, transactions.accountid FROM transactions " +
                "JOIN accounts ON transactions.accountid = accounts.accountid WHERE  ";

        String accountSelect = accountSelect(account);
        String dateSelect = dateSelect(datePicker, dayCheckBox, weekCheckBox, monthCheckBox, yearCheckBox);
        String typeSelect = typeSelect(earningCheckBox, spendingCheckBox, transferCheckBox);
        String searchTermSelect = searchTermSelect(searchWord);
        String andDate = isAndWord(dateSelect);
        String andType = isAndWord(typeSelect);
        String andSearch = isAndWord(searchTermSelect);

        try (PreparedStatement selectFilteredTransactionsStatement = app.getConnection().prepareStatement(
                standardSelect + accountSelect +andDate + dateSelect + andType + typeSelect + andSearch + searchTermSelect +
                        "ORDER BY transactions.date DESC;"
        )) {

            if (account == null) {
                selectFilteredTransactionsStatement.setObject(1, app.getActiveUser().getAcountList().getFirst().getAccountId());
                selectFilteredTransactionsStatement.setObject(2, app.getActiveUser().getAcountList().getLast().getAccountId());
            } else {
                selectFilteredTransactionsStatement.setObject(1, account.getAccountId());
            }

            ResultSet resultSet = selectFilteredTransactionsStatement.executeQuery();
            try {
                while (resultSet.next()) {
                    UUID id = (UUID) resultSet.getObject("id");
                    TransactionType transactionType = TransactionType.valueOf(resultSet.getString("transactiontype"));
                    Account fromAccount = transactionAccountCheck((UUID) resultSet.getObject("accountid"));
                    Date date = resultSet.getDate("date");
                    double amount = resultSet.getDouble("amount");
                    String comment = resultSet.getString("comment");
                    Transaction transaction = new Transaction(app, app.getActiveUser(), id, transactionType, fromAccount, date,
                            amount, comment);
                    app.getFilteredTransactionList().add(transaction);
                }
            } catch (SQLException ex) {
                System.err.println("Issue with gathering all transactions in transactionFilter. " + ex.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Issue with selectFilteredTransactionsStatement. " + e.getMessage());
        }
    }

    /** adds the AND word in the SQL query if necessary
     * @param selectStatementPart if the preceding statement in the query is blank, returns a blank string
     * @return the string " AND " if necessary for the SQL query
     */
    private String isAndWord(String selectStatementPart) {
        if (!Objects.equals(selectStatementPart, "")) {
            return "AND ";
        }
        return "";
    }

    /** if an account is given in the search parameters this method return the proper SQL query accordingly
     * @param account could also be blank
     * @return the query accordingly
     */
    private String accountSelect (Account account) {
        if (account == null) {
            return "(accounts.accountid = ? OR accounts.accountid = ?) ";
        }

        return "accounts.accountid = ? ";
    }

    /** edits the date part of the SQL query based on the following parameters
     * @param datePicker used to set the day of filtering or first day of period, converted to Calendar format for easier
     *                   handling (see DateUtility)
     * @param dayCheckBox if true will skip the Calendar conversion and return the query with the date unconverted
     * @param weekCheckBox uses the Calendar-converted date in the addPeriodAccordingly to set the last day of the query
     * @param monthCheckBox uses the Calendar-converted date in the addPeriodAccordingly to set the last day of the query
     * @param yearCheckBox uses the Calendar-converted date in the addPeriodAccordingly to set the last day of the query
     * @return the SQL query according to the given parameters
     */
    private String dateSelect(LocalDate datePicker, boolean dayCheckBox, boolean weekCheckBox, boolean monthCheckBox, boolean yearCheckBox) {

        if (datePicker == null) {
            return "";
        }
        String firstDay = datePicker.toString();
        if (dayCheckBox) {
            return "transactions.date = '" + firstDay + "'";
        }

        Calendar firstDayC = DateUtility.stringToCalendar(firstDay);
        Calendar lastDayC = (Calendar) firstDayC.clone(); //learned from first project YEPOS !!
        lastDayC = addPeriodAccordingly(lastDayC, weekCheckBox, monthCheckBox, yearCheckBox);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String lastDay = dateFormat.format(lastDayC.getTime());

        return "transactions.date BETWEEN '" + firstDay + "' AND '" + lastDay + "' ";
    }

    /** see usage above
     * @param firstDay of the period
     * @param weekCheckBox adds a week
     * @param monthCheckBox adds a month
     * @param yearCheckBox adds a year
     * @return the last day of the period depending on given parameters
     */
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

    /** edits the transactiontype part of the SQL query, will return blank if type is chosen     *
     * @param earningCheckBox for DEPOSIT
     * @param spendingCheckBox for WITHDRAWAL
     * @param transferCheckBox for TRANSFER
     * @return the SQL query part for the transaction type
     */
    private String typeSelect(boolean earningCheckBox, boolean spendingCheckBox, boolean transferCheckBox) {
        if (!earningCheckBox && !spendingCheckBox && !transferCheckBox) {
            return "";
        }
        if (earningCheckBox) {
            return "transactions.transactiontype = 'DEPOSIT' ";
        }
        if (spendingCheckBox) {
            return "transactions.transactiontype = 'WITHDRAWAL' ";
        }
        //no if statement necessary for transfer as only possible outcome
        return "transactions.transactiontype = 'TRANSFER' ";

    }

    /** edits a part of the SQL query for a broader search in comments, transaction-type or account names
     * @param searchWord originally written by the user
     * @return part of the query for a search of the word in the transactions table.
     */
    private String searchTermSelect(String searchWord) {
        if (searchWord.equals("")) {
            return "";
        }
        return "(transactions.comment LIKE '%" + searchWord + "%' OR transactions.transactiontype LIKE '%" +
                searchWord.toUpperCase() + "%' OR accounts.name LIKE '%" + searchWord + "%') ";
    }

    /**
     * gathers an account ID and check which of the account in the user account list the transaction belongs to.
     * @param accountId from the resultset
     * @return the account used in the transaction
     */
    private Account transactionAccountCheck(UUID accountId) {
        for (Account account : app.getActiveUser().getAcountList()) {
            if (account.getAccountId().equals(accountId)) {
                return account;
            }
        }
        return app.getActiveUser().getAcountList().getFirst();
    }
}
