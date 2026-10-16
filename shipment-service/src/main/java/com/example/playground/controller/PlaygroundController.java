package com.example.playground.controller;

import com.example.playground.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/playground")
public class PlaygroundController {

    private final TestService testService;

    public PlaygroundController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping(path = "/executeTestService")
    public void executeTestService() {

        try {
            testService.playWithReflection();

        } catch (Exception e) {
        }
    }
}
