package eckofox.efhomefinancialdb.command;

import eckofox.efhomefinancialdb.application.App;

public abstract class Command {
    protected App app;
    private String command;
    private String description;

    /**
     *
     * @param command
     * @param description
     * @param app
     * all COMMANDS have at least run() and showDescription() (implemented from CommandInterface)
     * other methods are private and used within the various command for its own purpose (extending from Command
     * but otherwise self-sustaining with some exceptions like dateUtility in ViewCommand).
     */
    public Command(String command, String description, App app) {
        this.command = command;
        this.description = description;
        this.app = app;
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

    public App getApplication() {
        return app;
    }
}
