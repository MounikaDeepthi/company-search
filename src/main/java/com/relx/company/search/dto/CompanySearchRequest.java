package com.relx.company.search.dto;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.constraints.AssertTrue;
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
public class CompanySearchRequest {
	private String companyName;
	private String companyNumber;

	@AssertTrue(message = "CompanyName or CompanyNumber is required")
	private boolean isCompanyNameOrCompanyNumberExists() {
		return StringUtils.isNotBlank(companyNumber) || StringUtils.isNotBlank(companyName);
	}

}
