package com.library.eureka.application.bookservice;
import com.library.eureka.application.bookservice.client.LoggingServiceClient;
import com.library.eureka.application.bookservice.model.Book;
import com.library.eureka.application.bookservice.service.BookService;
import com.library.eureka.application.dto.LogEntryDto;
import com.library.eureka.application.bookservice.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private LoggingServiceClient loggingServiceClient;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchBooks_WithParameters_ShouldCallRepositoryAndLogger() {
        // Arrange
        String author = "Tolkien";
        String title = "Hobbit";
        Integer year = 1937;
        Long userId = 1L;

        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(title);
        book.setPublicationYear(year);

        List<Book> expectedBooks = Arrays.asList(book);

        when(bookRepository.findByAuthorContainingOrTitleContainingOrPublicationYear(
                anyString(), anyString(), anyInt()))
                .thenReturn(expectedBooks);

        when(loggingServiceClient.createLog(any(LogEntryDto.class)))
                .thenReturn(new LogEntryDto());

        // Act
        List<Book> result = bookService.searchBooks(author, title, year, userId);

        // Assert
        assertEquals(expectedBooks, result);
        verify(bookRepository).findByAuthorContainingOrTitleContainingOrPublicationYear(author, title, year);
        verify(loggingServiceClient).createLog(any(LogEntryDto.class));
    }
}