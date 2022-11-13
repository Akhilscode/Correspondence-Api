package com.coservice.binding;

import lombok.Data;

@Data
public class CoResponse {
	
	private Long totalTriggers;
	
	private Long successTriggers;
	
	private Long failedTriggers;

}
