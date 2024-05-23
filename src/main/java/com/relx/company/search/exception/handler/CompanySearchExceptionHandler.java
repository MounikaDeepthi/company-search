package com.relx.company.search.exception.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.relx.company.search.dto.ErrorDetails;


@ControllerAdvice
public class CompanySearchExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		ex.getBindingResult().getAllErrors().forEach((error) ->{
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.add(fieldName+" "+message);
		});
		ErrorDetails errorDetails = new ErrorDetails(new Date(), errors, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
	}


	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers,HttpStatusCode status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		errors.add(ex.getMessage());
		ErrorDetails errorDetails = new ErrorDetails(new Date(), errors, HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
		return new ResponseEntity<Object>(errorDetails, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String error = String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL());
		List<String> errors = new ArrayList<>();
		errors.add(ex.getMessage());
		errors.add(error);
		ErrorDetails errorDetails = new ErrorDetails(new Date(), errors, HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ErrorDetails> handleAllExceptions(Exception ex, WebRequest request) {
		List<String> errors = new ArrayList<>();
		errors.add(ex.getMessage());
		ErrorDetails errorDetails = new ErrorDetails(new Date(),errors,HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.BAD_REQUEST);
	}
}
