package eckofox.efhomefinancialdb.transaction;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.filemanager.FileManager;
import eckofox.efhomefinancialdb.transaction.idnumber.IdNumber;
import eckofox.efhomefinancialdb.user.User;
import eckofox.efhomefinancialdb.user.account.Account;

import java.io.*;

public class Transaction implements FileManager {
    protected App app;
    protected IdNumber idNumber;
    protected User user;
    protected int id;
    protected TransactionType transactionType;
    protected String date;
    protected double amount;
    protected String comment;
    protected String dirPath;
    protected String filePath;

    public Transaction(App app) {
        this.app = app;
        user = app.getActiveUser();
    }

    public Transaction(int id, String date, double amount, String comment) {
        this.app = getApplication();
        this.user = app.getActiveUser();
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.comment = comment;
        setPaths();
    }

    public Transaction(App app, String date, double amount, String comment) {
        this.app = app;
        this.date = date;
        this.amount = amount;
        this.comment = comment;
    }

    /** SAVING PROCESS:
     *  filewriter (after: createFile <- createDir) -> write down the main data of the transaction in plain text
     *  account balances are updated and displayed
     */
    @Override
    public void saving() {
        fileWriter();
        System.out.println("Transaction number " + id + " saved.");
        app.getActiveUser().getAcountList().stream()
                .peek(Account::setBalanceFromTransactions).
                forEach(a -> System.out.print(a.getName() + ": " + a.getBalance() + " | "));
        System.out.println();
    }

    /**
     * check if transactions directory exist and creates it, according to preconfigured directory path, if necessary.
     */
    @Override
    public void createDir() {
        File transactionDir = new File(dirPath);
        if (transactionDir.exists()) {
            return;
        }
        transactionDir.mkdirs();
        System.out.println("Transaction directory created.");
    }

    /**
     * calls create dir to check directory then creates a file according to preconfigured filepath
     * to be edited.
     */
    @Override
    public void createFile() {
        createDir();
        File transactionFile = new File(filePath);
        if (transactionFile.exists()) {
            System.out.println("File already exits.");
            return;
        }
        try {
            transactionFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create transaction file.");
        }
    }

    /**
     * edit the file with transaction data.
     */
    @Override
    public void fileWriter() {
        try {
            createFile();

            FileWriter writer = new FileWriter(filePath, true);

            writer.append(String.valueOf(id)).append("\n");
            writer.append(date).append("\n");
            writer.append(transactionType.toString()).append("\n");
            amountIsPositive();
            writer.append(String.valueOf(amount)).append("\n");
            writer.append(comment).append("\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not edit transaction file.");
        }
    }

    /**
     * reads of transaction file and sets the transaction fields.
     */
    @Override
    public void fileReader() {
        File transactionFile = new File(filePath);

        try (FileReader file = new FileReader(transactionFile.getAbsolutePath());
                                BufferedReader lineReader = new BufferedReader(file)) {

            id = Integer.parseInt(lineReader.readLine());
            date = lineReader.readLine();
            transactionType = TransactionType.valueOf(lineReader.readLine());
            amount = Double.parseDouble(lineReader.readLine());
            comment = lineReader.readLine();

        } catch (FileNotFoundException e) { //never had it so far
            System.out.println("File not found:" + transactionFile.getName());
        } catch (IOException e) { //never had it so far
            System.err.println("Error reading file: " + transactionFile.getName());
        } catch (NumberFormatException e) { //had it after screwing in the files, should not happen through normal usage
            System.err.println("Invalid data format in file: " + transactionFile.getName());
        }
    }

    /**
     * deletes the transaction through its file path
     */
    public void deleteFile() {
        File file = new File(filePath);
        if (file.delete()) {
            System.out.println("Transaction nr. " + id + " deleted.");
        } else {
            System.out.println("Could not erase transaction file nr " + id + ".");
        }
    }

    /**
     * withdrawal are set as negative as to make it user-friendlier (user doesn't have to type -amount)
     */
    private void amountIsPositive() {
        if (transactionType.equals(TransactionType.WITHDRAWAL)) {
            this.amount = Math.abs(this.amount) * -1;
        }
    }


    /**
     * FOLLOWING ARE SOMETIMES "UPGRADED" GETTERS AND SETTERS
     */
    public void setPaths() {
        setDirPath();
        setFilePath();
    }

    public void setDirPath() {
        dirPath = user.getDirPath() + "transactionFiles/";
    }

    public void setFilePath() {
        filePath = dirPath + id + " " + date + ".txt";
    }

    public void setFilePath(String filePath) { //overloaded for TransactionGatherer
        this.filePath = filePath;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public App getApplication() {
        return app;
    }

    public User getUser() {
        return user;
    }

    public int getId() {
        return id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

}
