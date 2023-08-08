package ui;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import users.*;
import app.Driver;
import helpers.InputHandler;
import listings.Listing;


public final class AppActions {
	
	public static void loginUser() {
		try {
			System.out.print("Enter your username: ");
	        String username = lengthValidator(InputHandler.readLine(), 50);
	        System.out.print("Enter your password: ");
	        String password = lengthValidator(InputHandler.readLine(), 50);
	        User u = userValidator(Driver.db.authenticateUser(username, password));
	        User.setInstance(u);
	        if(u instanceof Host) {
	        	AppMenus.hostMenu.execute();
	        }
	        else {
	        	AppMenus.renterMenu.execute();
	        }
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void createUser() {
		try {
			System.out.print("Enter your first and last name: ");
            String name = lengthValidator(InputHandler.readLine(), 50);
            System.out.print("Enter your address: ");
            String address = lengthValidator(InputHandler.readLine(), 100);
            System.out.print("Enter your date of birth in the following format - YYYY-MM-DD: ");
			String dob = dobValidator(InputHandler.readLine());
            System.out.print("Enter your occupation: ");
            String occupation = lengthValidator(InputHandler.readLine(), 50);
            System.out.print("Enter your SIN number: ");
            String sin = sinValidator(InputHandler.readLine());
			
            System.out.print("Enter your username: ");
            String username = lengthValidator(InputHandler.readLine(), 50);
            System.out.print("Enter your password: ");
            String password = lengthValidator(InputHandler.readLine(), 50);
            System.out.print("Are you a renter(r) or a host(h)?: ");
            boolean isRenter = renterHostValidator(InputHandler.readLine());
            
            if(isRenter) {
            	Driver.db.registerRenter(name, address, dob, occupation, sin, username, password);
            }
            else {
            	Driver.db.registerHost(name, address, dob, occupation, sin, username, password);
            }
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void deleteUser() {
		try {
			System.out.print("Are you sure you want to delete your account? (y/n): ");
			boolean delete = yesNoValidator(InputHandler.readLine());
			if(delete) { Driver.db.deleteUser(User.getInstance().getSin()); Menu.earlyExit=true;}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	//HOST ACTIONS
	private static void suggestAmenities(Set<Integer> choices) {
		System.out.println("Here are some suggested amenities you might like to add:");
		for(int i = 0; i< 10; i++) {
			if (!choices.contains(i)) {
				String am = Listing.amenities[i];
				System.out.printf("%s: + $%d\n", am, am.length());
			}
		}
	}
	private static void suggestPrice(String[] amenities) {
		float base = 137;
		for(String am : amenities) {
			base += am.length();
		}
		System.out.printf("The suggested price of this listing is: $%.2f\n", base);
	}
	
	public static void createListing() {
		try {
			printChoices("Choose the type of your listing - [0-5]", Listing.types);
			String type = Listing.types[singleChoiceValidator(InputHandler.readLine(), Listing.types.length)];
			System.out.print("Latitude: ");
	        Float latitude = latitudeValidator(InputHandler.readLine());
			System.out.print("Longitude: ");
	        Float longitude = longitudeValidator(InputHandler.readLine());
	        System.out.print("Address: ");
	        String address = lengthValidator(InputHandler.readLine(), 100);
			System.out.print("Postal Code: ");
	        String postal = lengthValidator(InputHandler.readLine(), 7);
	        System.out.print("City: ");
	        String city = lengthValidator(InputHandler.readLine(), 100);
	        System.out.print("Country: ");
	        String country = lengthValidator(InputHandler.readLine(), 50);
	        printChoices("Enter the amentities you want to include in the form of numbers [0-9] separated by ',' :", Listing.amenities);
	        
	        Set<Integer> amenityChoices = choicesValidator(InputHandler.readLine());
	        
	        suggestAmenities(amenityChoices);
	        System.out.print("Would you like to add more amenities? - (y/n):");
	        if(yesNoValidator(InputHandler.readLine())) { 
	        	printChoices("Enter the amentities you want to include in the form of numbers [0-9] separated by ',' :", Listing.amenities);
	        	amenityChoices.addAll(choicesValidator(InputHandler.readLine()));
	        }
	        String[] amenities = choicesToAmenities(amenityChoices);
	        

	        suggestPrice(amenities);
	        System.out.print("Default Price: ");
	        Float price = floatValidator(InputHandler.readLine());
	        
	        Driver.db.addListing(type, latitude, longitude, address, postal, city, country, price, amenities);
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void hostViewListings() {
		ResultSet rs = Driver.db.getHostListings(User.getInstance().getSin());
		Listing.printListings(rs);
		InputHandler.waitEnter();
	}
	
	public static void editListing() {
		try {
			System.out.print("Enter a listing ID to update: ");
			int lid = intValidator(InputHandler.readLine());
			if(!Driver.db.hostOwnsListing(lid, User.getInstance().getSin())) { throw new Exception("You don't own this listing / this listing does not exist"); };
			Listing.selectedLid = lid;
			AppMenus.listingMenu.execute();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void addAvailability() {
		try {
			System.out.print("Enter an available date - YYYY-MM-DD: ");
			String date = dateValidator(InputHandler.readLine());
			if(Driver.db.checkDateBooked(Listing.selectedLid, date)) { throw new Exception("This date is booked"); }
			if(!Driver.db.checkAvailability(Listing.selectedLid, date)) { throw new Exception("This date is already available"); }
			Driver.db.addAvailability(Listing.selectedLid, date);
			System.out.println("Update successful: Availability has been added");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void updatePrice() {
		try {
			System.out.print("Enter the date to update - YYYY-MM-DD: ");
			String date = dateValidator(InputHandler.readLine());
			if(!Driver.db.checkAvailability(Listing.selectedLid, date)) { throw new Exception("Update failed - this date is not available for this listing"); }
			System.out.print("Enter a price for this date: ");
			float price = floatValidator(InputHandler.readLine());
			Driver.db.updatePrice(price, Listing.selectedLid, date);
			System.out.println("Update successful: price has been updated");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void removeAvailability() {
		try {
			System.out.print("Enter the date to update - YYYY-MM-DD: ");
			String date = dateValidator(InputHandler.readLine());
			if(!Driver.db.checkAvailability(Listing.selectedLid, date)) { throw new Exception("Update failed - this date is not available for this listing"); }
			Driver.db.removeAvailability(Listing.selectedLid, date);
			System.out.println("Update successful: availability has been removed");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void hostCancelBooking() {
		try {
			int lid = Listing.selectedLid;
			Listing.printBookings();
			System.out.print("Choose a date to cancel: ");
			String date = dateValidator(InputHandler.readLine());
			if(!Driver.db.checkDateBooked(lid, date)) { throw new Exception("Not a valid booking date"); }
			
			Driver.db.userCancelBooking(User.getInstance().getSin(), lid, date);
			System.out.println("Booking successfully cancelled");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void deleteListing() {
		try {
			System.out.print("Are you sure you want to delete this listing? (y/n): ");
			boolean delete = yesNoValidator(InputHandler.readLine());
			if(delete) { Driver.db.deleteListing(Listing.selectedLid); Menu.earlyExit=true; }
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void reviewRenter() {
		try {
			Set<String> renters = Driver.db.getReviewableRenters(User.getInstance().getSin());
			if(renters.size() == 0) { throw new Exception("No Renters to Review");} 
			System.out.println("Renters to review: ");
			for(String r : renters) {
				System.out.printf("%s  ", r);
			}
			System.out.println();
			
			System.out.print("Enter a renter id: ");
			String renter_sin = sinValidator(InputHandler.readLine());
			if(!renters.contains(renter_sin)) { throw new Exception("Not a valid renter!"); }
			
			System.out.print("Enter a comment: ");
			String comment = lengthValidator(InputHandler.readLine(), 200);
			
			System.out.print("Enter a rating: ");
			int rating = intValueValidator(InputHandler.readLine(), 1, 5);
			
			Driver.db.addUserReview(User.getInstance().getSin(), renter_sin, comment, rating);
			System.out.println("Succesfully added comment");
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	//RENTER ACTIONS
	public static void addPaymentInfo() {
		try {
			System.out.print("Enter your credit card number - (16 digits exactly): ");
			String num = creditCardValidator(InputHandler.readLine());
			Driver.db.addCard(User.getInstance().getSin(), num);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	public static void viewAllListings() {
		Listing.printListings(Driver.db.getAllListings());
		InputHandler.waitEnter();
	}
	
	public static void bookListing() {
		try {
			System.out.print("Enter a listing ID to book: ");
			int lid = intValidator(InputHandler.readLine());
			if(!Driver.db.listingExists(lid)) { throw new Exception("This listing does not exist"); };
			Listing.selectedLid = lid;
			
			Listing.printAvailability(lid);
			System.out.print("Enter a date to book: ");
			String date = dateValidator(InputHandler.readLine());
			if(!Driver.db.checkAvailability(Listing.selectedLid, date)) { throw new Exception("The listing is not available for booking on this day"); }
			
			Driver.db.createBooking(lid, date, User.getInstance().getSin());
			System.out.println("Booking successful!");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void viewBookings() {
		try {
			ResultSet rs = Driver.db.getAllBookings(User.getInstance().getSin());
			printBookings(rs);
		}
		catch (SQLException e){
			System.out.println("Failed to get bookings.");
		}
		InputHandler.waitEnter();
	}
	
	public static void cancelBooking() {
		try {
			String sin = User.getInstance().getSin();
			System.out.print("Enter a listing ID to cancel: ");
			int lid = intValidator(InputHandler.readLine());
			if(!Driver.db.checkRenterBookedListing(sin, lid)) { throw new Exception("You did not book this listing"); };
			
			printBookings(Driver.db.getUserListingBookings(sin, lid));
			System.out.print("Enter a date to cancel: ");
			String date = dateValidator(InputHandler.readLine());
			if(!Driver.db.checkRenterBookedDate(sin, lid, date)) { throw new Exception("Not a valid booking date"); }
			
			Driver.db.userCancelBooking(sin, lid, date);
			System.out.println("Booking successfully cancelled");
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void reviewListing() {
		try {
			Set<Integer> listings = Driver.db.getUserBookedListings(User.getInstance().getSin());
			if(listings.size() == 0) { throw new Exception("No Listings to Review");} 
			System.out.println("Listings to review: ");
			for(Integer r : listings) {
				System.out.printf("%s  ", r);
			}
			System.out.println();
			
			System.out.print("Enter a listing id: ");
			int lid = intValidator(InputHandler.readLine());
			if(!listings.contains(lid)) { throw new Exception("Not a valid listing!"); }
			
			System.out.print("Enter a comment: ");
			String comment = lengthValidator(InputHandler.readLine(), 200);
			
			System.out.print("Enter a rating: ");
			int rating = intValueValidator(InputHandler.readLine(), 1, 5);
			
			Driver.db.addListingReview(User.getInstance().getSin(), lid, comment, rating);
			System.out.println("Succesfully added listing review");
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void reviewHost() {
		try {
			Set<String> hosts = Driver.db.getReviewableHosts(User.getInstance().getSin());
			if(hosts.size() == 0) { throw new Exception("No hosts to Review");} 
			System.out.println("hosts to review: ");
			for(String r : hosts) {
				System.out.printf("%s  ", r);
			}
			System.out.println();
			
			System.out.print("Enter a host id: ");
			String host_sin = sinValidator(InputHandler.readLine());
			if(!hosts.contains(host_sin)) { throw new Exception("Not a valid host!"); }
			
			System.out.print("Enter a comment: ");
			String comment = lengthValidator(InputHandler.readLine(), 200);
			
			System.out.print("Enter a rating: ");
			int rating = intValueValidator(InputHandler.readLine(), 1, 5);
			
			Driver.db.addUserReview(User.getInstance().getSin(), host_sin, comment, rating);
			System.out.println("Succesfully added comment");
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	//SEARCHES
	public static void searchByCoords() {
		try {
			SearchFilterParameters params = new SearchFilterParameters();
			params.sortbyDistance = true;
			
			System.out.print("Enter a latitude: ");
			float latitude = latitudeValidator(InputHandler.readLine());
			System.out.print("Enter a longitude: ");
			float longitude = longitudeValidator(InputHandler.readLine());
			
			System.out.print("Enter a distance in km - default 5 km: ");
			float distance = distanceValidator(InputHandler.readLine());
			
			System.out.print("Do you want to add additional filters and sorts? (y/n): ");
			if(yesNoValidator(InputHandler.readLine())) {
				promptFilters(params);
			};
			
			System.out.println("Search Results:");
			Listing.printListingsDistance(Driver.db.searchByCoords(latitude, longitude, distance, params));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void searchByPostal() {
		try {
			SearchFilterParameters params = new SearchFilterParameters();
			
			System.out.print("Enter a postal code: ");
			String postal = InputHandler.readLine();
			System.out.print("Do you want to add additional filters and sorts? (y/n): ");
			if(yesNoValidator(InputHandler.readLine())) {
				promptFilters(params);
			};

			System.out.println("Search Results:");
			Listing.printListings(Driver.db.searchByZip(postal, params));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void searchByAddress() {
		try {
			SearchFilterParameters params = new SearchFilterParameters();
			
			System.out.print("Enter an address: ");
			String address = InputHandler.readLine();
			System.out.print("Do you want to add additional filters and sorts? (y/n): ");
			if(yesNoValidator(InputHandler.readLine())) {
				promptFilters(params);
			};

			System.out.println("Search Results:");
			Listing.printListings(Driver.db.searchByAddress(address, params));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private static void promptFilters(SearchFilterParameters params) throws Exception{
		System.out.print("Do you want to add a date filter? (y/n): ");
		params.filterTime = yesNoValidator(InputHandler.readLine());
		if(params.filterTime) {
			System.out.print("Enter a starting date - (YYY-MM-DD): ");
			params.startDate = dateValidator(InputHandler.readLine());
			
			System.out.print("Enter a end date - (YYY-MM-DD): ");
			params.endDate = dateValidator(InputHandler.readLine());
		}
		
		System.out.print("Do you want to add an amenities filter? (y/n): ");
		params.filterAmenities = yesNoValidator(InputHandler.readLine());
		if(params.filterAmenities) {
			printChoices("Enter the amentities you want to search for in the form of [0-9] separated by ',' :", Listing.amenities);
	        params.amenities = choicesToAmenities(choicesValidator(InputHandler.readLine()));
		}
		
		System.out.print("Do you want to add a price filter? (y/n): ");
		params.filterPrice = yesNoValidator(InputHandler.readLine());
		if(params.filterPrice) {
			System.out.print("Enter a lower bound for price: ");
			params.priceLower = floatValidator(InputHandler.readLine());
			
			System.out.print("Enter an upper bound for price: ");
			params.priceUpper = floatValidator(InputHandler.readLine());
		}
		
		System.out.print("Rank by price? (y/n): ");
		params.sortByPrice = yesNoValidator(InputHandler.readLine());
		if(params.sortByPrice) {
			System.out.print("Enter an sort order for price - (ASC/DESC): ");
			params.priceSortOrder = ascDescValidator(InputHandler.readLine());
		}
	}
	
	
	//REPORTS
	public static void countBookings() {
		ResultSet rs = Driver.db.countListingByCountry();
		
	}
	
	public static void countListings() {
		try {
			ResultSet rs = Driver.db.countListingByCountry();
			System.out.println("Listings by Country:");
			if(rs.next()) {
				do {
					System.out.printf("%s, %d\n", rs.getString("country"), rs.getInt("count"));
				}while(rs.next());
			}
			else {
				System.out.println("No listings!");
			}
			
			System.out.println("Listings by Country and City:");
			rs = Driver.db.countListingByCountryCity();
			if(rs.next()) {
				do {
					System.out.printf("%s, %s, %d\n", rs.getString("country"), rs.getString("city"), rs.getInt("count"));
				}
				while(rs.next());
			}
			else {
				System.out.println("No listings!");
			}
			
			System.out.println("Listings by Country, City, and Zip:");
			rs = Driver.db.countListingByCountryCityPostal();
			if(rs.next()) {
				do {
					System.out.printf("%s, %s, %s, %d\n", rs.getString("country"), rs.getString("city"), rs.getString("postal"), rs.getInt("count"));
				}
				while(rs.next());
			}
			else {
				System.out.println("No listings!");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void reportTopCancels() {
		try {
			System.out.println("The top cancellers:");
			ResultSet rs = Driver.db.getCancellations();
			if(rs.next()) {
				do {
					System.out.printf("%s, %d\n", rs.getString("name"), rs.getInt("count"));
				}
				while(rs.next());
			}
			else {
				System.out.println("No listings!");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void farewell() {
		Driver.db.close();
		System.out.println("Exiting - Goodbye!");
		System.exit(0);
	}
	
	public static void empty() {
		System.out.println("This Action is empty!");
	}
	
	//Validators and Helpers
	private static void printBookings(ResultSet rs) throws SQLException{
		while(rs.next()) {
			System.out.printf("Listing #%d booked on %s for $%f.\n", rs.getInt("lid"), rs.getString("date"), rs.getFloat("price"));
		}
	}
	
	private static float distanceValidator(String input) throws Exception{
		float out = 5;
		try {
			if(input.equals("")) {
				return out;
			}
			else {
				out = floatValidator(input);
			}
		}
		catch (Exception e){
			throw e;
		}
		return out;
	}
	
	private static String creditCardValidator(String input) throws Exception{
		if(input.matches("[0-9]{16}")) {
			return input;
		}
		throw new Exception("Invalid credit card number: must be a 16 digit number");
	}
	
	private static void printChoices(String title, String...options) {
		System.out.println(title);
		for(int i = 0; i < options.length; i++) {
			System.out.printf("%d) %s\n", i, options[i]);
		}
		System.out.print("Choice:");
	}
	private static String ascDescValidator(String input) throws Exception{
		if (input.equals("ASC") || input.equals("DESC")) {
			return input;
		}
		throw new Exception("Invalid choice: please enter 'ASC' or 'DESC'\n");
	}
	private static boolean yesNoValidator(String input) throws Exception{
		if (input.equals("y")) {
			return true;
		}
		else if (input.equals("n")) {
			return false;
		}
		throw new Exception("Invalid choice: please enter 'y' or 'n'\n");
	}
	
	private static int singleChoiceValidator(String choice, int opBound) throws Exception{
		try {
			int c = Integer.parseInt(choice);
			if(c < 0 || c >= opBound) { throw new Exception(); }
			return c;
		}
		catch (Exception e) {
			throw new Exception(String.format("Invalid choice - enter a number between 0 - %d", opBound-1));
		}
		
	}
	private static Set<Integer> choicesValidator(String input) throws Exception{
		try {
			if(input.matches("^[0-9](,[0-9])*$")) {
				String[] cStrs = input.split(",");
				Set<Integer> choices = new HashSet<>();
				for(String c : cStrs) {
					choices.add(Integer.parseInt(c));
				}
				return choices;
			}
			throw new Exception();
		}
		catch (Exception e) {
			throw new Exception("Unable to parse choices - make sure your input is formatted correctly");
		}
	}
	private static String[] choicesToAmenities(Set<Integer> choices) {
		List<String> ams = new ArrayList<>();
		for(Integer c : choices) {
			ams.add(Listing.amenities[c]);
		}
		return ams.toArray(new String[0]);
	}
	private static String lengthValidator(String str, int len) throws Exception{
		if(str.length() <= len) {
			return str.trim();
		}
		throw new Exception(String.format("Invalid Length: this field must have %d characters or less\n", len));
	}
	private static String dateValidator(String dob) throws Exception{
		if (dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
				return dob;
		}
		throw new Exception("Invalid Date: the format must be YYYY-MM-DD\n");
	}
	private static String dobValidator(String dob) throws Exception{
		if (dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
			int year = Integer.parseInt(dob.substring(0, 4));
			if(Calendar.getInstance().get(Calendar.YEAR) - year >= 18) {
				return dob;
			}
		}
		throw new Exception("Invalid Date: note you must be at least 18\n");
	}
	private static String sinValidator(String sin) throws Exception{
		if (sin.length() == 9) {
			return sin;
		}
		throw new Exception("Invalid SIN: please enter a valid SIN\n");
	}
	private static boolean renterHostValidator(String type) throws Exception{
		if (type.equals("h")) {
			return false;
		}
		else if (type.equals("r")) {
			return true;
		}
		throw new Exception("Invalid choice: please enter 'h' or 'r'\n");
	}
	private static User userValidator(User u) throws Exception{
		if(u != null) {
			return u;
		}
		throw new Exception("Unable to authenticate this user - make sure your username and password is correct\n");
	}

	private static int intValidator(String input) throws Exception{
		int out;
		try {
			out  = Integer.parseInt(input);
		}
		catch (Exception e){
			throw new Exception("Unable to parse int - please enter a valid integer\n");
		}
		return out;
	}
	private static int intValueValidator(String input, int lower, int upper) throws Exception {
		int out;
		try {
			out = intValidator(input);
			if(out < lower || out > upper) { throw new Exception(String.format("Invalid value - please enter a value between %d and %d", lower, upper)); }
		}
		catch (Exception e) {
			throw e;
		}
		return out;
		
	}
	private static float floatValidator(String f) throws Exception{
		float out;
		try {
			out = Float.parseFloat(f);
		}
		catch (Exception e){
			throw new Exception("Unable to parse float - please enter a valid decimal value\n");
		}
		return out;
	}
	private static float latitudeValidator(String lat) throws Exception{
		float out;
		try {
			out = Float.parseFloat(lat);
			if(out < -90 || out > 90) { throw new Exception();};
		}
		catch (Exception e){
			throw new Exception("Unable to parse latitude - please enter a decimal value between -180 and 180\n");
		}
		return out;
	}
	private static float longitudeValidator(String longi) throws Exception{
		float out;
		try {
			out = Float.parseFloat(longi);
			if(out < -180 || out > 180) { throw new Exception();};
		}
		catch (Exception e){
			throw new Exception("Unable to parse longitude - please enter a decimal value between -90 and 90\n");
		}
		return out;
	}
}
