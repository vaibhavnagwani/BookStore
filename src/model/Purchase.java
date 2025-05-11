package model;

import security.Label;

public class Purchase {
    public Book book;       // book details associated with the purchase
    public User buyer;      // user who made the purchase (owner of data)
    public Label label;     // label {C : C} — only the customer can read/write

    public Purchase(Book book, User buyer, Label label) {
        // book metadata is still readable, but label applies to purchase record
        // integrity: buyer confirms purchase — written by {C}
        // confidentiality: purchase owned by {C} — restrict flow unless consent
        this.book = book;
        this.buyer = buyer;
        this.label = label;
    }

    @Override
    public String toString() {
        // only safe to call this when label has been authorized to flow to viewer
        return "Purchase: " + book.title + " by " + buyer.name + " [Label: " + label + "]";
    }
}
