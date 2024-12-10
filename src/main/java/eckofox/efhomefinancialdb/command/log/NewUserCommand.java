package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;

public class NewUserCommand extends Command {
    public NewUserCommand(Application application) {
        super("newuser", "newuser: creates a new user.", application);
    }

    //just send to the new user menu
    @Override
    public void run(String commandArgs) {
        getApplication().getNewUserMenu().runMenu();
    }
}
