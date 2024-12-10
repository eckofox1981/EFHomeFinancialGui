package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;

public class NewUserCommand extends Command {
    public NewUserCommand(App app) {
        super("newuser", "newuser: creates a new user.", app);
    }

    //just send to the new user menu
    @Override
    public void run(String commandArgs) {
        getApplication().getNewUserMenu().runMenu();
    }
}
