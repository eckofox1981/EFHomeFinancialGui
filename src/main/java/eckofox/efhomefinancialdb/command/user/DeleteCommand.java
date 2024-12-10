package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.transaction.Transaction;

import java.util.Optional;

public class DeleteCommand extends Command {


    public DeleteCommand(Application application) {
        super("delete-transaction",
                "delete-transaction [transaction id#]: deletes a transaction from the records.", application);
    }


    /**
     * double checks that enough args have been received before sending them to commanAnalyzer() as a String-array
     * @param commandArgs from user
     */
    @Override
    public void run(String commandArgs) {
        String[] splitArgs = commandArgs.split(" ");
        if (splitArgs.length < 2) {
            System.out.println("This command requires more arguments.");
            showDescription();
            return;
        }

        if (splitArgs.length > 2) {
            System.out.println("This command requires less arguments.");
            showDescription();
            return;
        }

        commandAnalyzer(splitArgs);
    }


    /**
     * checks that user argument are correctly formatted (in this case an Integer) and then ask for
     * user confirmation before sending transaction ID-number to deleteFile.
     * Before confirmation, the user can see the transaction (stored by TransactionGatherer)
     * @param args split args from 'run'
     */
    private void commandAnalyzer(String[] args) {
        int idNumber;

        try {
            idNumber = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a numerical value (numbers) as third argument.");
            showDescription();
            return;
        }


        System.out.println("Are you sure you want to delete the following transaction (yes/no):");

        application.getTransactionGatherer().getTransactionList().stream()
                .filter(a -> a.getId() == idNumber)
                .forEach(a -> System.out.println(a.getId() + " " + a.getDate() + " " + a.getAmount() + " " + a.getComment()));

        String answer = application.scanner.nextLine();

        if (answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y")) {
            deleteFile(idNumber);
            return;
        }
        if (answer.equalsIgnoreCase("no") || answer.equalsIgnoreCase("n")) {
            System.out.println("File deletion aborted.");
            return;
        }
        System.out.println("incorrect input. File deletion aborted.");

    }


    /**
     * looks up the transaction ID-number and tries to create a transaction from transactionGatherer (if found)
     * Transaction has an inbuilt method to delete its own file (i.e. itself).
     * @param idNumber
     */
    private void deleteFile(int idNumber) {
        Optional<Transaction> transaction = Optional.ofNullable(application.getTransactionGatherer().getTransactionList()
                .stream()
                .filter(a -> a.getId() == idNumber)
                .findFirst()
                .orElse(null));

        if (transaction == null) {
            System.out.println("Transaction not found.");
            return;
        }
        transaction.get().deleteFile();
    }
}
