package eckofox.efhomefinancialdb.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.user.account.Account;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User implements DataBaseManager {
    private App app;
    private String name;
    private String password;
    private List<Account> acountList = new ArrayList<>();


    public User(App app, String name, String password) {
        this.app = app;
        this.name = name;
        this.password = password;

    }

    /**
     * sets user paths, creates directory and file and writes username and password
     */
    @Override
    public void saving() {
        insertData();
        createAccounts();
        System.out.println("User-" + name + " data saved");
    }


    public void createTable() {
        File userDir = new File("dirPath");
        if (userDir.exists()) {
            return;
        }
        userDir.mkdirs();
        System.out.println("User directory created.");
    }

    @Override
    public void toBEREMOVEDcreateFile() {
        createTable();
        File userFile = new File("filepath");
        if (userFile.exists()) {
            return;
        }
        try {
            userFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Could not create user file.");
        }
    }

    @Override
    public void insertData() {
        try {
            toBEREMOVEDcreateFile();
            File userFile = new File("filepath");
            FileWriter writer = new FileWriter(userFile);
            writer.append(name).append("\n");
            writer.append(password);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not edit user file.");
        }
    }

    @Override
    public void fetchData() {/*not used in this class*/}

    public void createAccounts() {
        acountList.add(new CheckingAccount(app, this));
        acountList.add(new SavingAccount(app, this));
    }

    public String getName() {
        return name;
    }

    public String getDirPath() {
        return "dirPath";
    }

    public List<Account> getAcountList() {
        return acountList;
    }
}
