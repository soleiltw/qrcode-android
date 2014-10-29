package tw.soleil.to;

import java.util.List;

public class Invoice {
	private List<InvoiceDtl> details;

	public List<InvoiceDtl> getDetails() {
		return details;
	}

	public void setDetails(List<InvoiceDtl> details) {
		this.details = details;
	}
}
