package com.library.eureka.application.loggingservice.controller;
import com.library.eureka.application.loggingservice.model.LogEntry;
import com.library.eureka.application.loggingservice.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    // Получить все логи (admin only)
    @GetMapping("/all")
    public List<LogEntry> getAllLogs(
            @RequestHeader(value = "X-User-Role", required = true) String userRole) {
        validateAdminRole(userRole);
        return logService.getAllLogs();
    }

    // Получить логи для конкрентного пользователя (admin only)
    @GetMapping("/user/{userId}")
    public List<LogEntry> getLogsByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Role", required = true) String userRole) {
        validateAdminRole(userRole);
        return logService.getLogsByUserId(userId);
    }

    // Получить логи для периода (admin only)
    @GetMapping("/period")
    public List<LogEntry> getLogsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestHeader(value = "X-User-Role", required = true) String userRole) {
        validateAdminRole(userRole);
        return logService.getLogsByPeriod(from, to);
    }

    // Создать лог
    @PostMapping
    public ResponseEntity<LogEntry> createLog(
            @RequestBody LogEntry logEntry) {
        return ResponseEntity.ok(logService.createLog(logEntry.getUserId(), logEntry.getAction()));
    }

    // Метод помогает определить админ ли это
    private void validateAdminRole(String role) {
        if (!"ADMIN".equals(role)) {
            throw new SecurityException("Access denied. Admin role required.");
        }
    }
}