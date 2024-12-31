package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.User;

public class CheckingAccount extends Account {

    public CheckingAccount(App app, User user) {
        super(app, "Checking account", user);
        setUserId(user.getUserID());
    }

    @Override
    public void setBalanceFromTransactions() {

    }


}
