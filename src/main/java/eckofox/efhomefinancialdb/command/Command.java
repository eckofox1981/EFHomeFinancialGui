package eckofox.efhomefinancialdb.command;

import eckofox.efhomefinancialdb.application.Application;

public abstract class Command {
    protected Application application;
    private String command;
    private String description;

    /**
     *
     * @param command
     * @param description
     * @param application
     * all COMMANDS have at least run() and showDescription() (implemented from CommandInterface)
     * other methods are private and used within the various command for its own purpose (extending from Command
     * but otherwise self-sustaining with some exceptions like dateUtility in ViewCommand).
     */
    public Command(String command, String description, Application application) {
        this.command = command;
        this.description = description;
        this.application = application;
    }

    public abstract void run(String commandArgs);

    public void showDescription() {
        System.out.println(getDescription());
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public Application getApplication() {
        return application;
    }
}
