package model;

public class User {
    public String name;               // user principal name (used in labels)
    public String role;               // role: customer, vendor, or marketplace
    public String address;            // shipping address, high confidentiality {U : U}
    public boolean consentToMarketing; // opt-in flag for declassification to {MP}

    public User(String name, String role, String address, boolean consentToMarketing) {
        this.name = name;
        this.role = role;
        this.address = address;
        this.consentToMarketing = consentToMarketing;
    }
}
