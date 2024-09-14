package com.security.de.Exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public String handleGlobalError(RuntimeException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "errorPage";
    }

}
