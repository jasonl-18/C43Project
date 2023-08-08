package ui;

public class SearchFilterParameters {
	public boolean filterTime;
	public String startDate;
	public String endDate;
	public boolean filterAmenities;
	public String[] amenities;
	public boolean filterPrice;
	public float priceLower;
	public float priceUpper;
	public boolean sortbyDistance;
	public boolean sortByPrice;
	public String priceSortOrder;
	
	public SearchFilterParameters() {
		this.filterTime = false;
		this.filterAmenities = false;
		this.filterPrice = false;
	}
}
