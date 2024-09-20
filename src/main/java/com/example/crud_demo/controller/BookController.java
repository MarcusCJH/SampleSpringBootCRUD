package com.example.crud_demo.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.crud_demo.model.Book;
import com.example.crud_demo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController  // This changes the controller to a REST API
@RequestMapping("/api/books")  // Prefix all API endpoints with /api/books
@XRayEnabled
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    // GET all books
    @GetMapping
    @XRayEnabled
    public List<Book> getAllBooks() {
        logger.debug("Fetching all books");
        return bookService.getAllBooks();  // Return JSON list of books
    }

    // GET a specific book by ID
    @GetMapping("/{id}")
    @XRayEnabled
    public Optional<Book> getBookById(@PathVariable("id") Long id) {
        logger.debug("Fetching book with ID {}", id);
        return bookService.getBookById(id);  // Return JSON for a specific book
    }

    // POST a new book with error injection logic
    @PostMapping
    @XRayEnabled
    public Book createBook(@RequestBody Book book) {
        logger.debug("Creating book with title: {}", book.getTitle());

        // Inject IllegalArgumentException if title is "Error"
        if ("Error".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated IllegalArgumentException: Invalid title '{}'", book.getTitle());
            throw new IllegalArgumentException("Simulated IllegalArgumentException: Title cannot be 'Error'");
        }

        // Simulate form validation error if title is "Null"
        if ("Null".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated NullPointerException: Book title cannot be null");
            throw new NullPointerException("Simulated NullPointerException: Title is null");
        }

        // Simulate a 500 Internal Server Error if title is "ServerError"
        if ("ServerError".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated 500 Internal Server Error for book with title '{}'", book.getTitle());
            throw new RuntimeException("Simulated 500 Internal Server Error");
        }

        // Simulate OutOfMemoryError if title is "OOM"
        if ("OOM".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated OutOfMemoryError: Forcing heap exhaustion for book with title '{}'", book.getTitle());
            try {
                // Create a large list of objects to exhaust heap space
                int[] largeArray = new int[Integer.MAX_VALUE];
            } catch (OutOfMemoryError e) {
                logger.error("OutOfMemoryError caught: {}", e.getMessage());
                throw e;  // Optionally rethrow or handle the error
            }
        }

        // Create the book in the database
        Book createdBook = bookService.createBook(book);
        logger.debug("Book created successfully: {}", book.getTitle());
        return createdBook;  // Return the created book as JSON
    }

    // PUT to update an existing book
    @PutMapping("/{id}")
    @XRayEnabled
    public Book updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
        logger.debug("Updating book with ID {} and title {}", id, book.getTitle());
        return bookService.updateBook(id, book);  // Return the updated book as JSON
    }

    // DELETE a book by ID
    @DeleteMapping("/{id}")
    @XRayEnabled
    public void deleteBook(@PathVariable("id") Long id) {
        logger.debug("Deleting book with ID {}", id);
        bookService.deleteBook(id);
        logger.debug("Book deleted successfully");
    }
}
