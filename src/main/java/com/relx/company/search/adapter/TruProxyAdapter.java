package com.relx.company.search.adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.relx.company.search.dto.truproxy.GetCompanyDetailsResponse;
import com.relx.company.search.dto.truproxy.GetOfficerResponse;
import com.relx.company.search.env.ApplicationProperties;
import com.relx.company.search.exception.TruProxyAdapterException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TruProxyAdapter {

	@Autowired private RestTemplate restTemplate;
	@Autowired private ApplicationProperties applicationProperties;

	public HttpHeaders getHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.add("x-api-key", apiKey);
		return headers;
	}

	public GetCompanyDetailsResponse getCompanyDetailsFromTruProxy(String queryParam, String apiKey) {
		UriComponentsBuilder uriBuilder= buildUrl(applicationProperties.getSearchCompanyURL(), applicationProperties.getTruProxyBaseURL()); 
		String url = uriBuilder.queryParam(applicationProperties.getSearchCompanyQueryParam(), queryParam).encode().toUriString();
		return makeRequest(url, HttpMethod.GET, getHeaders(apiKey),GetCompanyDetailsResponse.class);

	}

	public GetOfficerResponse getOfficerDetailsFromTruProxy(String queryParam, String apiKey) {
		UriComponentsBuilder uriBuilder = buildUrl(applicationProperties.getGetOfficersURL(), applicationProperties.getTruProxyBaseURL());   
		String url = uriBuilder.queryParam(applicationProperties.getGetOfficersQueryParam(), queryParam).encode().toUriString();
		return makeRequest(url,HttpMethod.GET, getHeaders(apiKey), GetOfficerResponse.class);
	}


	public <T, K> K makeRequest(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, Class<K> clazz) {
		try {
			log.info("Making Rest Call, URL:{} Method:{} Headers:{}", url, httpMethod, httpHeaders);
			ResponseEntity<K> response = restTemplate.exchange(url, httpMethod,new HttpEntity<>(httpHeaders) ,clazz);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			}else {
				log.info("TruProxy API Error, statusCode:{} Response:{}", response.getStatusCode(), response.getBody());
				throw TruProxyAdapterException.builder()
				.status(response.getStatusCode())
				.message(String.valueOf(response.getBody()))
				.build();
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			log.error("TruProxy API http Error, statusCode:{} msg:{}", e.getStatusCode(), e.getMessage());
			throw TruProxyAdapterException.builder()
			.status(e.getStatusCode())
			.message(e.getMessage())
			.throwable(e)
			.build();
		} catch (Exception e) {
			log.error("TruProxy API http Error msg:{}", e.getMessage());
			throw TruProxyAdapterException.builder()
			.status(HttpStatus.EXPECTATION_FAILED)
			.message(e.getMessage())
			.throwable(e)
			.build();
		}
	}

	public UriComponentsBuilder buildUrl(String apiPath, String baseURL) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(apiPath).host(baseURL).scheme("https");
		return uriBuilder;
	}
}