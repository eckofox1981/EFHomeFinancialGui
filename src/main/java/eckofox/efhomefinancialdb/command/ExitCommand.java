package eckofox.efhomefinancialdb.command;

import eckofox.efhomefinancialdb.application.App;

public class ExitCommand extends Command {
    public ExitCommand(App app) {
        super("exit", "exit: Terminates the app.", app);
    }

    /** self-explanatory
     */
    @Override
    public void run(String commandArgs) {
        getApplication().running = false;
    }
}
