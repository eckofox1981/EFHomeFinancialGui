package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;

public class LogOutCommand extends Command {

    public LogOutCommand(App app) {
        super("logout", "logout: logs you out of the app adn returns you to the login menu.", app);
    }

    /**
     * sets active user to null and returns to login menu.
     * @param commandArgs
     */
    @Override
    public void run(String commandArgs) {
        app.setActiveUser(null);
        System.out.println("You're have been logged out.");
        app.getLoginMenu().runMenu();
    }
}
