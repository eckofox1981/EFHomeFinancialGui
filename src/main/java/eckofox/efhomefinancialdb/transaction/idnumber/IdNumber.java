package eckofox.efhomefinancialdb.transaction.idnumber;

import eckofox.efhomefinancialdb.databasemanager.DataBaseManager;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.user.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;


public class IdNumber implements DataBaseManager {

    private int idNumber;
    private int idNumberOnFile;
    private String dirPath;
    private String filePath;
    private Transaction transaction;
    private User user;

    /**
     * since ID numbers are used for sorting and deleting transaction, I've made sure
     * they're generated autonomously in generateIdNumber() (just below) every time a new transaction is recorded.
     * @param transaction (every new transaction is directly assigned to the idNumber)
     */
    public IdNumber(Transaction transaction) {
        this.transaction = transaction;
        user = transaction.getUserId();
    }

    /**
     * the file reader checks the last recorded id-number on record and assigns the value to idNUmberOnfile
     * idNumber is assigned idNumberOnFile + 1
     * the fileWrtier records this in the ledger.
     */
    public void generateIdNumber() {
        fetchData();
        idNumber = idNumberOnFile + 1;
        insertData();
    }


    public void toBEREMOVEDsetPaths() {
        dirPath = user.getDirPath();
        filePath = dirPath + "idHistory - " + user.getUsername() + ".txt";
    }

    @Override
    public void saving() {/*not necessary for this class*/}

    public void createTable() {
        File historyDir = new File(dirPath);
        if (historyDir.exists()) {
            return;
        }
        historyDir.mkdirs();
        System.out.println("ID Numbers directory created");
    }


    public void toBEREMOVEDcreateFile() {
        createTable();
        File historyFile = new File(filePath);
        if (historyFile.exists()) {
            return;
        }
        try {
            historyFile.createNewFile();
            System.out.println("ID-number history created.");
        } catch (IOException e) {
            System.out.println("Could not create ID history file.");
        }
    }

    @Override
    public void insertData() {
        try {
            FileWriter writer = new FileWriter(filePath, true);

            if (idNumberOnFile == 0) {
                writer.append(String.valueOf(idNumber));
            } else {
                writer.append("\n").append(String.valueOf(idNumber));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not edit ID history file.");
        }
    }

    @Override
    public void fetchData() {
        toBEREMOVEDsetPaths();
        File historyFile = new File(filePath);
        if (!historyFile.exists()) {
            toBEREMOVEDcreateFile();
        }

        try (RandomAccessFile idFile = new RandomAccessFile(historyFile, "r")) {
            if (idFile.length() == 0) { //if file is empty nothing happens
                System.out.println("No transactions recorded.");
                idNumberOnFile = 0;
                return;
            }

            long idFileLength = idFile.length() - 1;
            StringBuilder lastLine = new StringBuilder();

            // Read from the end of the file
            for (long pointer = idFileLength; pointer >= 0; pointer--) {
                idFile.seek(pointer); // Move the pointer to the current position
                char c = (char) idFile.read();

                // Check for newline and stop if it's found
                if (c == '\n' && pointer != idFileLength) {
                    break;
                }

                lastLine.append(c); // Collect characters
            }

            // Reverse and convert the last line to a string, then trim it
            String lastIdInFileString = lastLine.reverse().toString().trim();

            // Parse the last line as an integer
            idNumberOnFile = Integer.parseInt(lastIdInFileString);

        } catch (NumberFormatException e) {
            System.out.println("Unable to read ID-history.");
        } catch (IOException x) {
            System.out.println("ID-file not found.");
        }

    }

    public int getIdNumber() {
        return idNumber;
    }

}
