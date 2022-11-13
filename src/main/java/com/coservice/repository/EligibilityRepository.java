package com.coservice.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coservice.entity.EligibilityEntity;



public interface EligibilityRepository extends JpaRepository<EligibilityEntity, Serializable>{
    public EligibilityEntity findByCaseNum(Long caseNum);
}
