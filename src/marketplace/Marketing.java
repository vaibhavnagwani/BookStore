package marketplace;

import model.PurchaseData;
import model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Marketing {

    public void marketingData(List<PurchaseData> records, User viewer) {
        System.out.println("Running marketing analysis for: " + viewer.name);

        Set<String> seen = new HashSet<>();

        for (PurchaseData record : records) {
            User buyer = record.buyer;
            String bookTitle = record.book.title;
            String uniqueKey = buyer.name + "|" + bookTitle;

            if (buyer.consentToMarketing && !seen.contains(uniqueKey)) {
                seen.add(uniqueKey);
                System.out.println("- " + buyer.name + " bought " + bookTitle +
                        " (Shipping: " + buyer.address + ")");
            }
        }
    }
}
