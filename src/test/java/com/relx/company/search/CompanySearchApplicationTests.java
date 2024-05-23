package com.relx.company.search;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.relx.company.search.dto.AddressDetails;
import com.relx.company.search.dto.CompanyDetails;
import com.relx.company.search.dto.CompanySearchRequest;
import com.relx.company.search.dto.OfficerDetails;
import com.relx.company.search.dto.truproxy.GetCompanyDetailsResponse;
import com.relx.company.search.dto.truproxy.GetOfficerResponse;
import com.relx.company.search.entity.Addresses;
import com.relx.company.search.entity.Companies;
import com.relx.company.search.entity.Officers;
import com.relx.company.search.repository.CompanyRepository;

@SpringBootTest(classes= CompanySearchApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class CompanySearchApplicationTests {

	@Autowired private MockMvc mockMvc;
	@MockBean private RestTemplate restTemplate;
	@Autowired private ObjectMapper objectMapper; 
	@Autowired private CompanyRepository companyRepository;

	@Test
	public void searchByNewCompanyNumber() throws Exception {
		ResponseEntity<GetCompanyDetailsResponse> companyResponse = ResponseEntity.ok(getCompanyDetailsResponse("1234"));
		ResponseEntity<GetOfficerResponse> officersResponse = ResponseEntity.ok(getOfficersResponse());
		String request = objectMapper.writeValueAsString(CompanySearchRequest.builder().companyNumber("1234").build());
		Mockito.when(
				restTemplate.exchange(
						Mockito.anyString(),
						Mockito.any(HttpMethod.class),
						Mockito.any(HttpEntity.class),
						Mockito.eq(GetCompanyDetailsResponse.class)))
		.thenReturn(companyResponse);
		Mockito.when(
				restTemplate.exchange(
						Mockito.anyString(),
						Mockito.any(HttpMethod.class),
						Mockito.any(HttpEntity.class),
						Mockito.eq(GetOfficerResponse.class)))
		.thenReturn(officersResponse);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/company/search")
				.content(request).header("x-api-key", "apiKey")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void searchByNewCompanyName() throws Exception {
		ResponseEntity<GetCompanyDetailsResponse> companyResponse = ResponseEntity.ok(getCompanyDetailsResponse("12347"));
		ResponseEntity<GetOfficerResponse> officersResponse = ResponseEntity.ok(getOfficersResponse());
		String request = objectMapper.writeValueAsString(CompanySearchRequest.builder().companyName("test").build());
		Mockito.when(
				restTemplate.exchange(
						Mockito.anyString(),
						Mockito.any(HttpMethod.class),
						Mockito.any(HttpEntity.class),
						Mockito.eq(GetCompanyDetailsResponse.class)))
		.thenReturn(companyResponse);
		Mockito.when(
				restTemplate.exchange(
						Mockito.anyString(),
						Mockito.any(HttpMethod.class),
						Mockito.any(HttpEntity.class),
						Mockito.eq(GetOfficerResponse.class)))
		.thenReturn(officersResponse);
		mockMvc.perform(
				MockMvcRequestBuilders.post("/company/search")
				.content(request).header("x-api-key", "apiKey").contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void searchByExistingCompanyNumber() throws Exception {
		companyRepository.save(Companies.builder()
				.companyNumber("12345").companyStatus("active").title("test")
				.address(Addresses.builder().country("UK").build())
				.officers(List.of(Officers.builder().name("user1")
						.address(Addresses.builder().country("UK").build()).build())) .build());
		String request = objectMapper.writeValueAsString(CompanySearchRequest.builder().companyNumber("12345").build());
		mockMvc.perform(
				MockMvcRequestBuilders.post("/company/search")
				.content(request).header("x-api-key", "apiKey")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void invalidRequest() throws Exception {
		String request = objectMapper.writeValueAsString(CompanySearchRequest.builder().build());
		mockMvc.perform(
				MockMvcRequestBuilders.post("/company/search")
				.content(request).header("x-api-key", "apiKey")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(status().is4xxClientError());
	}


	private GetCompanyDetailsResponse getCompanyDetailsResponse(String companyNumber) {
		return GetCompanyDetailsResponse.builder().items(List.of(CompanyDetails.builder()
				.companyNumber(companyNumber).companyStatus("active").title("test")
				.address(AddressDetails.builder().country("UK").build())
				.build())).build();
	}

	private GetOfficerResponse getOfficersResponse() {
		List<OfficerDetails> officerList = new ArrayList<>();
		officerList.add(OfficerDetails.builder().name("user1").resignedOn("2020-7-12")
				.address(AddressDetails.builder().country("UK").build()).build());
		officerList.add(OfficerDetails.builder().name("user2")
				.address(AddressDetails.builder().country("India").build()).build());
		return GetOfficerResponse.builder().items(officerList).build();
	}
}
