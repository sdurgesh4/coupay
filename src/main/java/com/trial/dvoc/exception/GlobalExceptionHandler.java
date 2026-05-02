package com.trial.dvoc.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {

        model.addAttribute("message", "Something went wrong");
        model.addAttribute("details", ex.getMessage());

        return "error/500";
    }
}