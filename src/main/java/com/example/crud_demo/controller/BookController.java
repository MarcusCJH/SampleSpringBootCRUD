package com.example.crud_demo.controller;

import com.example.crud_demo.model.Book;
import com.example.crud_demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public String getAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "book_list";  // Renders book_list.html
    }

    @GetMapping("/new")
    public String showCreateBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book_form";  // Renders book_form.html
    }

    @PostMapping
    public String createBook(@ModelAttribute("book") Book book) {
        bookService.createBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String showEditBookForm(@PathVariable("id") Long id, Model model) {
        Optional<Book> book = bookService.getBookById(id);
        if (book.isPresent()) {
            model.addAttribute("book", book.get());
            return "book_form";
        }
        return "redirect:/books";
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable("id") Long id, @ModelAttribute("book") Book book) {
        bookService.updateBook(id, book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/delete")
    public String deleteBook(@PathVariable("id") Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}