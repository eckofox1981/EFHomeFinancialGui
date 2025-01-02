package eckofox.efhomefinancialdb.controller;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.date.DateUtility;
import eckofox.efhomefinancialdb.transaction.Transaction;
import eckofox.efhomefinancialdb.transaction.TransactionType;
import eckofox.efhomefinancialdb.user.account.Account;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class MainScreenController {
    private App app;
    private Stage stage;

    public MainScreenController(App app) {
        this.app = app;
    }

    public void initData(Stage stage) {
        this.stage = stage;
        settingUpDashBoard();
        initializeTransactionPane();
        accordion.setExpandedPane(dashboardPane);
    }

    @FXML
    private Accordion accordion;
    //-----------------DASHBOARD-----------------
    @FXML
    private TitledPane dashboardPane;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label realNameLabel;
    @FXML
    private Label joinedLabel;
    @FXML
    private Button editUserButton;
    @FXML
    private Label checkingAccountLabel;
    @FXML
    private Label savingAccountLabel;
    @FXML
    private TableView allTransactionsTable;
    @FXML
    private TableColumn<Transaction, String> allDateColumn;
    @FXML
    private TableColumn<Transaction, String> allTypeColumn;
    @FXML
    private TableColumn<Transaction, Double> allAmountColumn;
    @FXML
    private TableColumn<Transaction, String> allCommentColumn;

    //-----------------TRANSACTIONS-----------------
    @FXML
    private TitledPane transactionsPane;
    @FXML
    private HBox enterTransactionHBox;
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
    private Button enterButton;
    @FXML
    private HBox filterHbox;
    @FXML
    private DatePicker firstDayPicker;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearchButton;
    @FXML
    private ComboBox searchFromAccountDropDown;
    @FXML
    private CheckBox earningsCheckBox;
    @FXML
    private CheckBox spendingsCheckBox;
    @FXML
    private CheckBox transferCheckBox;
    @FXML
    private CheckBox allTimeCheckBox;
    @FXML
    private CheckBox dayCheckBox;
    @FXML
    private CheckBox weekCheckBox;
    @FXML
    private CheckBox monthCheckBox;
    @FXML
    private CheckBox yearCheckBox;

    @FXML
    private Button deleteButton;
    @FXML
    private Label msgBox;
    @FXML
    private TableView<Transaction> filteredTransactionsTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> typeColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    private TableColumn<Transaction, String> commentColumn;
    //-----------------HELP-----------------
    @FXML
    private TitledPane helpPane;

    public void settingUpDashBoard() {
        app.getActiveUser().getAcountList().forEach(Account::fetchData);
        app.getTransactionManager().gatherAllTransactions();
        userNameLabel.setText("- " + app.getActiveUser().getUsername() + " -");
        realNameLabel.setText(app.getActiveUser().getFirstname() + " " + app.getActiveUser().getLastname());
        joinedLabel.setText(getJoinedMonthAndYear());

        updatingAccountDisplay();

        allDateColumn.setCellValueFactory(cellData -> DateUtility.datePropertyFormat(cellData.getValue().getDate()));
        allTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionType().toString()));
        allAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        allCommentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));

        allTransactionsTable.getItems().setAll(app.getAllTransactionsList());
    }

    @FXML
    private void initializeTransactionPane() {
        //initializes dashboard, dropdown-menus and tables.
        typeDropDown.getItems().setAll(FXCollections.observableArrayList(EnumSet.allOf(TransactionType.class)));
        fromAccountDropDown.getItems().addAll(app.getActiveUser().getAcountList());

        fromAccountDropDown.setConverter(new StringConverter<>() { //to be used as String and Object
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

        typeDropDown.setValue(TransactionType.valueOf("WITHDRAWAL"));
        fromAccountDropDown.setValue(fromAccountDropDown.getItems().get(0));

        dateColumn.setCellValueFactory(cellData -> DateUtility.datePropertyFormat(cellData.getValue().getDate()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransactionType().toString()));
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        commentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));

        searchFromAccountDropDown.getItems().addAll(app.getActiveUser().getAcountList());

        searchFromAccountDropDown.setConverter(new StringConverter<Account>() {
            public String toString(Account account) {
                return (account != null) ? account.getName() : "";
            }

            public Account fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                for (Object item : searchFromAccountDropDown.getItems()) {
                    if (item instanceof Account) {
                        Account account = (Account) item;  // Cast to Account
                        if (account.getName().equals(string)) {
                            return account;
                        }
                    }
                }
                return null;
            }
        });

        filteringTransactions();

        filteredTransactionsTable.getItems().setAll(app.getFilteredTransactionList());
    }

    @FXML
    private void updatingAccountDisplay() {
        app.getActiveUser().getAcountList().forEach(Account::setBalanceFromTransactions);
        checkingAccountLabel.setText(String.format(new Locale("sv", "SE"), "%s: %, .2f SEK",
                app.getActiveUser().getAcountList().getFirst().getName(),
                app.getActiveUser().getAcountList().getFirst().getBalance()));
        savingAccountLabel.setText(String.format(new Locale("sv", "SE"), "%s: %, .2f SEK",
                app.getActiveUser().getAcountList().getLast().getName(),
                app.getActiveUser().getAcountList().getLast().getBalance()));
    }

    //---------------------------------- TRANSACTION FUNCTIONS----------------------------------------------------------
    @FXML
    private void enterTransaction() {
        Double amount = 0.0;
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

        System.out.println(typeDropDown.getValue());
        Transaction transaction = new Transaction(app, app.getActiveUser(), UUID.randomUUID(), typeDropDown.getValue(), fromAccountDropDown.getValue(),
                date, amount, commentField.getText());

        transaction.saving();

        app.getActiveUser().getAcountList().forEach(Account::setBalanceFromTransactions);

        settingUpDashBoard();
        filteringTransactions();
    }

    @FXML
    private void deleteSelectedTransaction() {
        if (filteredTransactionsTable.getSelectionModel().isEmpty()) {
            msgBox.setTextFill(Color.ORANGE);
            msgBox.setText("You need to select a transaction to be deleted.");
            return;
        }
        Transaction selectedTransaction = filteredTransactionsTable.getSelectionModel().getSelectedItem();
        String transactionDescription = String.valueOf(selectedTransaction.getDate().toString() + " " + selectedTransaction
                .getTransactionType().toString() + " " + selectedTransaction.getAmount() + " SEK");

        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete transaction");
        deleteAlert.setContentText("You are about to delete transaction '" + transactionDescription +
                "'. Do you wish to proceed?");
        Optional<ButtonType> result = deleteAlert.showAndWait();

        if (result.isEmpty()) {
            msgBox.setText("Transaction not deleted.");
        } else if (result.get() == ButtonType.OK) {
            selectedTransaction.deleteData();
            app.getActiveUser().getAcountList().forEach(Account::setBalanceFromTransactions);
            updatingAccountDisplay();
            msgBox.setTextFill(Color.ORANGE);
            msgBox.setText("Transaction '" + transactionDescription + "' deleted.");
        } else if (result.get() == ButtonType.CANCEL) {
            msgBox.setText("Transaction not deleted.");
        }

        filteringTransactions();
    }

    //---------------------------------- FILTER FUNCTIONS---------------------------------------------------------------
    @FXML
    private void setEarningsCheckBox() {
        if (earningsCheckBox.isSelected()) {
            spendingsCheckBox.selectedProperty().setValue(false);
            transferCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setSpendingsCheckBox() {
        if (spendingsCheckBox.isSelected()) {
            earningsCheckBox.selectedProperty().setValue(false);
            transferCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setTransferCheckBox() {
        if (transferCheckBox.isSelected()) {
            spendingsCheckBox.selectedProperty().setValue(false);
            earningsCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setAllTimeCheckBox() {
        if (allTimeCheckBox.isSelected()) {
            dayCheckBox.selectedProperty().setValue(false);
            weekCheckBox.selectedProperty().setValue(false);
            monthCheckBox.selectedProperty().setValue(false);
            yearCheckBox.selectedProperty().setValue(false);
            firstDayPicker.setValue(null);
        }
        filteringTransactions();
    }

    @FXML
    private void setDayCheckBox() {
        if (dayCheckBox.isSelected()) {
            allTimeCheckBox.selectedProperty().setValue(false);
            weekCheckBox.selectedProperty().setValue(false);
            monthCheckBox.selectedProperty().setValue(false);
            yearCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setWeekCheckBox() {
        if (weekCheckBox.isSelected()) {
            allTimeCheckBox.selectedProperty().setValue(false);
            dayCheckBox.selectedProperty().setValue(false);
            monthCheckBox.selectedProperty().setValue(false);
            yearCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setMonthCheckBox() {
        if (monthCheckBox.isSelected()) {
            allTimeCheckBox.selectedProperty().setValue(false);
            dayCheckBox.selectedProperty().setValue(false);
            weekCheckBox.selectedProperty().setValue(false);
            yearCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setYearCheckBox() {
        if (yearCheckBox.isSelected()) {
            allTimeCheckBox.selectedProperty().setValue(false);
            dayCheckBox.selectedProperty().setValue(false);
            weekCheckBox.selectedProperty().setValue(false);
            monthCheckBox.selectedProperty().setValue(false);
        }
        filteringTransactions();
    }

    @FXML
    private void setClearSearchButton() {
        firstDayPicker.setValue(null);
        searchField.clear();
        searchFromAccountDropDown.setValue(null);
        earningsCheckBox.selectedProperty().setValue(false);
        spendingsCheckBox.selectedProperty().setValue(false);
        transferCheckBox.selectedProperty().setValue(false);
        allTimeCheckBox.selectedProperty().setValue(true);
        dayCheckBox.selectedProperty().setValue(false);
        weekCheckBox.selectedProperty().setValue(false);
        monthCheckBox.selectedProperty().setValue(false);
        yearCheckBox.selectedProperty().setValue(false);
    }

    @FXML
    private void filteringTransactions() {
        if (firstDayPicker.getValue() == null && !allTimeCheckBox.isSelected()) {
            msgBox.setText("Period selection enabled but first day undefined. Please select a date.");

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(event -> msgBox.setText(""));
            pause.play();
        }

        msgBox.setText(filteringMsg());

        app.getTransactionManager().transactionFilter(firstDayPicker.getValue(), searchField.getText(), (Account) searchFromAccountDropDown.getValue(),
                earningsCheckBox.isSelected(), spendingsCheckBox.isSelected(), transferCheckBox.isSelected(), dayCheckBox.isSelected(),
                weekCheckBox.isSelected(), monthCheckBox.isSelected(), yearCheckBox.isSelected());

        filteredTransactionsTable.getItems().setAll(app.getFilteredTransactionList());
    }

    //---------------------------------- MSG BOX FUNCTIONS--------------------------------------------------------------
    @FXML
    private void resettingMsgColor() {
        msgBox.setTextFill(Color.BLACK);
    }

    private String filteringMsg() {
        StringBuilder message = new StringBuilder("Showing ");
        if (!earningsCheckBox.isSelected() && !spendingsCheckBox.isSelected() && !transferCheckBox.isSelected()) {
            message.append("all ");
        }
        if (earningsCheckBox.isSelected()) {
            message.append("incoming ");
        }
        if (spendingsCheckBox.isSelected()) {
            message.append("outgoing ");
        }
        if (transferCheckBox.isSelected()) {
            message.append("transfer-");
        }

        if (allTimeCheckBox.isSelected()) {
            message.append("transactions");
        } else {
            message.append("transactions for the selected period, starting on ")
                    .append(firstDayPicker.getValue().toString());
        }

        if (!searchFromAccountDropDown.getSelectionModel().isEmpty()) {
            Account account = (Account) searchFromAccountDropDown.getSelectionModel().getSelectedItem();
            message.append(" from " + account.getName());
        }
        message.append(".");
        return new String(message);
    }


    //---------------------------------- KEY EVENTS -----------------------------------------------------------------
    @FXML
    private void enterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            if (enterTransactionHBox.isFocused() && datePicker.getValue() != null && amountField != null) {
                enterTransaction();
            }
            if (filterHbox.isFocused()) {
                filteringTransactions();
            }
        }
    }

    @FXML
    private void deletePressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            deleteSelectedTransaction();
        }
    }

    //---------------------------------- OTHER -------------------------------------------------------------------------
    @FXML
    private void openEditUserScreen () {
        try {
            Stage currenStage = (Stage) editUserButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/eckofox/efhomefinancialdb/edituser-screen.fxml"));

            fxmlLoader.setControllerFactory(type -> {
                if (type == EditUserScreenController.class) {
                    return new EditUserScreenController(app); // To be able to pass the app variable.
                }
                return null;
            });

            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/logo.png"))));
            stage.setTitle("EF Home Financial - Edit user");
            stage.setScene(new Scene(root));

            EditUserScreenController controller = fxmlLoader.getController();
            controller.initData(stage);

            currenStage.close();
            stage.show();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.getStackTrace();
        }
    }

    private String getJoinedMonthAndYear() {
        String joinedDate = "error fetching enrollment date.";
        try (PreparedStatement fetchDateStatement = app.getConnection().prepareStatement("SELECT created_at FROM users WHERE username = ?;")) {
            fetchDateStatement.setString(1, app.getActiveUser().getUsername());
            try {
                ResultSet result = fetchDateStatement.executeQuery();
                if (result.next()) {
                    Timestamp timestamp = result.getTimestamp("created_at");

                    SimpleDateFormat joinedDateFormat = new SimpleDateFormat("MMMM yyyy");
                    joinedDate = "joined " + joinedDateFormat.format(timestamp);
                }
            } catch (SQLException ex) {
                System.err.println("Error fetching date in getJoinedMonthAndYear. " + ex.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Error with getJoinedMonthAndYear statement. " + e.getMessage());
        }
        return joinedDate;
    }
}
