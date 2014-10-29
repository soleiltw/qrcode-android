package tw.soleil.constant;

public class Constants {
	public static final String PATTERN_INVOICE_MAIN_QR_CODE_CONTENT = "^([A-Z]{2}\\d{8})(\\d{7})(\\d{4})\\w{16}\\d{8}(\\d{8})(.{24}):.*$";
	public static final String INVOICE_SERVICE_ENDPOINT = "https://www.einvoice.nat.gov.tw";
	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	public static final String CHARSET_FOR_INVOICE_API_ENCODING = "utf-8";
	public static final String LINE_DELIMITER = "\n";
	public static final int SCAN_FOR_INVOICE_DETAIL = 0;
	
	/* Credentials */
	public static final String INVOICE_API_ID = "EINV6201409069601";
	public static final String INVOICE_API_KEY = "MDJQT3l0MDRQemFGNmNlMg==";
}
