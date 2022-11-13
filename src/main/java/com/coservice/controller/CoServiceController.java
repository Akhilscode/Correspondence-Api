package com.coservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coservice.binding.CoResponse;
import com.coservice.service.CoTriggerService;

@RestController
public class CoServiceController {
    
	@Autowired
	private CoTriggerService coservice;
	
	@GetMapping("/correspondence")
	public ResponseEntity<CoResponse> completeCoService() throws Exception{
		CoResponse processPendingTriggers = coservice.processPendingTriggers();
		return new ResponseEntity<>(processPendingTriggers, HttpStatus.OK);
	}
}
