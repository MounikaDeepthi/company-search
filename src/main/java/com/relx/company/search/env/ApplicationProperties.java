package com.relx.company.search.env;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties
@Configuration
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationProperties {
	private String truProxyBaseURL;
	private String searchCompanyURL;
	private String getOfficersURL;
	private String searchCompanyQueryParam;
	private String getOfficersQueryParam;
}
