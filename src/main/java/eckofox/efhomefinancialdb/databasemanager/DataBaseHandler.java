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

    /**
     * connects to the database and sets up the SQL tables
     */
    public void settingUpConnectionAndTables(){
        try {
            connectToDatabase();
        } catch (SQLException exception){
            System.err.println("Could not start the connection at startup.");
        }
        settingUpTables();
    }

    /** uses the dbconnection.txt file to connect to the database
     * IO exception handled in house.
     * @throws SQLException handled in settingUpConnectionAndTables
     */
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

    /**
     * sets up the SQL table in logical order:
     * users (primary key to account)
 *          -> accounts (references user and primary to transactions)
     *             -> transactions (references accounts)
     */
    public void settingUpTables(){
        settingUpUserTable();
        settingUpAccountTable();
        settingUpTransactionTable();
    }

    /** creates users table in SQL database
     * columns are: userid (UUID PRIMARY KEY), username (TEXT UNIQUE),first- and lastname (TEXT), created_at (TIMESTAMP)
     */
    private void settingUpUserTable() {
        try {
            Statement createUserTableStatement = app.getConnection().createStatement();
            createUserTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS users (userid UUID UNIQUE PRIMARY KEY, " +
                            "username TEXT UNIQUE, firstname TEXT, lastname TEXT, passwordhash TEXT, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        } catch (SQLException exception) {
            System.err.println("Issue with createUserTableStatement." + exception.getMessage());
        }
    }

    /** creates accounts table in SQL database
     * columns are:
     * accountid (UUID PRIMARY KEY), name (TEXT), balance (DECIMAL), userid (UUID ref to users.userid)
     * ON DELETE CASCADE so the accounts are deleted with the users.
     */
    private void settingUpAccountTable(){
        try {
            Statement createAccountTableStatement = app.getConnection().createStatement();
            createAccountTableStatement.execute(
                    "CREATE TABLE IF NOT EXISTS accounts (accountid UUID UNIQUE PRIMARY KEY, name TEXT, balance DECIMAL, userid UUID, " +
                            "FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE);");
        } catch (SQLException e) {
            System.err.println("Issue with createAccountTableStatement. " + e.getMessage());
        }
    }

    /** creates transactions table in SQL database
     * columns are:
     * id (UUID PRIMARY KEY), date (DATE) transactiontype (TEXT with CHECK), amount (DECIMAL), comment (TEXT),
     * accountid (UUID ref to accounts.accountid)
     * ON DELETE CASCADE so the accounts are deleted with the account (deleted with user).
     */
    private void settingUpTransactionTable(){
        try {
            Statement createTransactionTableStatement = app.getConnection().createStatement();
            createTransactionTableStatement.execute(
                        "CREATE TABLE IF NOT EXISTS transactions (id UUID UNIQUE PRIMARY KEY, date DATE, " +
                                "transactiontype TEXT CHECK (transactiontype IN ('WITHDRAWAL', 'DEPOSIT', 'TRANSFER')), " +
                                "amount DECIMAL, comment TEXT, accountid UUID, " +
                                "FOREIGN KEY (accountid) REFERENCES accounts(accountid) ON DELETE CASCADE);");

        } catch (SQLException exception) {
            System.err.println("Issue with create transaction table. " + exception.getMessage());
        }
    }

}
