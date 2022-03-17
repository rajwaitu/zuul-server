package com.techrocking.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);
	
	private RestTemplate restTemplate;
	
	@Value("${verify-token-url}")
	private String verifyTokenUrl;
	
	@Autowired
	public RestUtil() {
		this.restTemplate = new RestTemplate();
	}
	
	public VerifyTokenResponse verifyToken(String access_token) {
		
		VerifyTokenResponse verifyTokenResponse = null;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// Base64 Encode of client_id:client_secret
		headers.add("Authorization", "Basic MG9hNDZ5ZnJ0MndBU0RZRnc1ZDc6WmJIbjRmeWJOLVZ1c2hVbUY2d0pYT0l4MUVySVhvWVNCTk0wWmhrbw==");

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("token",access_token);
		map.add("token_type_hint","access_token");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		
		try {
			ResponseEntity<VerifyTokenResponse> response = restTemplate.exchange(verifyTokenUrl, HttpMethod.POST,
					entity, VerifyTokenResponse.class);
			verifyTokenResponse = response.getBody();
		} catch (RestClientException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return verifyTokenResponse;
	}

}
