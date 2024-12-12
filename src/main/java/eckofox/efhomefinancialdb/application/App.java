package eckofox.efhomefinancialdb.application;

import eckofox.efhomefinancialdb.databasemanager.DataBaseHandler;
import eckofox.efhomefinancialdb.menu.LoginMenu;
import eckofox.efhomefinancialdb.menu.Menu;
import eckofox.efhomefinancialdb.menu.NewUserMenu;
import eckofox.efhomefinancialdb.menu.UserMenu;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionGatherer;
import eckofox.efhomefinancialdb.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class App extends Application {

    /**
     * activeUser and activeMenu are used to set up different phases of the app.
     * for example the activeMenu is referenced for the different menu command
     * activeUser is obviously referenced for accessing/creating user data.
     */
    private User activeUser;
    private Menu activeMenu;

    public boolean running = true;

    private UserMenu userMenu;
    private LoginMenu loginMenu;
    private NewUserMenu newUserMenu;
    private Transaction transaction;
    private TransactionGatherer transactionGatherer;
    public Scanner scanner;
    private Connection connection;
    private DataBaseHandler dataBaseHandler;

    public App() {
        this.userMenu = new UserMenu(this);
        this.loginMenu = new LoginMenu(this);
        this.newUserMenu = new NewUserMenu(this);
        this.activeMenu = new LoginMenu(this);
        this.transaction = new Transaction(this);
        this.transactionGatherer = new TransactionGatherer(this);
        this.dataBaseHandler = new DataBaseHandler(this);
        scanner = new Scanner(System.in);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/eckofox/efhomefinancialdb/login-screen.fxml"));
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Welcome to EF Home Financial");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args){
            App app = new App();
            app.dataBaseHandler.settingUpConnectionAndTables();
            launch();

            try {
                app.connection.close();
            } catch (SQLException exception) {
                System.err.println("Could not close connection.");
            }
    }


    public void setActiveUser(User user) {
        this.activeUser = user;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public Menu getLoginMenu() {
        return loginMenu;
    }

    public Menu getUserMenu() {
        return userMenu;
    }

    public Menu getNewUserMenu() {
        return newUserMenu;
    }

    public void setActiveMenu(Menu activeMenu) {
        this.activeMenu = activeMenu;
    }

    public TransactionGatherer getTransactionGatherer() {
        return transactionGatherer;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}