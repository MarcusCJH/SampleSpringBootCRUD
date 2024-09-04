package com.example.crud_demo.controller;

import com.example.crud_demo.model.Book;
import com.example.crud_demo.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    // GET all books
    @GetMapping
    public String getAllBooks(Model model) {
        logger.debug("Fetching all books");
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "book_list";  // Renders book_list.html
    }

    // Show create book form
    @GetMapping("/new")
    public String showCreateBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book_form";  // Renders book_form.html
    }

    // Create a new book with error injection logic
    @PostMapping
    public String createBook(@ModelAttribute("book") Book book) {
        logger.debug("Creating book with title: {}", book.getTitle());

        // Inject ValueError if title is "Error"
        if ("Error".equalsIgnoreCase(book.getTitle())) {
            logger.error("Simulated ValueError: Book title cannot be 'Error'");
            throw new IllegalArgumentException("Simulated ValueError: Book title cannot be 'Error'");
        }

        // Simulate form validation error if title is "Invalid"
        if ("Invalid".equalsIgnoreCase(book.getTitle())) {
            logger.warn("Simulated validation error: Title 'Invalid' is not allowed");
            throw new IllegalArgumentException("Simulated validation error: Invalid book title");
        }

        // Create the book in the database
        bookService.createBook(book);
        logger.debug("Book created successfully: {}", book.getTitle());
        return "redirect:/books";
    }

    // Edit book form
    @GetMapping("/{id}/edit")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book_form";  // Renders book_form.html
        }

        // Simulate 404 error if title is "Not Found"
        logger.error("Simulated 404 error: Book not found for ID {}", id);
        throw new RuntimeException("Simulated 404 Error: Book not found");
    }

    // Update an existing book
    @PostMapping("/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book book) {
        logger.debug("Updating book with ID {} and title {}", id, book.getTitle());
        bookService.updateBook(id, book);
        logger.debug("Book updated successfully");
        return "redirect:/books";
    }

    // Delete book
    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable("id") Long id) {
        logger.debug("Deleting book with ID {}", id);
        bookService.deleteBook(id);
        logger.debug("Book deleted successfully");
        return "redirect:/books";
    }
}
