package com.example.notification.controller;

import com.example.notification.dto.NotificationDto;
import com.example.notification.repository.NotificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repository;
    public NotificationController(NotificationRepository repository) { this.repository = repository; }

    @GetMapping
    public List<NotificationDto> recentNotifications() { return repository.findRecent(); }
}
