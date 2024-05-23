package com.relx.company.search.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.relx.company.search.entity.Companies;


public interface CompanyRepository extends JpaRepository<Companies, Integer>{
	public Companies findByCompanyNumber(String companyNumber);
	public List<Companies> findByCompanyNumberIn(List<String> companyNumber);
}
