package com.avis.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper mapper = new ObjectMapper();
	    JavaTimeModule module = new JavaTimeModule();
	    
	    // Custom deserializer to handle fractional seconds correctly
	    module.addDeserializer(LocalDateTime.class, 
	        new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")));
	    
	    mapper.registerModule(module);
	    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    return mapper;
	}
}
