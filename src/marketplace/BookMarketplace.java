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
                "description VARCHAR)");

        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Purchases (" +
                "id IDENTITY PRIMARY KEY, " +
                "bookId BIGINT, " +
                "buyer VARCHAR, " +
                "labelOwner VARCHAR NOT NULL)");
    }

    public void offer(Book book, User vendor) throws SQLException {
        if (!"vendor".equals(vendor.role)) return;

        PreparedStatement exists = conn.prepareStatement(
                "SELECT 1 FROM Books WHERE title = ? AND author = ? AND vendor = ?"
        );
        exists.setString(1, book.title);
        exists.setString(2, book.author);
        exists.setString(3, vendor.name);
        if (exists.executeQuery().next()) return;

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Books (title, author, price, vendor, publication_year, edition, publisher, book_condition, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
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
        ps.executeUpdate();
    }

    public List<Book> search(String keyword, User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Books");
        ResultSet rs = ps.executeQuery();
        List<Book> results = new ArrayList<>();
        while (rs.next()) {
            String vendor = rs.getString("vendor");
            Label label = new Label(new Principal(vendor));
            if (SecurityMngr.isAuthorizedToView(user, label)) {
                String title = rs.getString("title");
                String author = rs.getString("author");
                if (title.toLowerCase().contains(keyword.toLowerCase()) || author.toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(new Book(
                            title,
                            author,
                            rs.getDouble("price"),
                            vendor,
                            rs.getInt("publication_year"),
                            rs.getString("edition"),
                            rs.getString("publisher"),
                            rs.getString("book_condition"),
                            rs.getString("description")
                    ));
                }
            }
        }
        return results;
    }

    public String purchase(long bookId, User buyer, double offeredPrice) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM Books WHERE id = ?");
        ps.setLong(1, bookId);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) return "Book not found.";

        if (isBookSold(bookId)) return "Book is out of stock.";

        double actualPrice = rs.getDouble("price");
        if (offeredPrice != actualPrice) return "Price mismatch.";

        String title = rs.getString("title");
        String author = rs.getString("author");

        Label label = new Label(new Principal(buyer.name));
        PreparedStatement insert = conn.prepareStatement("INSERT INTO Purchases (bookId, buyer, labelOwner) VALUES (?, ?, ?)");
        insert.setLong(1, bookId);
        insert.setString(2, buyer.name);
        insert.setString(3, buyer.name);
        insert.executeUpdate();

        return String.format("Confirmation: %s bought '%s' by %s. Ship to: %s", buyer.name, title, author, buyer.address);
    }

    private boolean isBookSold(long bookId) throws SQLException {
        PreparedStatement check = conn.prepareStatement("SELECT 1 FROM Purchases WHERE bookId = ?");
        check.setLong(1, bookId);
        return check.executeQuery().next();
    }

    public List<PurchaseData> getAllPurchases(Connection conn) throws SQLException {
        List<PurchaseData> purchases = new ArrayList<>();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT p.bookId, p.buyer, b.title, b.author, b.price, b.vendor, " +
                        "b.publication_year, b.edition, b.publisher, b.book_condition, b.description, " +
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
                    rs.getString("description")
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
        return purchases;
    }
}
