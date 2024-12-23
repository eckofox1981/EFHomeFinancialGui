package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.transaction.idnumber.IdNumber;

public class Deposit extends Transaction {
    public Deposit(App app, String date, double amount, String comment) {
        super(app, date, amount, comment);
        userId = app.getActiveUser();
        idNumber = new IdNumber(this);
        idNumber.generateIdNumber();
        id = idNumber.getIdNumber();
        transactionType = TransactionType.DEPOSIT; //difference from parent
        toBEREMOVEDsetPaths();
    }

    /**
     * SEE PARENT CLASS
     */
}
