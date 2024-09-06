package com.example.crud_demo.controller;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XRayConfig {

    @Bean
    public AWSXRayRecorder awsXRayRecorder() {
        // X-Ray recorder setup
        AWSXRayRecorder recorder = AWSXRay.getGlobalRecorder();
        recorder.setSamplingStrategy(new LocalizedSamplingStrategy());
        return recorder;
    }
}
