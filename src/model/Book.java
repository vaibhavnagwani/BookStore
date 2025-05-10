package model;

public class Book {
    public String title;
    public String author;
    public double price;
    public String vendor;
    public int year;
    public String edition;
    public String publisher;
    public String condition;
    public String description;

    public Book(String title, String author, double price, String vendor,
                int year, String edition, String publisher, String condition, String description) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.vendor = vendor;
        this.year = year;
        this.edition = edition;
        this.publisher = publisher;
        this.condition = condition;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s by %s (%d, %s, %s) - $%.2f [%s, %s] (Vendor: %s)",
                title, author, year, edition, publisher, price, condition, description, vendor);
    }
}
