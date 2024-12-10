package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.ExitCommand;
import eckofox.efhomefinancialdb.command.log.LoginCommand;
import eckofox.efhomefinancialdb.command.log.NewUserCommand;
import eckofox.efhomefinancialdb.command.log.SeeUserListCommand;

public class LoginMenu extends Menu {
    public LoginMenu(Application application) {
        super("Login Menu", application);
    }

    /**
     * SEE PARENT CLASS, rest is self-explanatory
     */

    @Override
    public void createCommandList() {
        commandList.add(new LoginCommand(application));
        commandList.add(new NewUserCommand(application));
        commandList.add(new SeeUserListCommand(application));
        commandList.add(new ExitCommand(application));
    }

    @Override
    public void runMenu() {
        application.setActiveMenu(this);
        activeMenuDisplay();
        String args = application.scanner.nextLine();
        readCommand(args);
    }
}
