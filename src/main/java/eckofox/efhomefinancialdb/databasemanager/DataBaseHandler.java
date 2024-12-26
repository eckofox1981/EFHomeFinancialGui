package eckofox.efhomefinancialdb.databasemanager;

import eckofox.efhomefinancialdb.application.App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseHandler {
    private App app;

    public DataBaseHandler(App app) {
        this.app = app;
    }

    public void settingUpConnectionAndTables(){
        try {
            connectToDatabase();
        } catch (SQLException exception){
            System.err.println("Could not start the connection at startup.");
        }
        settingUpTables();
    }

    public void connectToDatabase() throws SQLException {
        File file = new File("src/main/resources/dbconnection.txt");
        String connectionString = "";
        try (
                FileReader fileReader = new FileReader(file);
                BufferedReader lineReader = new BufferedReader(fileReader) ){
            connectionString = lineReader.readLine();
        } catch (IOException e) {
            System.err.println("FileReader error");
        }
        app.setConnection(DriverManager.getConnection(connectionString));
    }

    public void settingUpTables(){
        settingUpUserTable();
        settingUpTransactionTable();
    }

    private void settingUpUserTable() {
        try {
            //TODO: add account and transactions history to table
            Statement createUserTableStatement = app.getConnection().createStatement();
            createUserTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS users (userid UUID UNIQUE PRIMARY KEY, " +
                            "username TEXT UNIQUE, firstname TEXT, lastname TEXT, passwordhash TEXT, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        } catch (SQLException exception) {
            System.err.println("Issue with createUserTableStatement." + exception.getMessage());
        }
    }

    private void settingUpAccountTable(){
        try {
            Statement createAccountTableStatement = app.getConnection().createStatement();
            createAccountTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS accounts (accountid UUID UNIQUE PRIMARY KEY, name TEXT, userid UUID, " +
                            "FOREIGN KEY (userid) REFERENCES users(userid));");
        } catch (SQLException e) {
            System.err.println("Issue with createAccountTableStatement. " + e.getMessage());
        }
    }

    private void settingUpTransactionTable(){
        try {
            Statement createTransactionTableStatement = app.getConnection().createStatement();
            createTransactionTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS transactions (id UUID UNIQUE PRIMARY KEY, date DATE, " +
                            "transactiontype TEXT CHECK (transactiontype IN ('WITHDRAWAL', 'DEPOSIT', 'TRANSFER')), " +
                            "amount DECIMAL, comment TEXT, userid UUID, FOREIGN KEY (userid) REFERENCES users(userid));");

        } catch (SQLException exception) {
            System.err.println("Issue with create transaction table. " + exception.getMessage());
        }
    }

    private void settingUpAccountTransactionTable() {
        try {
            Statement accountTransactionTableStatement = app.getConnection().createStatement();
            accountTransactionTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS account_transactions (id SERIAL PRIMARY KEY, accountid UUID, transactionid UUID," +
                            "FOREIGN KEY (accountid) REFERENCES accounts(accountid), " +
                            "FOREIGN KEY (transactionid) REFERECENCES transactions(id));"
            )
        } catch (SQLException e) {
            System.err.println("Issue with junction table creation." + e.getMessage());
        }
    }
}
