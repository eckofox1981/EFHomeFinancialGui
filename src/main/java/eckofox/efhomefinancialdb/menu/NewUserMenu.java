package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.user.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewUserMenu extends Menu {
    public NewUserMenu(Application application) {
        super("create new user", application);
    }

    @Override
    public void runMenu() {
        application.setActiveMenu(this);
        activeMenuDisplay();
        String name;
        do {
            System.out.print("Enter your name: ");
            name = application.scanner.nextLine();
        } while (!userNameCheck(name));

        String password;
        String passwordComfirmation;
        do {
            System.out.print("Enter your password: ");
            password = application.scanner.nextLine();
            System.out.print("Comfirm your password: ");
            passwordComfirmation = application.scanner.nextLine();
            if (!passwordComfirmation.equals(password)) {
                System.out.println("Password comfirmation failed, please try again.");
            }
        } while (!passwordComfirmation.equals(password));

        User user = new User(application, name, password);
        application.setActiveUser(user);
        user.saving();

        application.getUserMenu().runMenu();
    }

    /**
     * check if a file with that username exist and notifies the user accordingly
     * @param userName wishedby the user
     * @return a boolean to allow to continue in the usercreation process
     */
    private boolean userNameCheck(String userName) {
        if (userName.equalsIgnoreCase("exit")){
            System.out.println("'EXIT' is not and available option.");
            return false;
        }
        for (String user : collectUsers()) {
            if (user.equals(userName)) {
                System.out.println("Username already used. Please choose another name.");
                return false;
            }
        }
        return true;
    }

    /**
     * user are collected before being
     * @return as a list in the userNameCheck for inspection
     */
    private List<String> collectUsers() {
        List<String> userList = new ArrayList<>();

        File usersDir = new File(application.getDirPath());
        if (!usersDir.exists() || !usersDir.isDirectory()) {
            return userList;
        }

        File[] userDirectories = usersDir.listFiles(File::isDirectory);
        if (userDirectories == null) {
            return userList;
        }

        for (File userFile : userDirectories) {
            userList.add(userFile.getName());
        }

        return userList;
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
