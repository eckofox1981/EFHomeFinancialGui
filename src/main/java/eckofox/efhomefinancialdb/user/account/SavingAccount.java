package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.User;

public class SavingAccount extends Account {

    public SavingAccount(Application application, User user) {
        super(application, "Saving account");
        this.setUser(user);
        createFile();
        fileWriter();
    }

    @Override
    public void setBalanceFromTransactions() {
        double newBalance = 0;

        for (Transaction transaction : application.getTransactionGatherer().getTransactionList()) {
            if (transaction.getTransactionType().equals(TransactionType.TRANSFER)) {
                newBalance += transferCheck(transaction);
            }
        }
        setBalance(newBalance);
    }

    @Override
    protected double transferCheck(Transaction transaction) {
        if (transaction.getComment().equalsIgnoreCase("TO-CHECKING")) {
            return Math.abs(transaction.getAmount()) * -1;
        }
        return transaction.getAmount();
    }

    @Override
    public void saving() {
        /* unused in this class*/
    }

    @Override
    public void createDir() {
        /* unused in this class*/
    }
}