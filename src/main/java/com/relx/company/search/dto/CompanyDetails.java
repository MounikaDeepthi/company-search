package com.relx.company.search.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyDetails {

	private AddressDetails address;
	private List<OfficerDetails> officers;
	@JsonProperty("company_number")
	private String companyNumber;
	@JsonProperty("company_type")
	private String companyType;
	private String title;
	@JsonProperty("company_status")
	private String companyStatus;
	@JsonProperty("date_of_creation")
	private String dateOfCreation;
}