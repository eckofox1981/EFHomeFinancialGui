package eckofox.efhomefinancialdb.application;

import eckofox.efhomefinancialdb.menu.LoginMenu;
import eckofox.efhomefinancialdb.menu.Menu;
import eckofox.efhomefinancialdb.menu.NewUserMenu;
import eckofox.efhomefinancialdb.menu.UserMenu;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionGatherer;
import eckofox.efhomefinancialdb.user.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.Scanner;

public class Application extends javafx.application.Application {

    /**
     * activeUser and activeMenu are used to set up different phases of the application.
     * for example the activeMenu is referenced for the different menu command
     * activeUser is obviously referenced for accessing/creating user data.
     */
    private User activeUser;
    private Menu activeMenu;

    public boolean running = true;
    private final String dirPath = "./zuluFiles/Users/"; //zuluFiles so it ends up at the bottom of the project window (zulu = z)

    private UserMenu userMenu;
    private LoginMenu loginMenu;
    private NewUserMenu newUserMenu;
    private Transaction transaction;
    private TransactionGatherer transactionGatherer;
    public Scanner scanner;

    public Application() {
        this.userMenu = new UserMenu(this);
        this.loginMenu = new LoginMenu(this);
        this.newUserMenu = new NewUserMenu(this);
        this.activeMenu = new LoginMenu(this);
        this.transaction = new Transaction(this);
        this.transactionGatherer = new TransactionGatherer(this);
        scanner = new Scanner(System.in);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(eckofox.efhomefinancialdb.HelloApplication.class.getResource("login-screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("Welcome");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args){
            Application application = new Application();
            application.loginMenu.createCommandList();
            application.userMenu.createCommandList();

            launch();
            application.activeMenu = application.loginMenu;

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

    public String getDirPath() {
        return dirPath;
    }

}