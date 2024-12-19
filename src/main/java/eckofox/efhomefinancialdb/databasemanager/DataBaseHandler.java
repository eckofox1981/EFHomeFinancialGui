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
            System.err.println("Could not start the connection att startup.");
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
                    "CREATE TABLE IF NOT EXISTS users (userid UUID UNIQUE PRIMARY KEY, username TEXT UNIQUE, firstname TEXT, lastname TEXT, passwordhash TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        } catch (SQLException exception) {
            System.err.println("Issue with createUserTableStatement");
        }
    }

    private void settingUpTransactionTable(){
        try {
            Statement createTransactionTableStatement = app.getConnection().createStatement();
            createTransactionTableStatement.execute("CREATE TABLE IF NOT EXISTS transactions (id SERIAL, username TEXT, date DATE, transaction_type TEXT, amount DECIMAL (20, 2), comment TEXT);");

        } catch (SQLException exception) {
            System.err.println("Issue with create transaction table. " + exception.getMessage());
        }
    }
}
