import exception.AuthenticationException;
import exception.BankingException;
import model.Account;
import model.User;
import persistence.DataStore;
import persistence.FileManager;
import service.AccountService;
import service.AuthService;
import service.TransactionService;
import util.SessionManager;

import java.io.Console;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner INPUT = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            FileManager files = new FileManager("data");
            DataStore store = new DataStore(files);
            AuthService auth = new AuthService(store);
            AccountService accounts = new AccountService(store);
            TransactionService tx = new TransactionService(store, accounts);

            banner();
            boolean running = true;
            while (running) {
                if (!SessionManager.getInstance().isLoggedIn()) {
                    running = guestMenu(auth);
                } else {
                    running = userMenu(accounts, tx);
                }
            }
            System.out.println("Goodbye.");
        } catch (BankingException e) {
            System.err.println("Fatal error: " + e.getMessage());
        }
    }

    private static boolean guestMenu(AuthService auth) {
        System.out.println("\n=== Secure Bank ===");
        System.out.println("1) Register");
        System.out.println("2) Login");
        System.out.println("0) Exit");
        String choice = prompt("Choose: ");
        switch (choice) {
            case "1" -> doRegister(auth);
            case "2" -> doLogin(auth);
            case "0" -> { return false; }
            default  -> System.out.println("Invalid option.");
        }
        return true;
    }

    private static boolean userMenu(AccountService accounts, TransactionService tx) {
        User user = SessionManager.getInstance().requireUser();
        System.out.println("\n=== Secure Bank ===");
        System.out.println("Logged in as: " + user.getFullName() +
                " (" + user.getUsername() + ")");
        System.out.println("1) Create new account");
        System.out.println("2) View my accounts");
        System.out.println("3) Deposit");
        System.out.println("4) Withdraw");
        System.out.println("5) Logout");
        String choice = prompt("Choose: ");
        try {
            switch (choice) {
                case "1" -> {
                    Account a = accounts.createAccount(user);
                    System.out.println("Account created: " + a.getAccountNumber());
                }
                case "2" -> viewAccounts(accounts, user);
                case "3" -> doTransaction(accounts, tx, user, true);
                case "4" -> doTransaction(accounts, tx, user, false);
                case "5" -> SessionManager.getInstance().logout();
                default  -> System.out.println("Invalid option.");
            }
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private static void doRegister(AuthService auth) {
        String username = prompt("Username: ");
        String fullName = prompt("Full name: ");

        char[] password = readPassword("Password");
        char[] confirm  = readPassword("Confirm password");

        try {
            if (!Arrays.equals(password, confirm)) {
                System.out.println("Error: passwords do not match.");
                return;
            }
            User u = auth.register(username, fullName, password);
            System.out.println("Registered as " + u.getUsername() + ". Please log in.");
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            Arrays.fill(confirm, '\0');
        }
    }

    private static void doLogin(AuthService auth) {
        String username = prompt("Username: ");
        char[] password = readPassword("Password");
        try {
            User u = auth.login(username, password);
            SessionManager.getInstance().login(u);
            System.out.println("Welcome, " + u.getFullName() + ".");
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (BankingException e) {
            System.out.println("Login error.");
        }
    }

    private static void viewAccounts(AccountService accounts, User user) {
        List<Account> list = accounts.listAccounts(user);
        if (list.isEmpty()) {
            System.out.println("You have no accounts yet.");
            return;
        }
        System.out.println("Your accounts:");
        for (Account a : list) {
            System.out.printf("  %s   balance: %s%n",
                    a.getAccountNumber(), a.getBalance().toPlainString());
        }
    }

    private static void doTransaction(AccountService accounts, TransactionService tx,
                                      User user, boolean deposit) {
        String number = prompt("Account number: ");
        String amount = prompt("Amount: ");
        try {
            BigDecimal balance = deposit
                    ? tx.deposit(user, number, amount)
                    : tx.withdraw(user, number, amount);
            System.out.println("New balance: " + balance.toPlainString());
        } catch (BankingException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Reads a password while masking each typed character with an asterisk. */
    private static char[] readPassword(String label) {
        return READER.readLine(label + ": ", '*').toCharArray();
    }

    private static String prompt(String label) {
        return READER.readLine(label);
    }

    private static void banner() {
        System.out.println("========================================");
        System.out.println("   Secure Banking Application (CLI)");
        System.out.println("========================================");
    }
}