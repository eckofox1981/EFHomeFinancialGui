package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.date.DateUtility;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class ViewCommand extends Command {
    private List<Transaction> transactionList = new ArrayList<>();
    private String filterTitle;

    public ViewCommand(App app) {
        super("view-transaction",
                """
                        view-transaction [DAY/WEEK/MONTH/YEAR] [first date of period] [ALL/PROFIT/LOSS]:
                                  displays the transactions for the period indicated from the first date input, filtered according
                                  to ALL (all transactions), PROFIT (transactions for income) or LOSS (transaction for expends).
                                  view-transaction all: shows all recorded transactions.""", app);
    }

    /**
     * checks the number of arguments before sending them to the commandAnalyzer().
     * if at least 2 args are sent in, the transactionList is updated locally to
     * be filtered through .streams() later on.
     * @param commandArgs
     */
    @Override
    public void run(String commandArgs) {
        String[] splitArgs = commandArgs.split(" ");
        if (splitArgs.length < 2) {
            System.out.println("This command requires more arguments.");
            showDescription();
            return;
        }
        transactionList = app.getTransactionGatherer().getTransactionList();

        commandAnalyzer(splitArgs);
    }


    /**
     * sets up the search parameters and handles error.
     * Most of the time, if a search parameter other than date is missing or incorrect the app
     * will display all transaction for the time period (in some cases all transactions are shown anyway).
     * During development, it was deemed more important to get more transactions shown than to have the userId go
     * through writing new arguments in the commandLine (this is true at least so long as there are not many
     * transactions.
     * @param args
     */
    private void commandAnalyzer(String[] args) {
        String period = periodSelector(args[1]);
        String date = "";
        String type = "";
        if (period.equalsIgnoreCase("ALL")) {
            filterTitle = "SHOWING ALL TRANSACTIONS";
        } else if (args.length == 3) {
            date = args[2];
            if (!DateUtility.checkIsDate(date)) {
                showDescription();
                return;
            }
            type = "ALL";
            System.out.println("No type of transaction typed, the app will display all types (profit/loss).");
        } else if (args.length == 4) {
            date = args[2];
            if (!DateUtility.checkIsDate(date)) {
                showDescription();
                return;
            }
            type = typeEditor(args[3]);
        } else {
            System.out.println("Too many command arguments, please try again");
            showDescription();
            return;
        }

        if (period.equalsIgnoreCase("ALL")) {
            showAll();
        } else {
            periodFilter(date, type, period);
        }
    }

    /**
     * ensures the proper format of the period parameter (if error returns ALL see comment above)
     * @param typeOfPeriod
     * @return type of period to be searched
     */
    private String periodSelector(String typeOfPeriod) {
        if (typeOfPeriod.equalsIgnoreCase("ALL")
                || typeOfPeriod.equalsIgnoreCase("DAY")
                || typeOfPeriod.equalsIgnoreCase("WEEK")
                || typeOfPeriod.equalsIgnoreCase("MONTH")
                || typeOfPeriod.equalsIgnoreCase("YEAR")) {
            return typeOfPeriod.toUpperCase();
        } else {
            System.out.println("Typed period type not recognized [DAY/WEEK/MONTH/YEAR].\nApp will show all transactions.");
            showDescription();
            return "ALL";
        }
    }

    private void showAll() { //shows all transactions, no filter but sorted by idNUmber
        formattedOutput(transactionList.stream()
                .sorted(Comparator.comparingInt(Transaction::getId))
                .toList());
    }

    /**
     * filters and send transactions to formattedOutput base on following parameters:
     * @param sDate string date wich will be converted to calendar date (see DateUtility)
     * @param type of transactions, if "ALL" => all are shown
     * @param period day, week, month or year used to adapt the length of the period to be searched/filtered
     *               through addPeriodAccordingly().
     */
    private void periodFilter(String sDate, String type, String period) {
        String periodTitle = periodTitleFormatter(period).toUpperCase();

        filterTitle = "SHOWING " + periodTitle + "LY TRANSACTIONS FROM " + sDate + " (" + type.toUpperCase() + ")";

        Calendar firstDay = DateUtility.stringToCalendar(sDate);
        Calendar lastDay = (Calendar) firstDay.clone(); //learned from last project YEPOS !!
        lastDay = addPeriodAccordingly(lastDay, period);

        if (type.equalsIgnoreCase("ALL")) {
            Calendar finalLastDay = lastDay;
            formattedOutput(transactionList.stream()
                    .filter(a -> (a.getDate().equalsIgnoreCase(sDate)) || (DateUtility.stringToCalendar(a.getDate()).after(firstDay)
                            && DateUtility.stringToCalendar(a.getDate()).before(finalLastDay)))
                    .sorted(Comparator.comparingInt(Transaction::getId))
                    .toList());
            return;
        }

        TransactionType transactionType = transactionTypeConverter(type);

        Calendar finalLastDay1 = lastDay;
        formattedOutput(transactionList.stream()
                .filter(a -> (a.getDate().equalsIgnoreCase(sDate)) || (DateUtility.stringToCalendar(a.getDate()).after(firstDay)
                        && DateUtility.stringToCalendar(a.getDate()).before(finalLastDay1)))
                .filter(a -> a.getTransactionType().equals(transactionType))
                .sorted(Comparator.comparingInt(Transaction::getId))
                .toList());
    }

    /**
     * sends the first day of the period to the correct method in DateUtility depending on period length.
     * @param firstDay onto which a time-period will be added.
     * @param period defines the amount of time to b added.
     * @return
     */
    private Calendar addPeriodAccordingly(Calendar firstDay, String period) {
        Calendar lastDay = firstDay; //had to initialized
        if (period.equalsIgnoreCase("WEEK")) {
            lastDay = DateUtility.addACalendarWeek(firstDay);
        } else if (period.equalsIgnoreCase("MONTH")) {
            lastDay = DateUtility.addACalendarMonth(firstDay);
        } else if (period.equalsIgnoreCase("YEAR")) {
            lastDay = DateUtility.addACalendarYear(firstDay);
        }
        return lastDay;
    }

    /**
     * to allow some flexibility the userId can search for "LOSS" or "PROFIT" and still get the proper DEPOSIT/WITHDRAWAL
     * transaction. This method edits the inputs to the proper transaction type to b filtered.
     * Error-handling: if the type is not recognized, the userId is notified and all types of transactions are shown
     * according to logic explained in the comment for commandAnalyzer above,
     * @param typeFromUser self-explanatory
     * @return a String version of transaction type, eventually "ALL" if userId input not recognized.
     */
    private String typeEditor(String typeFromUser) {
        if (typeFromUser.equalsIgnoreCase("profit") || typeFromUser.equalsIgnoreCase("deposit")) {
            return "DEPOSIT";
        }
        if (typeFromUser.equalsIgnoreCase("loss") || typeFromUser.equalsIgnoreCase("withdrawal")) {
            return "WITHDRAWAL";
        }

        if (typeFromUser.equalsIgnoreCase("transfer")) {
            return typeFromUser.toUpperCase();
        }

        if (typeFromUser.equalsIgnoreCase("all")) {
            return typeFromUser;
        }
        System.out.println("Type of transaction not recognized, the app will display all types (profit/loss).");
        return "ALL";
    }

    /**
     * String version fo transaction-type are converted to actual Transaction.Type for filtering
     * @param sType string to be converted
     * @return the Transaction.Type
     */
    private TransactionType transactionTypeConverter(String sType) {
        if (sType.equalsIgnoreCase("profit") || sType.equalsIgnoreCase("deposit")) {
            return TransactionType.DEPOSIT;
        }
        if (sType.equalsIgnoreCase("loss") || sType.equalsIgnoreCase("withdrawal")) {
            return TransactionType.WITHDRAWAL;
        }
        return TransactionType.TRANSFER;
    }

    //The format 'period' + "LY" doesn't work if period is "DAY" becomes daYly, This corrects daYly to daIly,
    private String periodTitleFormatter(String period){
        if (period.equalsIgnoreCase("DAY")){
            return "DAI";
        }
        return period;
    }

    /**
     * prints the transactions in a table.
     * @param filteredTransactions to be printed
     */
    private void formattedOutput(List<Transaction> filteredTransactions) {
        double total = 0; //total income-outcome for the transactions displayed

        show130DashLine();
        System.out.printf("| %50s %-76s |%n", filterTitle, "");
        show130DashLine();
        System.out.printf("| %5s | %10s | %10s | %9s | %-80s |%n", "ID", "DATE", "TYPE", "AMOUNT", "COMMENT");
        show130DashLine();

        if (!filteredTransactions.isEmpty()) {
            for (Transaction transaction : filteredTransactions) {
                total += adaptingTotalForDisplay(transaction);
                System.out.printf("| %5s | %10s | %10s | %9s | %-80s |%n", transaction.getId(), transaction.getDate(), transaction.getTransactionType(),
                        transaction.getAmount(), transaction.getComment());
            }
            show130DashLine();
            System.out.printf("| %31s | %9s | %-80s |%n", "TOTAL", total, "");
        } else {
            System.out.printf("| %126s |%n", "NO TRANSACTION FOUND");
        }
        show130DashLine();
    }

    /**self-explanatory*/
    private void show130DashLine() {
        for (int i = 0; i < 130; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    /**
     * since Transfers are all positive hence they need to be adapted when calculated on the table
     * which is for the checking account.
     * if the transaction is a transfer to the saving account the transaction amount is noted as a negative.
     * The opposite is true if the transfer is to the checking account.
     * @param transaction to be controlled
     * @return the amount to be added to total (either + or -)
     */
    private double adaptingTotalForDisplay(Transaction transaction) {
        if (transaction.getTransactionType().equals(TransactionType.TRANSFER)) {
            if (transaction.getComment().equalsIgnoreCase("TO-SAVING")) {
                transaction.setAmount(Math.abs(transaction.getAmount()) * -1);
            }
        }
        return transaction.getAmount();
    }
}
