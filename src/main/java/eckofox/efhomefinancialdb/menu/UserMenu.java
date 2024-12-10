package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.ExitCommand;
import eckofox.efhomefinancialdb.command.log.LogOutCommand;
import eckofox.efhomefinancialdb.command.user.*;

public class UserMenu extends Menu {
    public UserMenu(Application application) {
        super("User Menu", application);
    }

    /**
     * SEE PARENT CLASS, rest is self-explanatory
     */

    @Override
    public void createCommandList() {
        commandList.add(new BalanceCommand(application));
        commandList.add(new DeleteCommand(application));
        commandList.add(new EnterDeposit(application));
        commandList.add(new EnterWithdrawal(application));
        commandList.add(new TransferCommand(application));
        commandList.add(new ViewCommand(application));
        commandList.add(new LogOutCommand(application));
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
