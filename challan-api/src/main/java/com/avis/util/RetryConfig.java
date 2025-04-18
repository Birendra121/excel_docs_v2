package com.avis.util;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;


@Configuration
@EnableRetry
public class RetryConfig {	

	
	    @Bean
	    public RetryTemplate retryTemplate() {
	        return RetryTemplate.builder()
	                .maxAttempts(3)  // Number of retry attempts
	                .fixedBackoff(2000)  // Delay between retries (in milliseconds)
	                .build();
	    }
	}


