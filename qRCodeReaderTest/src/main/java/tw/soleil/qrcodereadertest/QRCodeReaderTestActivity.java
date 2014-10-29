package tw.soleil.qrcodereadertest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.RestAdapter;
import tw.soleil.constant.Constants;
import tw.soleil.service.InvoiceService;
import tw.soleil.to.Invoice;
import tw.soleil.to.InvoiceDtl;
import tw.soleil.util.DateUtil;
import tw.soleil.util.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import eu.livotov.zxscan.ZXScanHelper;
import eu.livotov.zxscan.ZXUserCallback;

public class QRCodeReaderTestActivity extends Activity {
	private InvoiceService invoiceService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode_reader_test);
		invoiceService = provideInvoiceService(); // JTODO use Dagger to manage
		TextView openText = (TextView) findViewById(R.id.openText);
		openText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ZXScanHelper.setUserCallback(new ZXUserCallback() { // JTODO 之後弄一個abstract的Adapter來implement這個介面，只把onCodeRead()弄成抽象方法就好，這邊再new那個Adapter
					
					@Override
					public void onScannerActivityResumed(Activity captureActivity) {}
					@Override
					public void onScannerActivityDestroyed(Activity activity) {}
					@Override
					public void onScannerActivityCreated(Activity activity) {}
					/**
					 * 此QR code是否含所需資訊
					 */
					@Override
					public boolean onCodeRead(String code) {
						return code.matches(Constants.PATTERN_INVOICE_MAIN_QR_CODE_CONTENT);
					}
				});
				ZXScanHelper.scan(QRCodeReaderTestActivity.this, Constants.SCAN_FOR_INVOICE_DETAIL);
			}
		});
	}
	
	private InvoiceService provideInvoiceService() {
		return new RestAdapter.Builder()
			.setEndpoint(Constants.INVOICE_SERVICE_ENDPOINT)
			.build()
			.create(InvoiceService.class);
	}
	
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == RESULT_OK && requestCode == Constants.SCAN_FOR_INVOICE_DETAIL)
        {
            String scannedCode = ZXScanHelper.getScannedCode(data);
            new GetInvoiceTask().execute(scannedCode);
        }
    }
	
	private class GetInvoiceTask extends AsyncTask<String, Void, Invoice> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(QRCodeReaderTestActivity.this);
			progressDialog.setMessage("Loading..."); // JTODO 放string xml
			progressDialog.show();
		}

		@Override
		protected Invoice doInBackground(String... params) {
			return invoiceService.getInvoice(composeQueryMap(params[0]));
		}
		
		private Map<String, String> composeQueryMap(String rawQRCodeStr) {
			Map<String, String> queryMap = new HashMap<String, String>();
			// REFINE value放Constants
			queryMap.put("version", "0.2");
			queryMap.put("type", "QRCode");
			queryMap.put("action", "qryInvDetail");
			queryMap.put("generation", "V2");
			// end
			Matcher matcher = Pattern.compile(Constants.PATTERN_INVOICE_MAIN_QR_CODE_CONTENT).matcher(rawQRCodeStr);
			if (matcher.find()) {
				queryMap.put("invNum", matcher.group(1));
				queryMap.put("invDate", DateUtil.getEDateWithSep(matcher.group(2), "/"));
				queryMap.put("encrypt", matcher.group(5));
				queryMap.put("sellerID", matcher.group(4));
				queryMap.put("UUID", Build.SERIAL); // API Level 9(含)以上才取的到
				queryMap.put("randomNumber", matcher.group(3));
				queryMap.put("appID", Constants.INVOICE_API_ID);
			}
			queryMap.put("signature", Utils.createSignature(queryMap));
			
			return queryMap;
		}
		
		@Override
		protected void onPostExecute(Invoice invoice) {
			Toast.makeText(QRCodeReaderTestActivity.this, getDetailsMsg(invoice), Toast.LENGTH_LONG).show();
			progressDialog.cancel();
		}
		
		private String getDetailsMsg(Invoice invoice) {
			StringBuffer sb = new StringBuffer();
			for (InvoiceDtl invoiceDtl : invoice.getDetails()) {
				sb.append(invoiceDtl.getDescription())
					.append(Constants.LINE_DELIMITER);
			}
			sb.deleteCharAt(sb.length() - 1); // delete the last line delimiter
			return sb.toString();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qrcode_reader_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
