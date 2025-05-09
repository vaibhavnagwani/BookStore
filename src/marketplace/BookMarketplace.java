package marketplace;

import model.Book;
import model.User;
import model.PurchaseData;
import security.Label;
import security.Principal;
import security.SecurityMngr;

import java.util.*;

public class BookMarketplace {
    private List<Book> catalog = new ArrayList<>(); //mid confidentiality, high integrity (vendor controlled)
    private List<PurchaseData> sales = new ArrayList<>(); //high confidentiality, mid integrity (buyer controlled)

    public void addBook(Book book, User vendor) {
        if (!vendor.role.equals("vendor")) return; //only vendors can add books
        catalog.add(book);
    }

    public List<Book> searchBooks(String keyword, User requester) {
        List<Book> results = new ArrayList<>();
        for (Book b : catalog) {
            Label bookLabel = new Label(new Principal(b.vendor));
            if (SecurityMngr.isAuthorizedToView(requester, bookLabel)) {
                if (b.title.contains(keyword) || b.author.contains(keyword)) {
                    results.add(b);
                }
            }
        }
        return results;
    }

    public void purchaseBook(User buyer, Book book) {
        Label label = new Label(new Principal(buyer.name)); //assign label with buyer as owner
        PurchaseData record = new PurchaseData(book, buyer, label);
        sales.add(record);
    }

    public void viewSales(User viewer) {
        for (PurchaseData pr : sales) {
            if (SecurityMngr.isAuthorizedToView(viewer, pr.label)) {
                System.out.println(pr);
            } else {
                System.out.println("[Restricted]");
            }
        }
    }

    public List<PurchaseData> getAllSales() {
        return sales;
    }
}
