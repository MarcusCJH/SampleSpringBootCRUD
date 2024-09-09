package com.example.crud_demo.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
public class StressTestController {

    private static final Logger logger = LoggerFactory.getLogger(StressTestController.class);

    // Max out CPU with multi-threading for 20 seconds and then render the page
    @GetMapping("/cpu-crash")
    @XRayEnabled
    public String cpuCrashTest(Model model) throws InterruptedException {
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
        logger.info("CPU spike finished. Rendering the page...");

        model.addAttribute("message", "CPU spike completed!");
        return "cpu_crash";  // Renders cpu_crash.html after task completion
    }

    // Max out heap memory for 10 seconds or until OutOfMemoryError and then render the page
    @GetMapping("/memory-crash")
    @XRayEnabled
    public String memoryCrashTest(Model model) {
        logger.info("Memory crash test started");

        List<int[]> memoryHog = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - startTime < 10000) {  // Run for 10 seconds
                memoryHog.add(new int[10000000]);  // Allocate large arrays to fill heap memory
            }
        } catch (OutOfMemoryError e) {
            logger.error("OutOfMemoryError caught: {}", e.getMessage());
            model.addAttribute("message", "OutOfMemoryError: Memory crash test completed!");
            return "memory_crash";  // Renders memory_crash.html with the error message
        }

        logger.info("Memory crash test completed. Rendering the page...");
        model.addAttribute("message", "Memory crash completed successfully!");
        return "memory_crash";  // Renders memory_crash.html after task completion
    }

    // Simulate combined CPU and memory spike for 20 seconds and then render the page
    @GetMapping("/combined-crash")
    @XRayEnabled
    public String causeCombinedSpikeFor20Seconds(Model model) throws InterruptedException {
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

        // Wait for all threads to finish
        scheduler.awaitTermination(20, TimeUnit.SECONDS);

        // Shutdown CPU spike threads after 20 seconds
        scheduler.shutdown();
        logger.info("Combined spike finished. Rendering the page...");

        model.addAttribute("message", "Combined CPU and memory spike completed!");
        return "combined_crash";  // Renders combined_crash.html after task completion
    }

    // Simulate latency before rendering a page
    @GetMapping("/latency-drag")
    public String simulateLatency(Model model) throws InterruptedException {
        logger.info("Simulating latency before rendering the page...");

        // Simulate a 5-second delay (5000 milliseconds)
        Thread.sleep(5000);

        logger.info("Page rendered after 5-second delay.");
        model.addAttribute("message", "This page had a forced latency!");

        return "latency_drag";  // Renders latency_drag.html after the delay
    }
}
