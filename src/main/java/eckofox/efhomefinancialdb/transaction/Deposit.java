package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.transaction.idnumber.IdNumber;

public class Deposit extends Transaction {
    public Deposit(Application application, String date, double amount, String comment) {
        super(application, date, amount, comment);
        user = application.getActiveUser();
        idNumber = new IdNumber(this);
        idNumber.generateIdNumber();
        id = idNumber.getIdNumber();
        transactionType = TransactionType.DEPOSIT; //difference from parent
        setPaths();
    }

    /**
     * SEE PARENT CLASS
     */
}
