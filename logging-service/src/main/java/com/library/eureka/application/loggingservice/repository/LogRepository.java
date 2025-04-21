package com.library.eureka.application.loggingservice.repository;
import com.library.eureka.application.loggingservice.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface LogRepository extends JpaRepository<LogEntry, Long> {
    // логи по id
    List<LogEntry> findByUserId(Long userId);

    // логи для определенного периода
    @Query("SELECT l FROM LogEntry l WHERE l.timestamp BETWEEN :from AND :to ORDER BY l.timestamp DESC")
    List<LogEntry> findByTimestampBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    // логи для конкретного пользователя с определенным периодом
    @Query("SELECT l FROM LogEntry l WHERE l.userId = :userId AND l.timestamp BETWEEN :from AND :to ORDER BY l.timestamp DESC")
    List<LogEntry> findByUserIdAndTimestampBetween(
            @Param("userId") Long userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}