package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.ExitCommand;
import eckofox.efhomefinancialdb.command.log.LogOutCommand;
import eckofox.efhomefinancialdb.command.user.*;

public class UserMenu extends Menu {
    public UserMenu(App app) {
        super("User Menu", app);
    }

    /**
     * SEE PARENT CLASS, rest is self-explanatory
     */

    @Override
    public void createCommandList() {
        commandList.add(new BalanceCommand(app));
        commandList.add(new DeleteCommand(app));
        commandList.add(new EnterDeposit(app));
        commandList.add(new EnterWithdrawal(app));
        commandList.add(new TransferCommand(app));
        commandList.add(new ViewCommand(app));
        commandList.add(new LogOutCommand(app));
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
