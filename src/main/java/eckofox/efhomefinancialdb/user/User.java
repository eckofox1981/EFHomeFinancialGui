package eckofox.efhomefinancialdb.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.user.account.Account;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements DataBaseManager {
    private App app;
    private UUID userID;
    private String username;
    private String firstname;
    private String lastname;
    private String passwordHash;
    private List<Account> acountList = new ArrayList<>();


    public User(App app, UUID userID, String username, String firstname, String lastname, String passwordHash) {
        this.app = app;
        this.userID = userID;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.passwordHash = passwordHash;
    }

    public User(App app, UUID userID, String username, String firstname, String lastname) {
        this.app = app;
        this.userID = userID;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public User() {

    }

    /**
     * sets userId paths, creates directory and file and writes username and passwordHash
     */
    @Override
    public void saving() {
        try (PreparedStatement newUserStatement =
                app.getConnection().prepareStatement("INSERT INTO users (userid, username, firstname, lastname, passwordhash) VALUES (?, ?, ?, ?, ?);")){
            newUserStatement.setObject(1, userID);
            newUserStatement.setString(2, username);
            newUserStatement.setString(3, firstname);
            newUserStatement.setString(4, lastname);
            newUserStatement.setString(5, passwordHash);
            newUserStatement.executeUpdate(); //executeUpdate => DELETE, UPDATE, INSERT INTO
        } catch (SQLException e) {
            System.err.println("Issue saving userId in SQL database: " + e.getMessage());
        }
        System.out.println("DEBUG: Account saved = " + username + " " + userID);
    }






    @Override
    public void insertData() {
        try {
            File userFile = new File("filepath");
            FileWriter writer = new FileWriter(userFile);
            writer.append(username).append("\n");
            writer.append(passwordHash);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not edit userId file.");
        }
    }

    @Override
    public void fetchData() {/*not used in this class*/}

    public void createAccounts() {
        acountList.add(new CheckingAccount(app, this));
        acountList.add(new SavingAccount(app, this));
    }

    public UUID getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getDirPath() {
        return "dirPath";
    }

    public List<Account> getAcountList() {
        return acountList;
    }
}
