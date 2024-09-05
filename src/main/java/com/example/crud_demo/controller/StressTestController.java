package com.example.crud_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class StressTestController {

    private static final Logger logger = LoggerFactory.getLogger(StressTestController.class);

    // Max out CPU with multi-threading for 10 seconds
    @GetMapping("/cpu-crash")
    public String cpuCrashTest() {
        logger.info("CPU crash test started");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executorService.submit(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 10000) {  // Run for 10 seconds
                    Math.pow(Math.random(), Math.random());  // Busy-wait loop
                }
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(12, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        logger.info("CPU crash test completed.");
        return "CPU crash test ran for 10 seconds. Check CPU usage.";
    }

    // Max out heap memory for 10 seconds or until OutOfMemoryError
    @GetMapping("/memory-crash")
    public String memoryCrashTest() {
        logger.info("Memory crash test started");

        List<int[]> memoryHog = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < 10000) {  // Run for 10 seconds
                memoryHog.add(new int[10000000]);  // Allocate large arrays to fill heap memory
            }
        } catch (OutOfMemoryError e) {
            logger.error("OutOfMemoryError caught: {}", e.getMessage());
            return "OutOfMemoryError: Memory crash test completed!";
        }

        logger.info("Memory crash test completed.");
        return "Memory crash test ran for 10 seconds. Check memory usage.";
    }
}
