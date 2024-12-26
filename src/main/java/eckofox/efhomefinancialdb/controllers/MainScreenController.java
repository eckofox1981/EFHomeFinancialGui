package eckofox.efhomefinancialdb.controllers;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.date.DateUtility;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.spi.CalendarDataProvider;

public class MainScreenController {
    private App app;
    private Stage stage;

    public MainScreenController(App app) {
        this.app = app;
    }

    public void initData (Stage stage){
        this.stage = stage;
        settingUpDashBoard();
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
    private Label checkingAccountLabel;
    @FXML
    private Label savingAccountLabel;
    @FXML
    private TableView latestTransactionsTable;
    //-----------------TRANSACTIONS-----------------
    @FXML
    private TitledPane transactionsPane;
    @FXML
    private ChoiceBox fromAccountDropDown;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox typeDropDown;
    @FXML
    private TextField amountField;
    @FXML
    private TextField commentField;
    @FXML
    private Button enterButton;
    @FXML
    private DatePicker firstDayPicker;
    @FXML
    private CheckBox earningsCheckBox;
    @FXML
    private CheckBox spendingsCheckBox;
    @FXML
    private CheckBox allTimeCheckBox;
    @FXML
    private CheckBox dayCheckBox;
    @FXML
    private CheckBox weekCheckBox;
    @FXML
    private CheckBox monthCheckBox;
    @FXML
    private CheckBox getEarningsCheckBox;
    @FXML
    private TableView transactionsTable;
    @FXML
    private Button deleteButton;
    //-----------------HELP-----------------
    @FXML
    private TitledPane helpPane;

    public void settingUpDashBoard(){
        userNameLabel.setText("- " + app.getActiveUser().getUsername() + " -");
        realNameLabel.setText(app.getActiveUser().getFirstname() + " " + app.getActiveUser().getLastname());
        joinedLabel.setText(getJoinedMonthAndYear());
        checkingAccountLabel.setText("Checking Account: " + "");
        savingAccountLabel.setText("Saving Account: " + "");
        //TODO set up tableview.
        accordion.setExpandedPane(dashboardPane);
    }

    @FXML
    private void enter() {

    }

    @FXML
    private void firstDayPicker() {

    }

    @FXML
    private void transactionTypeCheckBox() {

    }

    @FXML
    private void allTimeCheckBox() {

    }

    @FXML
    private void dayCheckBox() {

    }

    @FXML
    private void weekCheckBox() {

    }

    @FXML
    private void monthCheckBox() {

    }

    @FXML
    private void yearCheckBox() {

    }

    @FXML
    private void deletePressed() {

    }

    @FXML
    private void enterPressed(){

    }

    @FXML
    private void deleteSelectedTransaction(){

    }

    @FXML
    private  void resettingMsgColor() {

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
