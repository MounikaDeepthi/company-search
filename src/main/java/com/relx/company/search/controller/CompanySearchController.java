package com.relx.company.search.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.relx.company.search.dto.CompanySearchRequest;
import com.relx.company.search.dto.CompanySearchResponse;
import com.relx.company.search.service.CompanySearchService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CompanySearchController {

	@Autowired private CompanySearchService companySearchService;

	@PostMapping(value = "/company/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanySearchResponse> searchCompany(
			@NotBlank @RequestHeader(name = "x-api-key") String apiKey,
			@RequestBody @Valid CompanySearchRequest companySearchRequest,
			@RequestParam(required = false) boolean isActive) {
		log.info("Company Search API request, apikey:{} Request:{}, activeFlag:{}", apiKey, companySearchRequest, isActive);
		CompanySearchResponse companySearchResponse = companySearchService.searchCompany(companySearchRequest, isActive, apiKey);
		log.info("Company Search API respomse, response:{}", companySearchResponse);
		return ResponseEntity.ok().body(companySearchResponse);
	}
}
