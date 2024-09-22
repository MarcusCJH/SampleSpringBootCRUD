package com.example.crud_demo.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")  // Prefix all endpoints with /api
public class StressTestController {

    private static final Logger logger = LoggerFactory.getLogger(StressTestController.class);

    // Max out CPU with multi-threading for 20 seconds and then return a response
    @GetMapping("/cpu-crash")
    @XRayEnabled
    public ResponseEntity<String> cpuCrashTest() throws InterruptedException {
        logger.info("Starting CPU spike for 20 seconds...");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        // Create CPU spike by starting multiple threads
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            scheduler.submit(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000) {  // Run for 20 seconds
                    Math.pow(Math.random(), Math.random());  // Busy-wait to consume CPU
                }
            });
        }

        // Wait for all threads to finish
        scheduler.awaitTermination(20, TimeUnit.SECONDS);

        // Shutdown the threads after 20 seconds
        scheduler.shutdown();
        logger.info("CPU spike finished.");

        return ResponseEntity.ok("CPU spike completed!");  // Return a JSON response
    }

    // Max out heap memory for 10 seconds or until OutOfMemoryError and then return a response
    @GetMapping("/memory-crash")
    @XRayEnabled
    public ResponseEntity<String> memoryCrashTest() {
        logger.info("Memory crash test started");

        List<int[]> memoryHog = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < 10000) {  // Run for 10 seconds
                memoryHog.add(new int[10000000]);  // Allocate large arrays to fill heap memory
            }
        } catch (OutOfMemoryError e) {
            logger.error("OutOfMemoryError caught: {}", e.getMessage());
            return ResponseEntity.ok("OutOfMemoryError: Memory crash test completed!");  // Return error response
        }

        logger.info("Memory crash test completed.");
        return ResponseEntity.ok("Memory crash completed successfully!");  // Return success response
    }

    // Simulate combined CPU and memory spike for 60 seconds and then return a response
    @GetMapping("/combined-crash")
    @XRayEnabled
    public ResponseEntity<String> causeCombinedSpikeFor60Seconds() throws InterruptedException {
        logger.info("Starting combined CPU and memory spike for 60 seconds...");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        // CPU spike
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            scheduler.submit(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 60000) {  // Run for 60 seconds
                    Math.pow(Math.random(), Math.random());  // CPU stress
                }
            });
        }

        // Memory spike
        List<int[]> memoryHog = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 60000) {
            memoryHog.add(new int[1000000]);  // Memory stress
        }

        // Wait for all threads to finish
        scheduler.awaitTermination(60, TimeUnit.SECONDS);

        // Shutdown CPU spike threads after 60 seconds
        scheduler.shutdown();
        logger.info("Combined spike finished after 60 seconds.");

        return ResponseEntity.ok("Combined CPU and memory spike completed!");  // Return response after task completion
    }

    // Simulate latency before returning a response
    @GetMapping("/latency-drag")
    public ResponseEntity<String> simulateLatency() throws InterruptedException {
        logger.info("Simulating latency before responding...");

        // Simulate a 20-second delay (20000 milliseconds)
        Thread.sleep(20000);

        logger.info("Response sent after 20-second delay.");
        return ResponseEntity.ok("This response had a forced latency of 20 seconds!");
    }
}
