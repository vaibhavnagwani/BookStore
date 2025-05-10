package marketplace;

import model.PurchaseData;
import model.User;
import security.Label;
import security.Principal;
import security.SecurityMngr;

import java.util.List;

public class Marketing {

    public void analyzeData(List<PurchaseData> records, User viewer) {
        System.out.println("Running marketing analysis for authorized viewer: " + viewer.name);
        for (PurchaseData record : records) {
            User buyer = record.buyer;
            Label label = record.label;

            if (!SecurityMngr.isAuthorizedToView(viewer, label)) {
                System.out.println("- [Restricted]");
                continue;
            }

            if (buyer.consentToMarketing) {
                System.out.println("- " + buyer.name + " bought " + record.book.title +
                        " (Shipping: " + buyer.address + ")");
            } else {
                System.out.println("- " + buyer.name + " opted out of marketing.");
            }
        }
    }
}
