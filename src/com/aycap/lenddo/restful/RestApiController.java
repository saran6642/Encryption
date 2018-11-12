package com.aycap.lenddo.restful;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.aycap.lenddo.utils.DefaultRSAPublicKeyReader;
import com.aycap.simpleapp.client.crypto.AesGcmDecryptor;
import com.aycap.simpleapp.client.crypto.AesRsaHybridEncryptor;
import com.aycap.simpleapp.client.crypto.HybridEncryptor;

public class RestApiController {
	
	// Username & Password for Basic Authentication
	public static final String USERNAME = "lenddo_app";
	public static final String PASSWORD = "aa4577ght89f";

	public static String invokeRestApi(String apiUrl, String requestBody) throws GeneralSecurityException, IOException {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String responseBody = "";
		// ------------------------------------------------------------------------------------------
		// Reading public key
		InputStream inputStream = new FileInputStream("keys\\public-key.der");							//Input the path of public key
		RSAPublicKey rsaPublicKey = DefaultRSAPublicKeyReader.readFromDERFile(inputStream);
		// ------------------------------------------------------------------------------------------
		// Creating encrypted request body
		AesRsaHybridEncryptor encrypted = new AesRsaHybridEncryptor();
		HybridEncryptor.Output output = encrypted.encrypt(rsaPublicKey, requestBody.getBytes("UTF-8"));
		byte[] encryptedRequest = output.message;														//The format of encrypted request : |Random Key Length|Encrypted Random Key|Encrypted JSON|
		// ------------------------------------------------------------------------------------------
		// Calling an API
		URL myurl = new URL(apiUrl);
		HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
		String encoded = Base64.getEncoder().encodeToString((USERNAME+":"+PASSWORD).getBytes(StandardCharsets.UTF_8));  //Encoded Username & Password for Basic Authentication
		con.setRequestProperty("Authorization", "Basic " + encoded);													//
		con.setDoOutput(true);
		con.setDoInput(true);

		con.setRequestProperty("Content-Type", "application/json;");
		con.setRequestProperty("Accept", "application/json,text/plain");
		con.setRequestProperty("Method", "POST");
		OutputStream os = con.getOutputStream();
		os.write(encryptedRequest);
		os.close();

		int HttpResult = con.getResponseCode();
		byte[] encryptedResponse = null;
		if (HttpResult == HttpURLConnection.HTTP_OK) {

			encryptedResponse = new byte[con.getInputStream().available()];
			con.getInputStream().read(encryptedResponse);

		} else {

			System.out.println("An error occured while calling the API.");
			System.out.println(con.getResponseCode());
			System.out.println(con.getResponseMessage());
		}

		// ------------------------------------------------------------------------------------------
		//This section is to decrypt encrypted response
		AesGcmDecryptor aesGcmDecryptor = new AesGcmDecryptor();
		byte[] decryptedResponse = aesGcmDecryptor.decrypt(output.randomKey, output.nonce, encryptedResponse);
		responseBody = new String(decryptedResponse, "UTF-8");
		return responseBody;
	}

}
