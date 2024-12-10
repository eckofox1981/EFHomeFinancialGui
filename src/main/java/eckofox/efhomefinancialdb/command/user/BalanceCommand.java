package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.App;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.user.account.Account;

public class BalanceCommand extends Command {

    public BalanceCommand(App app) {
        super("see-balance",
                "see-balance: shows the balance of the checking and saving account.", app);
    }

    @Override
    public void run(String commandArgs) {
        System.out.println("The balance of your accounts as of now:");
        showBalance();
    }

    /**
     * before printing the registered balance, a double-check of all transactions is performed.
     */
    private void showBalance() {
        app.getActiveUser().getAcountList().stream()
                .peek(Account::setBalanceFromTransactions)
                .forEach(a -> System.out.print(a.getName() + ": " + a.getBalance() + " | "));
        System.out.println();
    }


}
