package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;

public class LogOutCommand extends Command {

    public LogOutCommand(Application application) {
        super("logout", "logout: logs you out of the application adn returns you to the login menu.", application);
    }

    /**
     * sets active user to null and returns to login menu.
     * @param commandArgs
     */
    @Override
    public void run(String commandArgs) {
        application.setActiveUser(null);
        System.out.println("You're have been logged out.");
        application.getLoginMenu().runMenu();
    }
}
