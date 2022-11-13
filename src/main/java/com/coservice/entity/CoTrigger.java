package com.coservice.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name="COTRIGGER_MASTER")
@Data
public class CoTrigger {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer trId;
  private Long caseNum;
  @Lob
  private byte[] coPdf;
  private String triggerStatus;
  
  @CreationTimestamp
  @Column(name="CREATED_DATE", updatable = false )
  private LocalDate createdDate;
  @UpdateTimestamp
  @Column(name="UPDATED_DATE",  insertable = false )
  private LocalDate updatedDate;
  private String createdBy;
  private String updatedBy;

  }

