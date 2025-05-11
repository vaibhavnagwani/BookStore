# BookMarketplace – Secure Java Bookstore

This project is a privacy-focused digital bookstore implemented in Java. It uses information flow control (IFC) based on the Myers & Liskov decentralized security model to manage confidentiality and integrity of user and book data.

## Features

- Vendors can offer books with rich metadata (title, author, edition, publisher, etc.)
- Buyers can securely search books based on keywords
- Users can purchase books, with confirmation messages to both buyer and vendor
- Access to sensitive data is protected using label-based control
- Optional marketing report feature that respects customer privacy through opt-in
- Embedded H2 database (`h2-2.3.232.jar`) for lightweight and persistent storage

## Prerequisites

- Java 8 or later (We have used Java 17)
- IntelliJ IDEA or any standard Java IDE
- H2 database JAR (`h2-2.3.232.jar`) [Download](https://www.h2database.com/)

## Setup Instructions

1. Place `h2-2.3.232.jar` in the project directory
2. Add the JAR to your classpath (in your IDE or via terminal)
3. Compile and run `Main.java`
4. The application will:
    - Create tables in the embedded H2 database
    - Add test users and a book
    - Allow users to offer, search, and purchase books
    - Display marketing report based on user consent

## Functional Overview

### Offer

- A vendor can offer a book, which gets stored in the database
- The book is labeled `{V : V, MP}` indicating the vendor owns it and it is readable by the vendor and the marketplace
- If the same book is offered again, stock is incremented

### Search

- A buyer searches for books using a keyword
- Book visibility is determined via `Label.canFlowTo(...)`
- Buyers can only see books if they are authorized to read the label

### Purchase

- A buyer purchases a book if stock is available and price matches
- Purchase is recorded and a confirmation message is generated
- Purchase labels are `{B : B}` to restrict access to only the buyer

### Marketing Report

- Only users who have opted in (`consentToMarketing = true`) are shown in the marketing report
- This simulates controlled declassification of private data
- Repeated purchases by the same user for the same book are deduplicated

## Security Model

This project uses a decentralized information flow control (DIFC) model inspired by:

- Myers, A. C., & Liskov, B. (1997). A Decentralized Model for Information Flow Control.  
  [PDF](https://www.cs.cornell.edu/andru/papers/dfc.pdf)

Key concepts:

- Data is tagged with `Label` objects, which contain sets of `Principal`s
- `Label.canFlowTo(...)` determines if a data flow is allowed
- Labels represent ownership (integrity) and readability (confidentiality)
