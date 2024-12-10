package eckofox.efhomefinancialdb.command.user;

import eckofox.efhomefinancialdb.application.Application;
import eckofox.efhomefinancialdb.command.Command;
import eckofox.efhomefinancialdb.user.account.Account;

public class BalanceCommand extends Command {

    public BalanceCommand(Application application) {
        super("see-balance",
                "see-balance: shows the balance of the checking and saving account.", application);
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
        application.getActiveUser().getAcountList().stream()
                .peek(Account::setBalanceFromTransactions)
                .forEach(a -> System.out.print(a.getName() + ": " + a.getBalance() + " | "));
        System.out.println();
    }


}
