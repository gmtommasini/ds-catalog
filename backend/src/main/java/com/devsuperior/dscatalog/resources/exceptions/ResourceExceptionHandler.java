package com.devsuperior.dscatalog.resources.exceptions;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ControllerAdvice // allows error interception/capture
public class ResourceExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class) // @ExceptionHandler with parameter identifies what type of
														// exception this method will handle
	public ResponseEntity<StandardError> entiryNotFound(ResourceNotFoundException error, HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError sd = new StandardError();

		sd.setTimestamp(Instant.now());
		sd.setStatus(status.value()); // NOT_FOUND = 404
		sd.setError("Resource not found");
		sd.setMessage(error.getMessage()); // Message thrown by Service layer
		sd.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(sd);
	}

	@ExceptionHandler(DatabaseException.class) // @ExceptionHandler with parameter identifies what type of exception
												// this method will handle
	public ResponseEntity<StandardError> database(DatabaseException error, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError sd = new StandardError();

		sd.setTimestamp(Instant.now());
		sd.setStatus(status.value()); // BAD_REQUEST = 400
		sd.setError("Database exception");
		sd.setMessage(error.getMessage()); // Message thrown by Service layer
		sd.setPath(request.getRequestURI());

		return ResponseEntity.status(status).body(sd);
	}
	

}
