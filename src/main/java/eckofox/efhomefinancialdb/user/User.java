package eckofox.efhomefinancialdb.user;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.filemanager.FileManager;
import eckofox.efhomefinancialdb.user.account.Account;
import eckofox.efhomefinancialdb.user.account.CheckingAccount;
import eckofox.efhomefinancialdb.user.account.SavingAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User implements FileManager {
    private Application application;
    private String name;
    private String password;
    private List<Account> acountList = new ArrayList<>();
    private String dirPath;
    private String filepath;

    public User(Application application, String name, String password) {
        this.application = application;
        this.name = name;
        this.password = password;
        setPaths();

    }

    /**
     * sets user paths, creates directory and file and writes username and password
     */
    @Override
    public void saving() {
        setPaths();
        fileWriter();
        createAccounts();
        System.out.println("User-" + name + " data saved");
    }

    @Override
    public void createDir() {
        File userDir = new File(dirPath);
        if (userDir.exists()) {
            return;
        }
        userDir.mkdirs();
        System.out.println("User directory created.");
    }

    @Override
    public void createFile() {
        createDir();
        File userFile = new File(filepath);
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
    public void fileWriter() {
        try {
            createFile();
            File userFile = new File(filepath);
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
    public void fileReader() {/*not used in this class*/}

    public void createAccounts() {
        acountList.add(new CheckingAccount(application, this));
        acountList.add(new SavingAccount(application, this));
    }

    public void setPaths() {
        dirPath = application.getDirPath() + name + "/";
        filepath = dirPath + "user - " + name + ".txt";
    }

    public String getName() {
        return name;
    }

    public String getDirPath() {
        return dirPath;
    }

    public List<Account> getAcountList() {
        return acountList;
    }
}
