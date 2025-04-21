package com.library.eureka.application.bookservice.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String title;
    private String description;
    private Integer publicationYear;
    private Integer totalQuantity;
    private Integer availableQuantity;

    // Новые поля для отслеживания заимствований книг
    private Long borrowedByUserId;
    private LocalDate borrowedDate;
    private LocalDate plannedReturnDate;
    private LocalDate actualReturnDate;

    // Метод для проверки, просрочена ли книга
    @Transient
    public boolean isOverdue() {
        return borrowedByUserId != null
                && plannedReturnDate != null
                && actualReturnDate == null
                && LocalDate.now().isAfter(plannedReturnDate);
    }

    // Метод для проверки, занята ли книга
    @Transient
    public boolean isBorrowed() {
        return borrowedByUserId != null && actualReturnDate == null;
    }
}