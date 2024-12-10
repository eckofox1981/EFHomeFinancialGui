package eckofox.efhomefinancialdb.menu;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu {
    protected Application application;
    protected String name;
    protected List<Command> commandList = new ArrayList<>();

    public Menu(String name, Application application) {
        this.name = name;
        this.application = application;
    }

    /**
     * everytime a menu is run, it is set active which, with the same methods prints a different output of command
     * list.
     */
    public void runMenu() {
        application.setActiveMenu(this);
        activeMenuDisplay();
    }

    /**
     * basically just edits layout of the menu-header according to the menu's setup
     */
    protected void activeMenuDisplay() {
        show130DashLine();
        System.out.println(name.toUpperCase());
        displayCommandList();
        show130DashLine();
    }

    protected void createCommandList() {/*empty for overrides in each child class*/}

    protected void displayCommandList() {
        System.out.print("Commands: ");
        for (Command command : commandList) {
            if (command == commandList.getLast()) {
                System.out.println(command.getCommand() + " | help");
                break;
            }
            System.out.print(command.getCommand() + " | ");
        }
    }

    protected void readCommand(String commandArgs) {
        String[] splitCommand = commandArgs.split(" ");

        if (splitCommand[0].equalsIgnoreCase("help")) {
            showHelp();
            return;
        }

        for (Command command : commandList) {
            if (splitCommand[0].equalsIgnoreCase(command.getCommand())) {
                command.run(commandArgs);
                return;
            }
        }
        System.out.println("Invalid command, please try again or type help.");
    }

    protected void showHelp() {
        commandList.forEach(Command::showDescription);
    }

    protected void show130DashLine() {
        for (int i = 0; i < 130; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
}
