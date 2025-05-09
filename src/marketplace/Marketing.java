package marketplace;

import model.PurchaseData;
import model.User;

import java.util.List;

public class Marketing {

    public void analyzeData(List<PurchaseData> records) {
        System.out.println("Running marketing analysis on consenting users:");
        for (PurchaseData record : records) {
            User buyer = record.buyer;
            if (buyer.consentToMarketing) {
                System.out.println("- " + buyer.name + " bought " + record.book.title);
            }
        }
    }
}
