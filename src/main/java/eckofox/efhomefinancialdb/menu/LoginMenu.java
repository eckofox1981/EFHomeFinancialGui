package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.ExitCommand;
import eckofox.efhomefinancialdb.command.log.LoginCommand;
import eckofox.efhomefinancialdb.command.log.NewUserCommand;
import eckofox.efhomefinancialdb.command.log.SeeUserListCommand;

public class LoginMenu extends Menu {
    public LoginMenu(App app) {
        super("Login Menu", app);
    }

    /**
     * SEE PARENT CLASS, rest is self-explanatory
     */

    @Override
    public void createCommandList() {
        commandList.add(new LoginCommand(app));
        commandList.add(new NewUserCommand(app));
        commandList.add(new SeeUserListCommand(app));
        commandList.add(new ExitCommand(app));
    }

    @Override
    public void runMenu() {
        app.setActiveMenu(this);
        activeMenuDisplay();
        String args = app.scanner.nextLine();
        readCommand(args);
    }
}
