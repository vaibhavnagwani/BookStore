import model.*;
import marketplace.*;

public class Main {
    public static void main(String[] args) {
        BookMarketplace store = new BookMarketplace();
        Marketing marketing = new Marketing();

        User alice = new User("Alice", "customer", "123", true);
        User bob = new User("Bob", "vendor", "456", false);
        User admin = new User("Admin", "marketplace", "HQ", false);

        Book book1 = new Book("LFS", "Sebastian", 50, bob.name);
        Book book2 = new Book("Java", "ABC", 30, bob.name);

        store.addBook(book1, bob);
        store.addBook(book2, bob);

        //simulate search
        System.out.println("Search results for 'Java':");
        for (Book b : store.searchBooks("Java", alice)) {
            System.out.println(b);
        }

        store.purchaseBook(alice, book1);

        System.out.println("\nSales visible to Admin:");
        store.viewSales(admin);

        System.out.println("\nMarketing data:");
        marketing.analyzeData(store.getAllSales());
    }
}
