package model;

public class User {
    public String name;
    public String role; // customer, vendor, marketplace
    public String address;
    public boolean consentToMarketing;

    public User(String name, String role, String address, boolean consentToMarketing) {
        this.name = name;
        this.role = role;
        this.address = address;
        this.consentToMarketing = consentToMarketing;
    }
}
