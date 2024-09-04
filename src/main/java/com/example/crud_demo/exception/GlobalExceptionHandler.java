package com.example.crud_demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationErrors(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error_page";  // Custom error page
    }

    @ExceptionHandler(RuntimeException.class)
    public String handle404Errors(RuntimeException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error_page";  // Custom error page
    }
}
