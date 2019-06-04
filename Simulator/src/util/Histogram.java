package util;

public class Histogram {

	String [] bins;
	String [] frequencies;
	
	public Histogram(String[] bins, String[] frequencies) {
		super();
		this.bins = bins;
		this.frequencies = frequencies;
	}
	
	
	
	public String[] getBins() {
		return bins;
	}
	public void setBins(String[] bins) {
		this.bins = bins;
	}
	public String[] getFrequencies() {
		return frequencies;
	}
	public void setFrequencies(String[] frequencies) {
		this.frequencies = frequencies;
	}



	public Histogram() {
		super();
		bins = new String[0];
		frequencies = new String[0];
	}




	
}
