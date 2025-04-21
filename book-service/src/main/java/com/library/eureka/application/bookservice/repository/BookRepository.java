package com.library.eureka.application.bookservice.repository;
import com.library.eureka.application.bookservice.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE " +
            "(:author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:year = 0 OR b.publicationYear = :year)")
    List<Book> findByAuthorContainingOrTitleContainingOrPublicationYear(
            @Param("author") String author,
            @Param("title") String title,
            @Param("year") Integer year
    );

    // Получить все книги, которые в данный момент заимствованы пользователями
    @Query("SELECT b FROM Book b WHERE b.borrowedByUserId IS NOT NULL AND b.actualReturnDate IS NULL")
    List<Book> findAllBorrowedBooks();

    // Получить книги, заимствованные конкретным пользователем
    List<Book> findByBorrowedByUserIdAndActualReturnDateIsNull(Long userId);

    // Получить просроченные книги
    @Query("SELECT b FROM Book b WHERE b.borrowedByUserId IS NOT NULL AND b.actualReturnDate IS NULL AND b.plannedReturnDate < CURRENT_DATE")
    List<Book> findAllOverdueBooks();
}