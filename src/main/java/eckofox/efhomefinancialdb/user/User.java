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
        createAccounts();
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

    /** sends user details into SQL database
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
    }

    /** updates user data in SQL database
     */
    @Override
    public void insertUpdateData() {
        try (PreparedStatement updateStatement = app.getConnection().prepareStatement(
                "UPDATE users SET username = ?, firstname = ?, lastname = ?, passwordhash = ? WHERE userid = ?")){
            updateStatement.setString(1, username);
            updateStatement.setString(2, firstname);
            updateStatement.setString(3, lastname);
            updateStatement.setString(4, passwordHash);
            updateStatement.setObject(5, userID);
            updateStatement.executeUpdate();

        }catch (SQLException e) {
            System.err.println("Issue with user.updateStatement");
        }
    }

    @Override
    public void fetchData() {/*not used in this class*/}

    /** deletes user data, since 'accounts' and 'transactions' table are referencing to each other and the 'users' table
     *  using the ON DELETE CASCADE constraint, all accounts and transactions related to the users will be removed
     */
    @Override
    public void deleteData() {
        try (PreparedStatement deleteStatement = app.getConnection().prepareStatement(
                "DELETE FROM users WHERE userid = ?;")) {
            deleteStatement.setObject(1, userID);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Issue with deleteStatement. " + e.getMessage());
        }
    }

    /**
     * adds the default accounts for the user
     */
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<Account> getAcountList() {
        return acountList;
    }
}
