package tw.soleil.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import tw.soleil.constant.Constants;
import android.util.Base64;

public class Utils {
	public static String createSignature(Map<String, String> queryMap) {
		String orderedQueryStr = composeStrForSignatureEncryption(queryMap);
		byte[] base64Encoded = null;
		String signature = null;
		try {
			base64Encoded = Base64.encode(calculateRFC2104HMAC(orderedQueryStr, Constants.INVOICE_API_KEY).getBytes(Constants.CHARSET_FOR_INVOICE_API_ENCODING), Base64.DEFAULT);
			signature = new String(base64Encoded, Constants.CHARSET_FOR_INVOICE_API_ENCODING);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return signature;
	}
	
	private static String composeStrForSignatureEncryption(Map<String, String> queryMap) {
		List<String> keySet = new ArrayList<String>(queryMap.keySet());
		Collections.sort(keySet, new Comparator<String>() {

			/**
			 * compare string case insensitively
			 */
			@Override
			public int compare(String str1, String str2) {
				return str1.toLowerCase(Locale.ENGLISH).compareTo(str2.toLowerCase(Locale.ENGLISH));
			}
		});
		List <String> orderedKeySet = keySet;
		StringBuffer sb = new StringBuffer();
		for (String key : orderedKeySet) {
			sb.append(key)
				.append("=")
				.append(queryMap.get(key))
				.append("&");
		}
		sb.deleteCharAt(sb.length() - 1); // delete last '&'
		
		return sb.toString();
	}
	
	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String hexStr = formatter.toString();
		formatter.close();
 
		return hexStr;
	}
 
	private static String calculateRFC2104HMAC(String data, String key)
		throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		try {
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(Constants.CHARSET_FOR_INVOICE_API_ENCODING), Constants.HMAC_SHA1_ALGORITHM);
			Mac mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
			return toHexString(mac.doFinal(data.getBytes(Constants.CHARSET_FOR_INVOICE_API_ENCODING)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
