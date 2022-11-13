package com.coservice.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name  = "ELIGIBILITY_MASTER")
@Data
public class EligibilityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 	private  Integer eligibilityId;
	private Long caseNum;
	private String planName;
	private String holderName;
	private Long holderSsn;
	private String planStartDate;
	private String planEndDate;
	private String planStatus;
	private String benefitAmnt;
	private String denialReason;
	
	@CreationTimestamp
	@Column(name="CREATED_DATE", updatable = false )
	private LocalDate createdDate;
	@UpdateTimestamp
	@Column(name="UPDATED_DATE",  insertable = false )
	private LocalDate updatedDate;
	private String createdBy;
	private String updatedBy;

	}

