package marketplace;

import model.*;
import security.*;

import java.sql.*;
import java.util.*;

public class BookMarketplace {
    private final Connection conn;

    public BookMarketplace(Connection conn) throws SQLException {
        this.conn = conn;
        setupDatabase();
    }

    private void setupDatabase() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Users (" +
                "name VARCHAR PRIMARY KEY, " +
                "role VARCHAR NOT NULL, " +
                "address VARCHAR NOT NULL, " +
                "consentToMarketing BOOLEAN DEFAULT FALSE)");

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Books (" +
                "id IDENTITY PRIMARY KEY, " +
                "title VARCHAR NOT NULL, " +
                "author VARCHAR NOT NULL, " +
                "price DOUBLE PRECISION NOT NULL, " +
                "vendor VARCHAR NOT NULL, " +
                "publication_year INT, " +
                "edition VARCHAR, " +
                "publisher VARCHAR, " +
                "book_condition VARCHAR, " +
                "description VARCHAR, " +
                "stock INT DEFAULT 1)");

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Purchases (" +
                "id IDENTITY PRIMARY KEY, " +
                "bookId BIGINT, " +
                "buyer VARCHAR, " +
                "labelOwner VARCHAR NOT NULL)");
    }

    public void offer(Book book, User vendor) throws SQLException {
        if (!"vendor".equals(vendor.role)) return;

        PreparedStatement check = conn.prepareStatement(
                "SELECT id, stock FROM Books WHERE title = ? AND author = ? AND vendor = ?"
        );
        check.setString(1, book.title);
        check.setString(2, book.author);
        check.setString(3, vendor.name);
        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            int bookId = rs.getInt("id");
            int currentStock = rs.getInt("stock");
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE Books SET stock = ? WHERE id = ?"
            );
            update.setInt(1, currentStock + 1);
            update.setInt(2, bookId);
            update.executeUpdate();

            System.out.printf(
                    "The stock of the book -- '%s' by %s, edition %s, published by %s in %d, condition: %s -- has been increased. New stock: %d\n",
                    book.title,
                    book.author,
                    book.edition,
                    book.publisher,
                    book.year,
                    book.condition,
                    currentStock + 1
            );
        } else {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Books (title, author, price, vendor, publication_year, edition, publisher, book_condition, description, stock) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, book.title);
            ps.setString(2, book.author);
            ps.setDouble(3, book.price);
            ps.setString(4, vendor.name);
            ps.setInt(5, book.year);
            ps.setString(6, book.edition);
            ps.setString(7, book.publisher);
            ps.setString(8, book.condition);
            ps.setString(9, book.description);
            ps.setInt(10, 1);
            ps.executeUpdate();

            System.out.printf(
                    "New book added: '%s' by %s, edition %s, published by %s in %d, condition: %s. Stock: 1\n",
                    book.title,
                    book.author,
                    book.edition,
                    book.publisher,
                    book.year,
                    book.condition
            );
        }
    }


    public List<Book> search(String keyword, User user) throws SQLException {
        List<Book> results = new ArrayList<>();
        String keywordLower = keyword.toLowerCase().trim();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Books WHERE stock > 0");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            String title = rs.getString("title");
            String author = rs.getString("author");
            String vendor = rs.getString("vendor");
            int stock = rs.getInt("stock");

            boolean matches = (title != null && title.toLowerCase().contains(keywordLower)) ||
                    (author != null && author.toLowerCase().contains(keywordLower));

            if (matches) {
                // create a label owned by the vendor, readable by the user
                Label label = new Label(new Principal(vendor));
                label.addOwner(new Principal(user.name));

                if (SecurityMngr.isAuthorizedToView(user, label)) {
                    Book b = new Book(
                            title,
                            author,
                            rs.getDouble("price"),
                            vendor,
                            rs.getInt("publication_year"),
                            rs.getString("edition"),
                            rs.getString("publisher"),
                            rs.getString("book_condition"),
                            rs.getString("description"),
                            stock
                    );
                    results.add(b);
                } else {
                    System.out.println("DEBUG: User not authorized to view book by vendor " + vendor);
                }
            }
        }

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            System.out.println("Search completed. Found " + results.size() + " matching book(s).");
        }

        return results;
    }



    public String purchase(long bookId, User buyer, double offeredPrice) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Books WHERE id = ?");
        ps.setLong(1, bookId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return "Book not found.";

        int stock = rs.getInt("stock");
        if (stock <= 0) return "Book is out of stock.";

        double actualPrice = rs.getDouble("price");
        if (offeredPrice != actualPrice) return "Price mismatch.";

        String title = rs.getString("title");
        String author = rs.getString("author");
        String vendor = rs.getString("vendor");

        Label label = new Label(new Principal(buyer.name));
        PreparedStatement insert = conn.prepareStatement("INSERT INTO Purchases (bookId, buyer, labelOwner) VALUES (?, ?, ?)");
        insert.setLong(1, bookId);
        insert.setString(2, buyer.name);
        insert.setString(3, buyer.name);
        insert.executeUpdate();

        PreparedStatement update = conn.prepareStatement("UPDATE Books SET stock = stock - 1 WHERE id = ?");
        update.setLong(1, bookId);
        update.executeUpdate();

        String confirmation = String.format(
                "Confirmation: %s bought '%s' by %s. Ship to: %s",
                buyer.name, title, author, buyer.address
        );

        System.out.println("Sending confirmation to buyer: " + buyer.name);
        System.out.println("Sending confirmation to seller: " + vendor);
        System.out.println("-> " + confirmation);

        return confirmation;
    }


    public List<PurchaseData> getAllPurchases(Connection conn) throws SQLException {
        List<PurchaseData> purchases = new ArrayList<>();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT p.bookId, p.buyer, b.title, b.author, b.price, b.vendor, " +
                        "b.publication_year, b.edition, b.publisher, b.book_condition, b.description, b.stock, " +
                        "u.role, u.address, u.consentToMarketing " +
                        "FROM Purchases p " +
                        "JOIN Books b ON p.bookId = b.id " +
                        "JOIN Users u ON p.buyer = u.name"
        );
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Book book = new Book(
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
            User buyer = new User(
                    rs.getString("buyer"),
                    rs.getString("role"),
                    rs.getString("address"),
                    rs.getBoolean("consentToMarketing")
            );
            Label label = new Label(new Principal(buyer.name));
            purchases.add(new PurchaseData(book, buyer, label));
        }

        System.out.println("All purchases retrieved successfully.");
        return purchases;
    }
}
