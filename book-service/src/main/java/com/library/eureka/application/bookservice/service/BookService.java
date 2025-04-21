package com.library.eureka.application.bookservice.service;
import com.library.eureka.application.bookservice.client.LoggingServiceClient;
import com.library.eureka.application.bookservice.model.Book;
import com.library.eureka.application.bookservice.repository.BookRepository;
import com.library.eureka.application.dto.LogEntryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LoggingServiceClient loggingServiceClient;

    public List<Book> searchBooks(String author, String title, Integer year, Long userId) {
        // Логирование поиска книг
        if (userId != null) {
            loggingServiceClient.createLog(new LogEntryDto(userId, "SEARCH_BOOKS"));
        }

        author = author == null ? "" : author;
        title = title == null ? "" : title;
        year = year == null ? 0 : year;

        return bookRepository.findByAuthorContainingOrTitleContainingOrPublicationYear(author, title, year);
    }

    @Transactional
    public Book createBook(Book book, Long userId) {
        if (userId != null) {
            loggingServiceClient.createLog(new LogEntryDto(userId, "CREATE_BOOK"));
        }
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long bookId, Book bookDetails, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + bookId));

        book.setAuthor(bookDetails.getAuthor());
        book.setTitle(bookDetails.getTitle());
        book.setDescription(bookDetails.getDescription());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setTotalQuantity(bookDetails.getTotalQuantity());
        book.setAvailableQuantity(bookDetails.getAvailableQuantity());

        if (userId != null) {
            loggingServiceClient.createLog(new LogEntryDto(userId, "UPDATE_BOOK"));
        }

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long bookId, Long userId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NoSuchElementException("Book not found with id: " + bookId);
        }

        bookRepository.deleteById(bookId);

        if (userId != null) {
            loggingServiceClient.createLog(new LogEntryDto(userId, "DELETE_BOOK"));
        }
    }

    @Transactional
    public Book borrowBook(Long bookId, Long userId, LocalDate plannedReturnDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + bookId));

        if (book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("No available copies of this book");
        }

        book.setBorrowedByUserId(userId);
        book.setBorrowedDate(LocalDate.now());
        book.setPlannedReturnDate(plannedReturnDate);
        book.setActualReturnDate(null);
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        loggingServiceClient.createLog(new LogEntryDto(userId, "BORROW_BOOK"));

        return bookRepository.save(book);
    }

    @Transactional
    public Book returnBook(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book not found with id: " + bookId));

        if (book.getBorrowedByUserId() == null || !book.getBorrowedByUserId().equals(userId)) {
            throw new IllegalStateException("This book was not borrowed by this user");
        }

        book.setActualReturnDate(LocalDate.now());
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);

        loggingServiceClient.createLog(new LogEntryDto(userId, "RETURN_BOOK"));

        return bookRepository.save(book);
    }

    public List<Book> getAllBorrowedBooks() {
        return bookRepository.findAllBorrowedBooks();
    }

    public List<Book> getBorrowedBooksByUser(Long userId) {
        return bookRepository.findByBorrowedByUserIdAndActualReturnDateIsNull(userId);
    }

    public List<Book> getOverdueBooks() {
        return bookRepository.findAllOverdueBooks();
    }
}