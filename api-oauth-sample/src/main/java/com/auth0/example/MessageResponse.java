package com.auth0.example;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MessageResponse {

	private final String message;
	
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss.SSS")
	private final LocalDateTime timestamp;
	
	public MessageResponse(String message) {
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}
	
}
