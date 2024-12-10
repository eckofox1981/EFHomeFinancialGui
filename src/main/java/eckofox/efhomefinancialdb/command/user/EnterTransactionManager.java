package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.date.DateUtility;

import java.util.Scanner;

public interface EnterTransactionManager {


    default void enterTransaction() {
        Scanner scanner = new Scanner(System.in);
        String date;
        String amount;
        String comment;
        do {
            System.out.print("Enter the date [YYYY-MM-DD]: ");
            date = scanner.nextLine();
        } while (!DateUtility.checkIsDate(date));

        do {
            System.out.print("Enter the amount: ");
            amount = scanner.nextLine();
        } while (!checkIsAmount(amount));
        System.out.print("Enter comment: ");
        comment = scanner.nextLine();
        createTransaction(date, amount, comment);
    }

    default void createTransaction(String date, String amountIn, String comment) {
    }

    default boolean checkIsAmount(String amount) {
        try {
            Double.parseDouble(amount);
        } catch (IllegalArgumentException e) {
            System.out.println("Amount format incorrect, numerics only, use '.' (point) for decimal.");
            return false;
        }
        return true;
    }

}
