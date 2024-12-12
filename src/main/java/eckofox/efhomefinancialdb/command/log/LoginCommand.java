package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.user.User;

import java.io.*;

public class LoginCommand extends Command {
    public LoginCommand(App app) {
        super("login", "login: gets you to the login menu.", app);
    }


    /**
     * loops through the login process, see comment for loginCheck
     * @param commandArgs (user input)
     */
    @Override
    public void run(String commandArgs) {
        String name;
        String password;
            System.out.print("Enter your name: ");
            name = app.scanner.nextLine();
            if (name.equalsIgnoreCase("exit")) {
                System.out.println("Aborting login.");
                return;
            }
            System.out.print("Enter your password: ");
            password = app.scanner.nextLine();
    }

    /**
     * checks first if a user file based on the name input exist (path setting convention is robust enough to allow
     * proper generation.
     * IF NO USER FILE is found, the user is sent out of the loop above to be allowed to create a newuser.
     * @param name self-explanatory
     * @param password self-explanatory
     * @return boolean till loop in 'run' above.
     */
    private void loginCheck(String name, String password) {


    }

}
