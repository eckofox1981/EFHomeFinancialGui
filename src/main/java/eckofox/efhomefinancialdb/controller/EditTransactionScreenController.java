package eckofox.efhomefinancialdb.controller;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.date.DateUtility;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.account.Account;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.ParseException;
import java.util.Date;
import java.util.EnumSet;

public class EditTransactionScreenController {
    public VBox msgVbox;
    public Label lastEntryTitle;
    private App app;
    private Stage stage;
    private Transaction transaction;

    /** see LoginScreen ln 46
     */
    public EditTransactionScreenController(App app) {
        this.app = app;
    }

    @FXML
    private ComboBox<Account> fromAccountDropDown;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<TransactionType> typeDropDown;
    @FXML
    private TextField amountField;
    @FXML
    private TextField commentField;
    @FXML
    private Label msgBox;
    @FXML
    private Button editButton;
    @FXML
    private Button cancelButton;

    /** initializes the menus and sets the values of the fields based on the transaction to be edited (hence
     * Transaction as a parameter for the method)
     * @param stage needed for the stage to be setup
     * @param transaction to be edited, transaction values and method will be used to sav edits.
     */
    public void initData (Stage stage, Transaction transaction) {
        this.stage = stage;
        this.transaction = transaction;
        initializeDropDowns();
        fromAccountDropDown.setValue(transaction.getFromAccount());
        datePicker.setValue(DateUtility.dateToLocalDateConverter(transaction.getDate()));
        typeDropDown.setValue(transaction.getTransactionType());
        amountField.setText(Double.toString(transaction.getAmount()));
        commentField.setText(transaction.getComment());
    }

    /** self-explanatory
     */
    private void initializeDropDowns() {
        typeDropDown.getItems().setAll(FXCollections.observableArrayList(EnumSet.allOf(TransactionType.class)));
        fromAccountDropDown.getItems().addAll(app.getActiveUser().getAcountList());

        //this allows to use the dropdown menu as an "object container" for the accounts
        fromAccountDropDown.setConverter(new StringConverter<>() {
            public String toString(Account account) {
                return (account != null) ? account.getName() : "";
            }

            public Account fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                for (Account account : fromAccountDropDown.getItems()) {
                    if (account != null && account.getName().equals(string)) {
                        return account; //
                    }
                }
                return null;
            }
        });
    }

    /** uses dropdowns and fields values are sets them to the transaction
     * calls Transaction.insertUpdateData()
     * A "Transaction edited" message is displayed for 1.5 sec before closing the window
     */
    @FXML
    private void setEditButton () {
        double amount;
        Date date = null;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            msgBox.setText("Could not read amount, please check input. " + e.getMessage());
            return;
        }
        try {
            date = DateUtility.convertDateString(datePicker.getValue().toString());
        } catch (ParseException e) {
            msgBox.setText("Could not recognize date, please check input. " + e.getMessage());
        }
        transaction.setFromAccount(fromAccountDropDown.getValue());
        transaction.setDate(date);
        transaction.setTransactionType(typeDropDown.getValue());
        transaction.setAmount(amount);
        transaction.setComment(commentField.getText());

        transaction.insertUpdateData();

        msgBox.setText("Transaction edited, this window will close.");

        Stage currenStage = (Stage) editButton.getScene().getWindow();

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> currenStage.close());
        pause.play();
    }

    /** exits the edittransaction-screen
     */
    @FXML
    private void setCancelButton() {
        Stage currenStage = (Stage) cancelButton.getScene().getWindow();
        currenStage.close();
    }

    /** @param keyEvent calls setEditButton
     */
    @FXML
    private void enterKey (KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            setEditButton();
        }
    }

    /** @param keyEvent calls setCancelButton
     */
    @FXML
    private void escapeKey (KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
            setCancelButton();
        }
    }


}
