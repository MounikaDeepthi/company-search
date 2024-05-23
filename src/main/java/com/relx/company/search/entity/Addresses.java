package com.relx.company.search.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.relx.company.search.enums.AddressTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Addresses {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer aId;
	private String locality;
	private String country;
	private String premises;
	private String addressLine;
	private String postalCode;
	@Enumerated(EnumType.STRING)
	private AddressTypeEnum addressType;
	@OneToOne(mappedBy = "address")
	private Companies company;
	@OneToOne(mappedBy = "address")
	private Officers officer;
	@CreationTimestamp 
	private LocalDateTime createdDateTime;
}
