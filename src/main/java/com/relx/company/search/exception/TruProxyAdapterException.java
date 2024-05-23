package com.relx.company.search.exception;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class TruProxyAdapterException extends RuntimeException {
	private final HttpStatusCode status;
	private final Throwable throwable;
	private final String message;
}