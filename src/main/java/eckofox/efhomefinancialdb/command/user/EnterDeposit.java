package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.transaction.Deposit;
import eckofox.efhomefinancialdb.transaction.Transaction;

public class EnterDeposit extends Command implements EnterTransactionManager {

    public EnterDeposit(Application application) {
        super("deposit",
                "deposit: follow the prompts to enter a deposit.", application);
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
        Transaction transaction = new Deposit(application, date, amount, comment);
        transaction.saving();
    }
}
