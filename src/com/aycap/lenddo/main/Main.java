package com.aycap.lenddo.main;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.aycap.lenddo.restful.RestApiController;

public class Main {

	public static void main(String[] args) throws GeneralSecurityException, IOException {
		
		String apiUrl = "http://abcdefgh:31701/services/otp/request";						//Input API's URL
		
		String requestBody = "{\r\n" + 
				"	\"appNo\" : \"TSF180027163“\r\n" + 
				"}\r\n" + 
				"";																			//This is request body
		
		
		String responseBody = RestApiController.invokeRestApi(apiUrl, requestBody);
		
		System.out.println("responseBody : " + responseBody);								//Display decrypted response body
	}

}
