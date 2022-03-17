package com.techrocking.util;

import lombok.Data;

@Data
public class VerifyTokenResponse {
	private Boolean active; 
	private String aud;
	private String client_id;
	private String device_id;
	private Integer exp;
	private Integer iat;
	private String iss;
	private String scope;
	private String sub;
	private String uid;
	private String username;

}
