package eckofox.efhomefinancialdb.command;

import eckofox.efhomefinancialdb.application.Application;

public class ExitCommand extends Command {
    public ExitCommand(Application application) {
        super("exit", "exit: Terminates the application.", application);
    }

    /** self-explanatory
     */
    @Override
    public void run(String commandArgs) {
        getApplication().running = false;
    }
}
