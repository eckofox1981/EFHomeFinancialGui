package eckofox.efhomefinancialdb.controllers;

import eckofox.efhomefinancialdb.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        //TODO method to get month and year account created
        return "joined march 2004";
    }
}
