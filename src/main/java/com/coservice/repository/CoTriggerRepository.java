package com.coservice.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coservice.entity.CoTrigger;

public interface CoTriggerRepository extends JpaRepository<CoTrigger, Serializable> {
     public List<CoTrigger> findByTriggerStatus(String status);
     
     public CoTrigger findByCaseNum(Long caseNum);
}
