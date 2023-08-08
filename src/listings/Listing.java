package listings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import app.Driver;

public class Listing {
	public static int selectedLid;
	public static final String[] types = {"Apartment", "House", "Secondary unit", "Unique space", "Bed and breakfast", "Boutique hotel"};
	public static final String[] amenities = {"Pool", "Wifi", "Kitchen", "Free Parking", "Jacuzzi", "Washer or Dryer", 
			"Air conditioning or heating", "Self check-in", "Laptop-friendly workspace", "Pets allowed"};
	
	public static void printListings(ResultSet rs) {
		try {
			if(rs.next()) {
				do {
					printListing(rs);
				}while(rs.next());
			}
			else {
				System.out.println("No Listings!");
			}
		}
		catch(SQLException e) {
			System.out.println("Unable to read listings");
			e.printStackTrace();
		}
		
	}
	
	public static void printListing(ResultSet rs) throws SQLException {
		int id = rs.getInt("lid");
		System.out.printf("Id: %d ; Type: %s ; Location: %s, %s %s, %s ; Default Price: $%.2f\n", 
				id, rs.getString("type"), rs.getString("address"), rs.getString("city"), rs.getString("postal"),  rs.getString("country"), rs.getFloat("price"));
		printAmenities(id);
		printAvailability(id);
	}
	public static void printListingDistance(ResultSet rs) throws SQLException{
		System.out.printf("Distance: %.3fkm away; Id: %d ; Type: %s ; Location: %s, %s %s, %s ; Default Price: $%.2f\n", 
				rs.getFloat("distance"), rs.getInt("lid"), rs.getString("type"), 
				rs.getString("address"), rs.getString("city"), rs.getString("postal"),  rs.getString("country"), rs.getFloat("price"));
	}
	
	public static void printAmenities(int lid) {
		System.out.printf("\tAmenities: %s\n", Driver.db.getListingAmenities(lid));
	}
	
	public static void printAvailability(int lid) {
		System.out.printf("\tAvailable: %s\n", Driver.db.getListingAvailability(lid));
	}
	
	public static void printBookings() {
		try {
			ResultSet rs = Driver.db.getListingBookings(selectedLid);
			while(rs.next()) {
				System.out.printf("Date: %s, Price: $%.2f\n", rs.getString("date"), rs.getFloat("price"));
			}
		}
		catch (SQLException e){
			
		}
	}
	
	public static void printListingsDistance(ResultSet rs) {
		try {
			if(rs.next()) {
				do {
					int lid = rs.getInt("lid");
					printListingDistance(rs);
					printAmenities(lid);
				}while(rs.next());
			}
			else {
				System.out.println("No Listings!");
			}
		}
		catch(SQLException e) {
			System.out.println("Unable to read listings");
			e.printStackTrace();
		}
	}
}
