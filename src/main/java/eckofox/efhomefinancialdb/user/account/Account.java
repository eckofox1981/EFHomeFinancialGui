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
    private String dirPath;
    private String filepath;

    public Account(App app, String name) {
        this.app = app;
        this.name = name;
    }

    /**
     * creates the account file
     */
    @Override
    public void toBEREMOVEDcreateFile() {
        toBEREMOVEDsetPaths();

        File accountFile = new File(filepath);
        if (accountFile.exists()) {
            return;
        }
        try {
            accountFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create file.");
        }
    }

    /**
     * after calling file creation, account data is appended onto on file
     */
    @Override
    public void insertData() {
        toBEREMOVEDcreateFile();
        File userFile = new File(filepath);
        try {
            FileWriter writer = new FileWriter(userFile);
            writer.append(user.getName()).append(" - ").append(name).append("\n");
            writer.append(String.valueOf(balance));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write account file.");
        }
    }

    /**
     * gather account data read from account file.
     */
    @Override
    public void fetchData() {
        toBEREMOVEDsetPaths();
        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println("No account file found.");
            return;
        }

        try (FileReader accountFile = new FileReader(file.getAbsolutePath());
             BufferedReader lineReader = new BufferedReader(accountFile)) {

            lineReader.readLine();
            setBalance(Double.parseDouble(lineReader.readLine()));

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filepath);
        } catch (IOException e) {
            System.out.println("Problem reading file - " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format - Invalid balance format in file");
        }
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

    public void toBEREMOVEDsetPaths() {
        dirPath = user.getDirPath();
        filepath = dirPath + "account - " + name + ".txt";
    }

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
