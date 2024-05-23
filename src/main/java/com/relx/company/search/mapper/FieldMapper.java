package com.relx.company.search.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.relx.company.search.dto.AddressDetails;
import com.relx.company.search.dto.CompanyDetails;
import com.relx.company.search.dto.OfficerDetails;
import com.relx.company.search.entity.Addresses;
import com.relx.company.search.entity.Companies;
import com.relx.company.search.entity.Officers;
import com.relx.company.search.enums.AddressTypeEnum;

@Component
public class FieldMapper {
	private AddressDetails getAddressFromDB(Addresses address) {
		return AddressDetails.builder()
				.locality(address.getLocality())
				.country(address.getCountry())
				.premises(address.getPremises())
				.addressLine(address.getAddressLine())
				.postalCode(address.getPostalCode())
				.build();
	}

	private List<OfficerDetails> getOfficersFromDB(List<Officers> officers) {
		return officers.stream().map(t -> OfficerDetails.builder()
				.address(getAddressFromDB(t.getAddress()))
				.name(t.getName())
				.officerRole(t.getRole())
				.appointedOn(t.getAppointedOn())
				.build())
				.collect(Collectors.toList());
	}

	public List<CompanyDetails> getCompanyDetailsFromDB(List<Companies> companyList){
		return companyList.stream().map(t -> CompanyDetails.builder()
				.address(getAddressFromDB(t.getAddress()))
				.officers(getOfficersFromDB(t.getOfficers()))
				.companyNumber(t.getCompanyNumber())
				.companyType(t.getCompanyType())
				.title(t.getTitle())
				.companyStatus(t.getCompanyStatus())
				.dateOfCreation(t.getDateOfCreation()).build())
				.collect(Collectors.toList());
	}

	public List<Companies> getCompanyList(List<CompanyDetails> companyDtoList) {
		List<Companies> companyList = new ArrayList<>();
		companyDtoList.stream().forEach(t -> {
			Companies company = Companies.builder()
					.address(getAddressList(t.getAddress(), AddressTypeEnum.COMPANY))
					.companyNumber(t.getCompanyNumber())
					.companyType(t.getCompanyType())
					.title(t.getTitle())
					.companyStatus(t.getCompanyStatus())
					.dateOfCreation(t.getDateOfCreation()).build();
			company.setOfficers(getOfficersList(t.getOfficers(), company));
			companyList.add(company);
		});
		return companyList;
	}

	private Addresses getAddressList(AddressDetails address, AddressTypeEnum addressType) {
		return Addresses.builder()
				.locality(address.getLocality())
				.country(address.getCountry())
				.premises(address.getPremises())
				.addressLine(address.getAddressLine())
				.postalCode(address.getPostalCode())
				.addressType(addressType)
				.build();
	}

	private List<Officers> getOfficersList(List<OfficerDetails> officersDtoList, Companies company) {
		return officersDtoList.stream().map(t -> Officers.builder()
				.name(t.getName())
				.company(company)
				.address(getAddressList(t.getAddress(), AddressTypeEnum.EMPLOYEE))
				.role(t.getOfficerRole())
				.appointedOn(t.getAppointedOn())
				.build()).toList();
	}

}
