package com.aycap.lenddo.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

public class DefaultRSAPublicKeyReader {

	public static RSAPublicKey readFromDERFile(InputStream inputStream) {
		
		try {

			byte[] keyBytes = IOUtils.toByteArray(inputStream);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return (RSAPublicKey) kf.generatePublic(spec);
			
		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			
			if (inputStream != null) {
				
				try {
					
					inputStream.close();
					
				} catch (IOException ex) {

					ex.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
