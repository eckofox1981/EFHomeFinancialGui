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
        List<String> userList = collectUsers();
        userList.forEach(a -> System.out.print(a + " | "));
    }

    /**
     * checks the users' directories -> grabs the name of said directory (= username) -> adds String to List
     * @return the compiled List<String>.
     */
    private List<String> collectUsers() {
        List<String> userList = new ArrayList<>();

        File usersDir = new File(app.getDirPath());
        if (!usersDir.exists() || !usersDir.isDirectory()) {
            System.out.println("No user registered.");
            return userList;
        }

        File[] userDirectories = usersDir.listFiles(File::isDirectory);
        if (userDirectories == null || userDirectories.length == 0) {
            System.out.println("No user registered.");
            return userList;
        }

        for (File userFile : userDirectories) {
            userList.add(userFile.getName());
        }
        return userList;
    }
}
