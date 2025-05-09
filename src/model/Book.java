package model;

public class Book {
    public String title;
    public String author;
    public double price;
    public String vendor;

    public Book(String title, String author, double price, String vendor) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.vendor = vendor;
    }

    @Override
    public String toString() {
        return title + " by " + author + " - $" + price + " (Vendor: " + vendor + ")";
    }
}
