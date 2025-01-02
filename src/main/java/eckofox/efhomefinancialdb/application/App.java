package eckofox.efhomefinancialdb.application;

import eckofox.efhomefinancialdb.controller.LoginScreenController;
import eckofox.efhomefinancialdb.databasemanager.DataBaseHandler;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionManager;
import eckofox.efhomefinancialdb.user.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class App extends Application {

    /**
     * activeUser and activeMenu are used to set up different phases of the app.
     * for example the activeMenu is referenced for the different menu command
     * activeUser is obviously referenced for accessing/creating userId data.
     */
    private User activeUser;
    private List<Transaction> filteredTransactionList;
    private List<Transaction> allTransactionsList;
    public boolean running = true;

    //private NewUserScreenController newUserMenu;
    private Transaction transaction;
    private TransactionManager transactionManager;
    public Scanner scanner;
    private Connection connection;
    private DataBaseHandler dataBaseHandler;

    public App() {
        this.transaction = new Transaction(this);
        this.transactionManager = new TransactionManager(this);
        this.dataBaseHandler = new DataBaseHandler(this);
        scanner = new Scanner(System.in);
        dataBaseHandler.settingUpConnectionAndTables();
        filteredTransactionList = new ArrayList<>();
        allTransactionsList = new ArrayList<>();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/eckofox/efhomefinancialdb/login-screen.fxml"));

        fxmlLoader.setControllerFactory(type -> { //type = classtype
            if (type == LoginScreenController.class) { //if class is the login controller it sends 'app' in the special
                return new LoginScreenController(this); // constructor, not normally OK for FXML controller.
            }
            return null;
        });

        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Welcome to EF Home Financial");

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {


        launch(); //NOTE: launch makes an instance of app


    }


    public void setActiveUser(User user) {
        this.activeUser = user;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public List<Transaction> getFilteredTransactionList() {
        return filteredTransactionList;
    }

    public List<Transaction> getAllTransactionsList() {
        return allTransactionsList;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public DataBaseHandler getDataBaseHandler() {
        return dataBaseHandler;
    }
}