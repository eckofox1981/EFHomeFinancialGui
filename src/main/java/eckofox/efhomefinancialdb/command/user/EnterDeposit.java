package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.transaction.Deposit;
import eckofox.efhomefinancialdb.transaction.Transaction;

public class EnterDeposit extends Command implements EnterTransactionManager {

    public EnterDeposit(App app) {
        super("deposit",
                "deposit: follow the prompts to enter a deposit.", app);
    }

    /**
     * since EnterDeposit, EnterWithdrawal and TransferCommand are very similar, they all used the
     * EnterTransactionManager almost identically. The main difference will be at Transaction-creation which is
     * overriden (creates either Deposit, Withdrawal or Transfer).
     * @param commandArgs
     */
    @Override
    public void run(String commandArgs) {
        enterTransaction();
    }

    @Override
    public void createTransaction(String date, String amountIn, String comment) {
        double amount = Double.parseDouble(amountIn);
        Transaction transaction = new Deposit(app, date, amount, comment);
        transaction.saving();
    }
}
