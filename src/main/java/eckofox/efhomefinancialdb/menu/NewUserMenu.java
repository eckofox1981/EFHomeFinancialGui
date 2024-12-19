package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.user.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewUserMenu extends Menu {
    public NewUserMenu(App app) {
        super("create new user", app);
    }

    @Override
    public void runMenu() {
        app.setActiveMenu(this);
        activeMenuDisplay();
        String name;
        do {
            System.out.print("Enter your name: ");
            name = app.scanner.nextLine();
        } while (!userNameCheck(name));

        String password;
        String passwordComfirmation;
        do {
            System.out.print("Enter your password: ");
            password = app.scanner.nextLine();
            System.out.print("Comfirm your password: ");
            passwordComfirmation = app.scanner.nextLine();
            if (!passwordComfirmation.equals(password)) {
                System.out.println("Password comfirmation failed, please try again.");
            }
        } while (!passwordComfirmation.equals(password));

//        User user = new User(app, name, password);
//        app.setActiveUser(user);
//        user.saving();

        app.getUserMenu().runMenu();
    }

    /**
     * check if a file with that username exist and notifies the user accordingly
     * @param userName wishedby the user
     * @return a boolean to allow to continue in the usercreation process
     */
    private boolean userNameCheck(String userName) {
        return true;
    }

    /**
     * user are collected before being
     * @return as a list in the userNameCheck for inspection
     */
    private void collectUsers() {
        //used to return a list
    }

    /**
     * overriden to NOT display commands (none available as the menu only lets the user
     * create a user-profile).
     */
    @Override
    protected void activeMenuDisplay() {
        show130DashLine();
        System.out.println(name.toUpperCase());
        show130DashLine();
    }
}
