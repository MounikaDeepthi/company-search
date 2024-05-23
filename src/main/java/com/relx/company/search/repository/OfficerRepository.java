package com.relx.company.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.relx.company.search.entity.Officers;

public interface OfficerRepository  extends JpaRepository<Officers, Integer>{}
