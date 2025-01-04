package eckofox.efhomefinancialdb.authservice;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.user.User;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginService {
    App app;

    public LoginService(App app) {
        this.app = app;
    }

    /** connects to database to see if username exists, response will either be used to continue an operation (for login)
     * or stop it (for user registration or change of username)
     * @param username to be checked in database
     * @return boolean
     */
    public boolean userNameExists(String username) {
        try (PreparedStatement usernameIsPresentStatement =
                     app.getConnection().prepareStatement("SELECT username FROM users WHERE username = ?;")) {
            usernameIsPresentStatement.setString(1, username);
            try (ResultSet result = usernameIsPresentStatement.executeQuery()) {
                while (result.next()) {
                    if (result.getString("username").equals(username)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to query in checkUsernameUsage. " + e.getMessage());
        }
        return false;
    }

    /** checks password hash in database     *
     * @param username -> to look up relevant user
     * @param password -> be checked through BCrypt.checkpw() (ln 68)
     * @return
     */
    public boolean passwordCheck(String username, String password) {
        String passwordHash = "";

        try (PreparedStatement checkPasswordHashStatement =
                     app.getConnection().prepareStatement("SELECT passwordhash FROM users WHERE username =?")) {
            checkPasswordHashStatement.setString(1, username);

            try (ResultSet result = checkPasswordHashStatement.executeQuery()) {
                if (result.next()) {
                    passwordHash = result.getString("passwordhash");
                }
            } catch (SQLException e) {
                System.err.println("Unable to execute query at passworCheck. " + e.getMessage());
            }
        } catch (SQLException sqlException) {
            System.err.println("Error with prepared statement checkpasswordCheck. " + sqlException.getMessage());
        }

        if (BCrypt.checkpw(password, passwordHash)) {
            return true;
        }

        return false;
    }

    /** sets the active user in App, this allows for access of user object in various classes.
     * sets up the accounts in an account list (see Account, CheckingAccount and SavingAccount)
     * results from the query are handled in userFromResult for cleaner code.
     * @param username self-explanatory
     */
    public void setActiveUser(String username) {
        User user = new User();

        try (PreparedStatement selectAllStatement = app.getConnection().prepareStatement("SELECT * FROM users WHERE username = ?;")) {
            selectAllStatement.setString(1, username);
            try (ResultSet result = selectAllStatement.executeQuery()) {
                while (result.next()) {
                    user = userFromResult(result);
                    app.setActiveUser(user);
                    app.getActiveUser().getAcountList().clear();
                    app.getActiveUser().getAcountList().add(new CheckingAccount(app, user));
                    app.getActiveUser().getAcountList().add(new SavingAccount(app, user));
                }
            } catch (SQLException e) {
                System.err.println("Error while creating userId at setActiveUser. " + e.getMessage());
            }

        } catch (SQLException sqlException) {
            System.err.println("Error with prepared statement in setActiveUser. " + sqlException.getMessage());
        }
    }

    /** create a user from result
     * @param result from SQL query above
     * @return the created user based on the query.
     * @throws SQLException not handled here since it has to be handled in setActiveUser anyway.
     */
    private User userFromResult(ResultSet result) throws SQLException {
        UUID userid = UUID.fromString(result.getString("userid"));
        String username = result.getString("username");
        String firstname = result.getString("firstname");
        String lastname = result.getString("lastname");
        String passwordHash = result.getString("passwordhash");

        return new User(app, userid, username, firstname, lastname, passwordHash);
    }


}
