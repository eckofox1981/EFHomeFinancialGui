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

}