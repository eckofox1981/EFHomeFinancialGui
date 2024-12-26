package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.User;

public class SavingAccount extends Account {

    public SavingAccount(App app, User user) {
        super(app, "Saving account", user);
        setUserId(user.getUserID());
    }

    @Override
    public void setBalanceFromTransactions() {
        double newBalance = 0;

        for (Transaction transaction : app.getTransactionGatherer().getTransactionList()) {
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


}