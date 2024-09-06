package com.example.crud_demo.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.example.crud_demo.model.Book;
import com.example.crud_demo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@XRayEnabled
@RequestMapping("/api/books")
public class BookApiController {

    private static final Logger logger = LoggerFactory.getLogger(BookApiController.class);

    @Autowired
    private BookService bookService;

    // GET all books
    @GetMapping
    public List<Book> getAllBooks() {
        logger.info("Received request to fetch all books");
        List<Book> books = bookService.getAllBooks();
        logger.info("Successfully fetched {} books", books.size());
        return books;
    }

    // POST create a new book
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        logger.info("Received request to create a new book with title: {}", book.getTitle());

        // Simulate error cases
        simulateErrors(book);

        Book createdBook = bookService.createBook(book);
        logger.info("Book '{}' created successfully", createdBook.getTitle());
        return createdBook;
    }

    // Error simulation similar to your BookController class
    private void simulateErrors(Book book) {
        if ("Error".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated IllegalArgumentException: Invalid title '{}'", book.getTitle());
            throw new IllegalArgumentException("Simulated IllegalArgumentException: Title cannot be 'Error'");
        }

        if ("Null".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated NullPointerException: Book title cannot be null");
            throw new NullPointerException("Simulated NullPointerException: Title is null");
        }

        if ("ServerError".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated RuntimeException: 500 Internal Server Error for title '{}'", book.getTitle());
            throw new RuntimeException("Simulated 500 Internal Server Error");
        }

        if ("OOM".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated OutOfMemoryError: Attempting to exhaust heap memory");
            try {
                int[] largeArray = new int[Integer.MAX_VALUE];  // This will throw an OutOfMemoryError
            } catch (OutOfMemoryError e) {
                logger.error("Caught OutOfMemoryError: {}", e.getMessage());
                throw e;
            }
        }
    }
}
