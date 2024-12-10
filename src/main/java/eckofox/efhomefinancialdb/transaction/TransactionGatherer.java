package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.Application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * a quick and easy way to gather transactions. Used for displaying transaction, erasing transaction and
 * calculating account balances.
 */
public class TransactionGatherer {
    private List<Transaction> transactionList = new ArrayList<>();
    private Application application;

    public TransactionGatherer(Application application) {
        this.application = application;
    }

    /** Gathering process in short:
     * the transaction list is cleared (to avoid to add all transactions again)
     * a generic transaction is used to find the user's transaction directory
     * each file is 'transaction.fileReader()' (see Transaction class) and resulting transaction added to the list
     * Error-handling accordingly
     */
    private void transactionGatherer() {
        transactionList.clear();

        Transaction genericTransaction = new Transaction(application);

        genericTransaction.setPaths();

        File transactionDir = new File(genericTransaction.getDirPath());
        if (!transactionDir.exists()) {
            return;
        }

        File[] transactionFiles = transactionDir.listFiles();
        if (transactionFiles == null) {
            System.out.println("No transaction on record.");
            return;
        }

        for (File file : transactionFiles) {
            Transaction transaction = new Transaction(application);
            transaction.setFilePath(file.getPath());
            transaction.fileReader();
            transactionList.add(transaction);
        }
    }

    public List<Transaction> getTransactionList() {
        transactionGatherer();
        return transactionList;
    }
}
