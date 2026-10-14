public class CurrentAccount extends Account {

    private final double overdraftLimit;
    private final double MONTHLY_FEE = 10.00;

    public CurrentAccount(String accountNumber, double balance,
                           double overdraftLimit) {
        super(accountNumber, balance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {

        if (amount <= 0) {
            System.out.println("Withdrawal rejected: amount must be positive.");
        } else if (balance - amount < -overdraftLimit) {
            System.out.println("Current account withdrawal rejected: "
                    + "overdraft limit of $" + overdraftLimit
                    + " exceeded.");
        } else {
            balance -= amount;
            System.out.println("Current account withdrawal successful: $"
                    + amount);

            if (balance < 0) {
                System.out.println("Account is now in overdraft.");
            }
        }
    }

    @Override
    public void endOfMonth() {
        balance -= MONTHLY_FEE;

        System.out.println("Monthly maintenance fee deducted: $"
                + MONTHLY_FEE);
        System.out.println("New balance: $" + balance);
    }
}