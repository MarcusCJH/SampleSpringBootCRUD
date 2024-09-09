package com.example.crud_demo.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class StressTestController {

    private static final Logger logger = LoggerFactory.getLogger(StressTestController.class);

    // Max out CPU with multi-threading for 10 seconds
    @GetMapping("/cpu-crash")
    @XRayEnabled
    public String cpuCrashTest() {
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

        // Shutdown the threads after 20 seconds
        scheduler.schedule(() -> {
            logger.info("Stopping CPU spike after 20 seconds...");
            scheduler.shutdownNow();
        }, 20, TimeUnit.SECONDS);

        return "CPU spike running for 20 seconds!";
    }

    // Max out heap memory for 10 seconds or until OutOfMemoryError
    @GetMapping("/memory-crash")
    @XRayEnabled
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

    @GetMapping("/combined-spike")
    @XRayEnabled
    public String causeCombinedSpikeFor20Seconds() {
        logger.info("Starting combined CPU and memory spike for 20 seconds...");

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        // CPU spike
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            scheduler.submit(() -> {
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 20000) {  // Run for 20 seconds
                    Math.pow(Math.random(), Math.random());  // CPU stress
                }
            });
        }

        // Memory spike
        List<int[]> memoryHog = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 20000) {
            memoryHog.add(new int[1000000]);  // Memory stress
        }

        // Shutdown CPU spike threads after 20 seconds
        scheduler.schedule(() -> {
            logger.info("Stopping CPU spike after 20 seconds...");
            scheduler.shutdownNow();
        }, 20, TimeUnit.SECONDS);

        logger.info("Combined spike stopped after 20 seconds.");
        return "Combined CPU and memory spike ran for 20 seconds!";
    }


}
