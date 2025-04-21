package com.library.eureka.application.loggingservice.service;
import com.library.eureka.application.loggingservice.model.LogEntry;
import com.library.eureka.application.loggingservice.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;
    public LogEntry createLog(Long userId, String action) {
        LogEntry logEntry = new LogEntry();
        logEntry.setUserId(userId);
        logEntry.setAction(action);
        return logRepository.save(logEntry);
    }

    public List<LogEntry> getAllLogs() {
        return logRepository.findAll();
    }

    public List<LogEntry> getLogsByUserId(Long userId) {
        return logRepository.findByUserId(userId);
    }

    public List<LogEntry> getLogsByPeriod(LocalDateTime from, LocalDateTime to) {
        return logRepository.findByTimestampBetween(from, to);
    }

    public List<LogEntry> getLogsByUserIdAndPeriod(Long userId, LocalDateTime from, LocalDateTime to) {
        return logRepository.findByUserIdAndTimestampBetween(userId, from, to);
    }
}