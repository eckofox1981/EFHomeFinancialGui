package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.transaction.idnumber.IdNumber;

public class Withdrawal extends Transaction {

    public Withdrawal(App app, String date, double amount, String comment) {
        super(app, date, amount, comment);
        user = app.getActiveUser();
        idNumber = new IdNumber(this);
        idNumber.generateIdNumber();
        id = idNumber.getIdNumber();
        transactionType = TransactionType.WITHDRAWAL; //difference from parent
        setPaths();
    }

    /**
     * SEE PARENT CLASS
     */
}
