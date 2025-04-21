package com.library.eureka.application.bookservice.controller;
import com.library.eureka.application.bookservice.model.Book;
import com.library.eureka.application.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<Book> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestHeader(value = "X-User-ID", required = false) Long userId
    ) {
        return bookService.searchBooks(author, title, year, userId);
    }

    @PostMapping
    public Book createBook(
            @RequestBody Book book,
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        return bookService.createBook(book, userId);
    }

    @PutMapping("/{id}")
    public Book updateBook(
            @PathVariable Long id,
            @RequestBody Book book,
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        return bookService.updateBook(id, book, userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        bookService.deleteBook(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/borrow")
    public Book borrowBook(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate plannedReturnDate,
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        return bookService.borrowBook(id, userId, plannedReturnDate);
    }

    @PostMapping("/{id}/return")
    public Book returnBook(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        return bookService.returnBook(id, userId);
    }

    @GetMapping("/borrowed")
    public List<Book> getAllBorrowedBooks() {
        return bookService.getAllBorrowedBooks();
    }

    @GetMapping("/borrowed/user")
    public List<Book> getBorrowedBooksByUser(
            @RequestHeader(value = "X-User-ID", required = true) Long userId
    ) {
        return bookService.getBorrowedBooksByUser(userId);
    }

    @GetMapping("/overdue")
    public List<Book> getOverdueBooks() {
        return bookService.getOverdueBooks();
    }
}