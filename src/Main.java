import marketplace.BookMarketplace;
import marketplace.Marketing;
import model.Book;
import model.PurchaseData;
import model.User;
import security.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:./bookmarketdb", "sa", "");
            BookMarketplace store = new BookMarketplace(conn);

            User alice = new User("Alice", "customer", "123 Main St", true);
            User bob = new User("Bob", "vendor", "456 Vendor Ln", false);
            User admin = new User("Admin", "marketplace", "HQ", false);

            conn.createStatement().executeUpdate(
                    "MERGE INTO Users (name, role, address, consentToMarketing) KEY(name) VALUES " +
                            "('Alice', 'customer', '123 Main St', true)"
            );
            conn.createStatement().executeUpdate(
                    "MERGE INTO Users (name, role, address, consentToMarketing) KEY(name) VALUES " +
                            "('Bob', 'vendor', '456 Vendor Ln', false)"
            );
            conn.createStatement().executeUpdate(
                    "MERGE INTO Users (name, role, address, consentToMarketing) KEY(name) VALUES " +
                            "('Admin', 'marketplace', 'HQ', false)"
            );

            Book book1 = new Book(
                    "Core Java",
                    "Yasu",
                    40,
                    bob.name,
                    2023,
                    "1st",
                    "TechBooks",
                    "New",
                    "Comprehensive Java guide",
                    1 // initial stock, will be updated by offer logic
            );

            System.out.println("Offering book to the marketplace...");
            store.offer(book1, bob);

            System.out.println("\nSearch results for 'Java':");
            List<Book> results = store.search("Java", alice);
            if (!results.isEmpty()) {
                for (Book b : results) {
                    System.out.println(b);
                }
            }


            System.out.println("\nAttempting purchase...");
            String confirmation = store.purchase(1, alice, 40);
            System.out.println(confirmation);

            System.out.println("\nBooks currently in stock:");
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM Books WHERE stock > 0"
            );
            ResultSet rs = ps.executeQuery();
            boolean anyInStock = false;
            while (rs.next()) {
                anyInStock = true;
                Book b = new Book(
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getString("vendor"),
                        rs.getInt("publication_year"),
                        rs.getString("edition"),
                        rs.getString("publisher"),
                        rs.getString("book_condition"),
                        rs.getString("description"),
                        rs.getInt("stock")
                );
                System.out.println(b);
            }
            if (!anyInStock) {
                System.out.println("No books currently in stock.");
            }

            System.out.println("\nMarketing report for Admin:");
            Marketing marketing = new Marketing();
            List<PurchaseData> purchases = store.getAllPurchases(conn);
            marketing.marketingData(purchases, admin);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
