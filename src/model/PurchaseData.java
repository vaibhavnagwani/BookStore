package model;

import security.Label;

public class PurchaseData {
    public Book book;
    public User buyer;
    public Label label;

    public PurchaseData(Book book, User buyer, Label label) {
        this.book = book;
        this.buyer = buyer;
        this.label = label;
    }

    @Override
    public String toString() {
        return "Purchase: " + book.title + " by " + buyer.name + " [Label: " + label + "]";
    }
}
