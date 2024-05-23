package com.relx.company.search.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.relx.company.search.adapter.TruProxyAdapter;
import com.relx.company.search.dto.CompanyDetails;
import com.relx.company.search.dto.CompanySearchRequest;
import com.relx.company.search.dto.CompanySearchResponse;
import com.relx.company.search.dto.OfficerDetails;
import com.relx.company.search.dto.truproxy.GetCompanyDetailsResponse;
import com.relx.company.search.dto.truproxy.GetOfficerResponse;
import com.relx.company.search.entity.Companies;
import com.relx.company.search.mapper.FieldMapper;
import com.relx.company.search.repository.CompanyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CompanySearchService {

	@Autowired CompanyRepository companyRepository;
	@Autowired TruProxyAdapter truProxyAdapter;
	@Autowired FieldMapper mapper;

	public CompanySearchResponse searchCompany(CompanySearchRequest companySearchRequest, boolean isActive, String apiKey) {
		if(StringUtils.isNotBlank(companySearchRequest.getCompanyNumber())) {
			log.info("Company Search by company number:{}, activeFlag:{}", companySearchRequest.getCompanyNumber(), isActive);
			Companies company = companyRepository.findByCompanyNumber(companySearchRequest.getCompanyNumber());
			log.info("Company Search by company number, db result:{}", company);
			if(Objects.nonNull(company) && BooleanUtils.isTrue(isActive) && !"active".equalsIgnoreCase(company.getCompanyStatus())) {
				log.info("Company Search by company number, db result found but not active:{}", company);
				return CompanySearchResponse.builder()
						.companyDetails(Collections.emptyList())
						.totalResults(0).build();
			}
			return Objects.nonNull(company) ? prepareResponseFromDB(List.of(company)) : getCompanyAndOfficerDetailsFromTruProxy(companySearchRequest.getCompanyNumber(), isActive, apiKey);
		}else {
			log.info("Company Search by company name:{}, activeFlag:{}", companySearchRequest.getCompanyName(), isActive);
			return getCompanyAndOfficerDetailsFromTruProxy(companySearchRequest.getCompanyName(), isActive, apiKey);
		}
	}

	private CompanySearchResponse getCompanyAndOfficerDetailsFromTruProxy(String searchQuery, boolean isActive, String apiKey) {
		GetCompanyDetailsResponse companyDetailsResponse = truProxyAdapter.getCompanyDetailsFromTruProxy(searchQuery, apiKey);
		log.info("Company Search, query:{}, TruProxy API Response:{}", searchQuery, companyDetailsResponse);
		List<CompanyDetails> companyList = BooleanUtils.isTrue(isActive) ?
				getActiveCompanyList(companyDetailsResponse.getItems())
				: companyDetailsResponse.getItems();
		log.info("Company Search TruProxy API Response, active clients filter:{}, {}", companyList.size(), isActive);
		companyList.stream().forEach(t -> {
			GetOfficerResponse getOfficerResponse = truProxyAdapter.getOfficerDetailsFromTruProxy(t.getCompanyNumber(), apiKey);
			log.info("Company Search, query:{}, TruProxy Get Officers API Response:{}", t.getCompanyNumber(), getOfficerResponse);
			List<OfficerDetails> officerList = getOfficerResponse.getItems().stream().filter(obj -> StringUtils.isBlank(obj.getResignedOn())).collect(Collectors.toList());
			t.setOfficers(officerList);
			log.info("Company Search, TruProxy API Response, active officers:{}", officerList.size());
		});
		if(!companyList.isEmpty()) {
			saveCompanyAndOfficerDetails(companyList);
		}
		return CompanySearchResponse.builder().companyDetails(companyList).totalResults(companyList.size()).build();
	}

	private List<CompanyDetails> getActiveCompanyList(List<CompanyDetails> companyList){
		return companyList.stream().filter(t -> "active".equalsIgnoreCase(t.getCompanyStatus())).collect(Collectors.toList());
	}

	private CompanySearchResponse prepareResponseFromDB(List<Companies> companyList) {
		List<CompanyDetails> companyDetails = mapper.getCompanyDetailsFromDB(companyList);
		return CompanySearchResponse.builder()
				.companyDetails(companyDetails)
				.totalResults(companyDetails.size()).build();
	}

	private void saveCompanyAndOfficerDetails(List<CompanyDetails> companyDtoList) {
		List<String> companyNumberList = companyDtoList.stream().map(t -> t.getCompanyNumber()).toList();
		List<Companies> existingCompanyList = companyRepository.findByCompanyNumberIn(companyNumberList);
		List<Companies> companyList = mapper.getCompanyList(companyDtoList);
		companyList.stream().forEach(t -> {
			boolean alreadyExists = existingCompanyList.stream().anyMatch(obj -> t.getCompanyNumber().equalsIgnoreCase(obj.getCompanyNumber()));
			if(!alreadyExists) {
				companyRepository.save(t);
			}
		});
		
		//companyRepository.saveAll(mapper.getCompanyList(companyDtoList)); - unique constraint exception if already exists in db
		log.info("Company Search, company details saved to database:{}", companyDtoList.size());
	}

}
