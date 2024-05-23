package com.relx.company.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.relx.company.search.entity.Addresses;

public interface AddressRepository  extends JpaRepository<Addresses, Integer>{}
