package eckofox.efhomefinancialdb.command.log;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SeeUserListCommand extends Command {


    public SeeUserListCommand(App app) {
        super("seeuser", "seeuser: shows you the list of registered users.", app);
    }

    @Override
    public void run(String commandArgs) {
        System.out.print("List of registered users: ");
        showUser();
        System.out.println();
    }


    /**
     * ask collectUsers() to create a String-list of usernames (i.e string not the object itself)
     * and prints each in the terminal.
     */
    public void showUser() {

    }

    /**
     * checks the users' directories -> grabs the name of said directory (= username) -> adds String to List
     * @return the compiled List<String>.
     */
    private void collectUsers() {



    }
}
