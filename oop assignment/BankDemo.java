import java.util.ArrayList;
import java.util.List;

public class BankDemo {

    public static void main(String[] args) {

        List<Account> accounts = new ArrayList<>();

        // Create Savings Accounts
        accounts.add(new SavingsAccount("S001", 1000.00, 500.00));
        accounts.add(new SavingsAccount("S002", 800.00, 600.00));

        // Create Current Accounts
        accounts.add(new CurrentAccount("C001", 500.00, 300.00));
        accounts.add(new CurrentAccount("C002", 200.00, 500.00));

        System.out.println("===== BANK ACCOUNT DEMO =====");

        for (Account account : accounts) {

            System.out.println("\nAccount: " + account.accountNumber);
            System.out.println("Starting balance: $"
                    + account.getBalance());

            // Polymorphic method call
            account.withdraw(700.00);

            System.out.println("Balance after withdrawal: $"
                    + account.getBalance());

            // Polymorphic method call
            account.endOfMonth();

            System.out.println("Final balance: $"
                    + account.getBalance());
        }

        System.out.println("\n===== EDGE CASES =====");

        // Savings withdrawal rejected
        Account savings = new SavingsAccount("S003", 1000.00, 500.00);

        System.out.println("\nSavings Account S003");
        System.out.println("Starting balance: $"
                + savings.getBalance());

        savings.withdraw(600.00);

        System.out.println("Balance: $"
                + savings.getBalance());

        // Current account goes into overdraft
        Account current = new CurrentAccount("C003", 100.00, 500.00);

        System.out.println("\nCurrent Account C003");
        System.out.println("Starting balance: $"
                + current.getBalance());

        current.withdraw(300.00);

        System.out.println("Balance: $"
                + current.getBalance());
    }
}