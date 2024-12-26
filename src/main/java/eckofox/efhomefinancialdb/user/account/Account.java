package eckofox.efhomefinancialdb.user.account;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.user.User;

import java.io.*;

public abstract class Account implements DataBaseManager {
    protected App app;
    private String name;
    private User user;
    private double balance;

    public Account(App app, String name) {
        this.app = app;
        this.name = name;
    }

    /**
     * creates the account file
     */



    /**
     * after calling file creation, account data is appended onto on file
     */
    @Override
    public void insertData() {

    }

    /**
     * gather account data read from account file.
     */
    @Override
    public void fetchData() {

    }

    /**
     * setBalance will set the balance from the account but needs to be implemented differently
     * for Transfer-type transactions.
     */
    public abstract void setBalanceFromTransactions();

    /**
     * Transfer (extending Transaction) are submitted to a check to make sure
     * the proper account is awarded or debited
     */
    abstract double transferCheck(Transaction transaction);


    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
