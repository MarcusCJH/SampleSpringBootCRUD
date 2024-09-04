# Spring Boot CRUD Application with Error Injection

This is a simple Spring Boot CRUD application to manage a list of books. The application demonstrates basic CRUD operations and includes functionality to simulate various error scenarios for testing purposes.

## Features
- View all books
- Create new books
- Edit existing books
- Delete books
- Error injection for testing

## Error Injection

You can simulate various errors by using specific book titles during creation. The following error injections are available:

- **IllegalArgumentException**: Create a book with the title "Error" to trigger a simulated `IllegalArgumentException`.
- **NullPointerException**: Create a book with the title "Null" to trigger a simulated `NullPointerException`.
- **500 Internal Server Error**: Create a book with the title "ServerError" to simulate a 500 Internal Server Error.
- **OutOfMemoryError**: Create a book with the title "OOM" to trigger an `OutOfMemoryError` by exhausting heap space.

## Setup and Running the Application

### Prerequisites
- Java 17 or later
- Maven or Gradle
- Spring Boot 3.0+
- MySQL or H2 Database (for local testing)

### Steps to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/spring-boot-crud-demo.git
   cd spring-boot-crud-demo
2. Update the database connection properties in application.properties (if you're using MySQL)
3. Run the application using Maven:
    ```bash
   mvn spring-boot:run
4. The application will be available at http://localhost:8080/books.

