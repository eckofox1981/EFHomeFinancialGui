package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.date.DateUtility;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.transaction.Withdrawal;

public class TransferCommand extends Command implements EnterTransactionManager {

    public TransferCommand(App app) {
        super("transfer-to",
                """
                        transfer-to [checking/saving] [DATE(YYYY-MM-DD)] [amount]: transfer the amount to the account specified from the account not mentioned.
                                  Example: "transfer-to saving 2023-07-04 1000" - transfers 1000 from the checking account to the saving account at the given date.
                                  The transaction is automatically recorded.""", app);
    }

    /**
     * since EnterDeposit, EnterWithdrawal and TransferCommand are very similar, they all used the
     * EnterTransactionManager-interface almost identically. The main difference will be at Transaction-creation which is
     * overriden (creates either Deposit, Withdrawal or Transfer).
     * Since the TransferCommand itself is simpler for the userId (all in one line) than other transactions (prompt after
     * prompt) it requires a little bit more error handling through the commandAnalyzer but the interface is used
     * the same way.
     * @param commandArgs
     */
    @Override
    public void run(String commandArgs) {
        String[] splitArgs = commandArgs.split(" ");
        if (splitArgs.length < 4) {
            System.out.println("This command requires more arguments.");
            showDescription();
            return;
        }

        if (splitArgs.length > 4) {
            System.out.println("This command requires less arguments.");
            showDescription();
            return;
        }

        commandAnalyzer(splitArgs);
    }

    private void commandAnalyzer(String[] args) {
        String toAccount;
        String date;
        String amount;

        if (!args[1].equalsIgnoreCase("checking") && !args[1].equalsIgnoreCase("saving")) {
            System.out.println("Please enter account to transfer to, ex: transfer-to 'SAVING' (or 'CHECKING').");
            showDescription();
            return;
        } else {
            toAccount = args[1];
        }

        if (!DateUtility.checkIsDate(args[2])) {
            System.out.println("Please enter the date as [YYYY-MM-DD], ex: '1981-12-09'.");
            showDescription();
            return;
        } else {
            date = args[2];
        }

        if (!checkIsAmount(args[3])) {
            System.out.println("Please enter a numerical value (numbers) as third argument.");
            showDescription();
            return;
        } else {
            amount = args[3];
        }
        String commment = ("TO-" + toAccount).toUpperCase();
        createTransaction(date, amount, commment);

    }

    @Override
    public void createTransaction(String date, String amountIn, String comment) {
        double amount = Double.parseDouble(amountIn);
        Transaction transaction = new Withdrawal(app, date, amount, comment);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.saving();
    }
}
