package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.User;

public class CheckingAccount extends Account {

    public CheckingAccount(App app, User user) {
        super(app, "Checking account");
        this.setUser(user);
        createFile();
        fileWriter();
    }

    @Override
    public void setBalanceFromTransactions() {
        double newBalance = 0;

        for (Transaction transaction : app.getTransactionGatherer().getTransactionList()) {
            if (transaction.getTransactionType().equals(TransactionType.TRANSFER)) {
                newBalance += transferCheck(transaction);
            } else {
                newBalance += transaction.getAmount();
            }
        }
        setBalance(newBalance);
    }

    @Override
    protected double transferCheck(Transaction transaction) {
        if (transaction.getComment().equalsIgnoreCase("TO-SAVING")) {
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
