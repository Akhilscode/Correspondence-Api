package com.coservice.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coservice.entity.DcCaseEntity;



public interface DCCasesRepository  extends JpaRepository<DcCaseEntity, Serializable>{
	
	public DcCaseEntity findByAppId(Integer appId);

}
