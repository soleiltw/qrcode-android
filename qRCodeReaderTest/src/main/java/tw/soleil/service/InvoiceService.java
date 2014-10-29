package tw.soleil.service;

import java.util.Map;

import retrofit.http.POST;
import retrofit.http.QueryMap;
import tw.soleil.to.Invoice;

public interface InvoiceService {
	@POST("/PB2CAPIVAN/invapp/InvApp")
	public Invoice getInvoice(@QueryMap Map<String, String> queryMap);
}
