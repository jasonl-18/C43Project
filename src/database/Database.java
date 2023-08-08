package database;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ui.SearchFilterParameters;
import users.*;

public class Database {

	private static final String dbClassName = "com.mysql.cj.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/c43_proj";
	
	private static Connection con;
	
	
	public Database() throws ClassNotFoundException, SQLException {
		//Register JDBC driver
		Class.forName(dbClassName);
		//Database credentials
		final String USER = "root";
		final String PASS = "";
		System.out.println("Connecting to database...");
		con = DriverManager.getConnection(CONNECTION,USER,PASS);
		System.out.println("Successfully connected to MySQL!");
	}
	
	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("DB connection failed to close");
			e.printStackTrace();
		}
	}
	
	private static void runUpdateTransaction(String tag, PreparedStatement... sqls) {
		try {
			con.setAutoCommit(false);
			for (PreparedStatement sql : sqls) { 
				sql.execute();
			}
			con.commit();
		}
		catch (SQLException e) {
			try {
				System.err.print("Transaction is being rolled back");
				con.rollback(); 
			}
			catch ( SQLException e1){ e1.printStackTrace(); }
		}
	}
	private static void runUpdate(String tag, String... sqls) {
		try (Statement stmt = con.createStatement()){
			con.setAutoCommit(false);
			for (String sql : sqls) { stmt.execute(sql); }
			con.commit();
			stmt.close();
		}
		catch (SQLException e) {
			try {
				System.err.println("Transaction is being rolled back");
				con.rollback(); 
			}
			catch ( SQLException e1) {}
			e.printStackTrace();
		}
	}
	
	private static ResultSet runQuery(String tag, String sql) {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			//System.out.println(sql);
			//stmt.close();
			return rs;
		}
		catch(SQLException e) {
			System.out.println("Query failed: " + tag);
			e.printStackTrace();
		}
		return null;
	}
	
	public void registerRenter(String name, String address, String dob, String occupation, String sin, String username, String password) {
		//note order is reversed because con.commit() executes statements like a queue
		runUpdate("Add Renter", 
				String.format("INSERT INTO users VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');", name, address, dob, occupation, sin, username, password),
				String.format("INSERT INTO renters(sin) VALUES('%s');", sin));
		
	}
	public void registerHost(String name, String address, String dob, String occupation, String sin, String username, String password){
		runUpdate("Add Host", 
				String.format("INSERT INTO users VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');", name, address, dob, occupation, sin, username, password),
				String.format("INSERT INTO hosts(sin) VALUES('%s');", sin));
	}
	public void addCard(String sin, String card) {
		runUpdate("Adding credit card info", String.format("UPDATE renters SET card_num = '%s' WHERE sin = '%s'", sin, card));
	}
	public boolean isHost(String sin) throws SQLException {
		ResultSet rs = runQuery("Checking User type", String.format("SELECT * FROM hosts WHERE sin = '%s'", sin));
		if(rs.next()) {
			return true;
		}
		return false;
	}
	public User authenticateUser(String username, String password) {
		ResultSet rs = runQuery("Authenticate User", String.format("SELECT * FROM users WHERE username = '%s' AND password = '%s'", username, password));
		try {
			if(rs.next()) {
				String sin = rs.getString(5);
				if(isHost(sin)) { return new Host(sin); }
				else { return new Renter(sin); }
			}
			return null;
		} catch (SQLException e) {
			System.out.println("Failed to authenticate user.");
			e.printStackTrace();
			return null;
		}
	}
	public void deleteUser(String sin) {
		runUpdate("Delete user", String.format("DELETE FROM users WHERE sin = '%s'", sin));
	}
	
	public void addListing(String type, float latitude, float longitude, String address, String postal, String city, String country, float price, String[] amenities) {
		try {
			runUpdate("Add Listing", 
					String.format("INSERT INTO listings VALUES( NULL, '%s', %f, %f, '%s', '%s', '%s', '%s', %f, '%s');", 
							type, latitude, longitude, address, postal, city, country, price, User.getInstance().getSin()));
			ResultSet rs = runQuery("Get last insert id", "SELECT LAST_INSERT_ID() AS id FROM listings");
			rs.next();
			String lid = rs.getString("id");
			for(String am : amenities) {
				runUpdate("Insert Amenity", String.format("INSERT INTO amenities VALUES('%s', '%s')", am, lid));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		
	}
	
	public ResultSet getHostListings(String sin) {
		return runQuery("Get Host Listings", String.format("SELECT * FROM listings where host_sin = '%s'", sin));
	}
	
	public ResultSet getAllListings() {
		return runQuery("Get Host Listings", "SELECT * FROM listings");
	}
	public String getListingAmenities(int lid) {
		String amenities = "";
		try {
			ResultSet rs = runQuery("Get listing amenities", String.format("SELECT am_type FROM amenities WHERE lid = %d", lid));
			List<String> ams = new ArrayList<>();
			while(rs.next()) {
				ams.add(rs.getString(1));
			}
			amenities = ams.toString();
		}
		catch (SQLException e){
			
		}
		return amenities;
	}
	public String getListingAvailability(int lid) {
		String avails = "";
		try {
			ResultSet rs = runQuery("Get listing availability", String.format("SELECT date, price FROM calendar WHERE lid = %d", lid));
			List<String> avs = new ArrayList<>();
			while(rs.next()) {
				avs.add(String.format("%s:$%.2f", rs.getString("date"), rs.getFloat("price")));
			}
			avails = avs.toString();
		}
		catch (SQLException e){
			
		}
		return avails;
	}
	
	public boolean hostOwnsListing(int lid, String sin) {
		boolean owns = false;
		try {
			ResultSet rs = runQuery("Check if host owns listing", String.format("SELECT lid FROM listings WHERE host_sin = '%s' AND lid = %d", sin, lid));
			if(rs.next()) { owns = true; }
			else { owns = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return owns;
	}
	
	public boolean listingExists(int lid) {
		boolean exists = false;
		try {
			ResultSet rs = runQuery("Check if listing exists", String.format("SELECT lid FROM listings WHERE lid = %d", lid));
			if(rs.next()) { exists = true; }
			else { exists = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}
	public float getDefaultPrice(int lid) {
		float out = 100;
		try {
			ResultSet rs = runQuery("Get Listing default price", String.format("SELECT price FROM listings WHERE lid = %d", lid));
			rs.next();
			out =  rs.getFloat("price");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}
	public void addAvailability(int lid, String date) throws Exception {
		try (Statement stmt = con.createStatement()){
			float defPrice = getDefaultPrice(lid);
			runUpdate("Update availability", String.format("INSERT INTO calendar VALUES('%s', %f, '%s')", date, defPrice, lid));
		}
		catch (SQLException e) {
			throw new Exception("Failed to add availability - make sure this date is unique");
		}
		
	}
	
	public boolean checkAvailability(int lid, String date) {
		boolean avail = false;
		try {
			ResultSet rs = runQuery("Check if listing is available on date", String.format("SELECT date FROM calendar WHERE lid = %d AND date = '%s'", lid, date));
			if(rs.next()) { avail = false; }
			else { avail = true; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return avail;
	}
	
	public boolean checkIfBooked(int lid, String date) {
		boolean booked = false;
		try {
			ResultSet rs = runQuery("Check if listing is booked on date", String.format("SELECT date FROM bookings WHERE lid = %d AND date = '%s'", lid, date));
			if(rs.next()) { booked = true; }
			else { booked = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return booked;
	}
	public void updatePrice(float price, int selectedLid, String date) {
		runUpdate("Update Price", String.format("UPDATE calendar SET price = %f WHERE lid = %d AND date = '%s'", price, selectedLid, date));
	}

	public void removeAvailability(int lid, String date) {
		runUpdate("Remove Availability", String.format("DROP FROM calendar WHERE lid = %d AND date = '%s'", lid, date));
	}
	
	public ResultSet getListingBookings(int lid) {
		return runQuery("Get bookings for listing", String.format("SELECT * FROM bookings WHERE lid = %s ORDER BY date ASC", lid));
	}
	
	public void deleteListing (int lid) {
		runUpdate("Delete Listing", String.format("DROP FROM listings WHERE lid = %d", lid));
	}
	public void createBooking(int lid, String date, String renter_sin) {
		runUpdate("Creating booking and updating calendar",
				String.format("INSERT INTO bookings SELECT '%s', date, price, lid from calendar WHERE date = '%s' AND lid = %d;", renter_sin, date, lid),
				String.format("Delete from calendar WHERE date = '%s' AND lid = %d;", date, lid));
	}
	
	public ResultSet getAllBookings(String sin) {
		return runQuery("Get user bookings", String.format("SELECT * FROM bookings WHERE renter_sin = '%s'", sin));
	}
	
	public boolean checkRenterBookedListing(String sin, int lid) {
		boolean booked = false;
		try {
			ResultSet rs = runQuery("Check if user booked this listing", String.format("SELECT 1 FROM bookings WHERE lid = %d AND renter_sin = '%s'", lid, sin));
			if(rs.next()) { booked = true; }
			else { booked = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return booked;
	}
	public boolean checkRenterBookedDate(String sin, int lid, String date) {
		boolean booked = false;
		try {
			ResultSet rs = runQuery("Check if user booked this listing", String.format("SELECT 1 FROM bookings WHERE lid = %d AND renter_sin = '%s' AND date = '%s'", lid, sin, date));
			if(rs.next()) { booked = true; }
			else { booked = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return booked;
	}
	public boolean checkDateBooked(int lid, String date) {
		boolean booked = false;
		try {
			ResultSet rs = runQuery("Check if listing is booked on this date", String.format("SELECT 1 FROM bookings WHERE lid = %d AND date = '%s'", lid, date));
			if(rs.next()) { booked = true; }
			else { booked = false; }
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return booked;
	}
	
	public ResultSet getUserListingBookings(String sin, int lid) {
		return runQuery("Get user bookings for a listing", String.format("SELECT * FROM bookings WHERE renter_sin = '%s' AND lid = %d", sin, lid));
	}
	
	public void userCancelBooking(String sin, int lid, String date) {
		runUpdate("Creating booking and updating calendar",
				String.format("INSERT INTO calendar SELECT date, price, lid from bookings WHERE lid = %d AND date = '%s';", lid, date),
				String.format("Delete from bookings WHERE lid = %d AND date = '%s';", lid, date),
				String.format("INSERT INTO cancels (user_sin, count) VALUES ('%s', 1) ON DUPLICATE KEY UPDATE count = count + 1;", sin));
	}
	
	public Set<String> getReviewableRenters(String sin) {
		ResultSet rs = runQuery("Get reviewable Renters", String.format("(SELECT renter_sin FROM (SELECT * FROM listings WHERE host_sin = '%s') L INNER JOIN bookings C ON L.lid = C.lid) "
				+ "EXCEPT (SELECT target_sin FROM user_reviews);", sin));
		Set<String> renters = new HashSet<>();
		try {
			while(rs.next()) {
				renters.add(rs.getString("renter_sin"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return renters;
	}
	public Set<String> getReviewableHosts(String sin){
		ResultSet rs = runQuery("Get reviewable hosts", String.format("SELECT DISTINCT host_sin FROM listings L INNER JOIN bookings B ON L.lid = B.lid WHERE renter_sin = '%s';", sin));
		Set<String> hosts = new HashSet<>();
		try {
			while(rs.next()) {
				hosts.add(rs.getString("host_sin"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return hosts;
	}
	public void addUserReview(String writer_sin, String target_sin, String comment, int rating) {
		runUpdate("Add user review", String.format("INSERT INTO user_reviews VALUES('%s', '%s', '%s', %d) "
				+ "ON DUPLICATE KEY UPDATE comment = '%s', rating = %d; ", writer_sin, target_sin, comment, rating, comment, rating));
	}
	
	public Set<Integer> getUserBookedListings(String sin) {
		ResultSet rs = runQuery("Get booked listings", String.format("SELECT DISTINCT lid FROM bookings WHERE renter_sin = '%s'", sin));
		Set<Integer> listings = new HashSet<>();
		try {
			while(rs.next()) {
				listings.add(rs.getInt("lid"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return listings;
	}
	public void addListingReview(String writer_sin, int target_lid, String comment, int rating) {
		runUpdate("Add user review", String.format("INSERT INTO user_reviews VALUES('%s', %d, '%s', %d) "
				+ "ON DUPLICATE KEY UPDATE comment = '%s', rating = %d; ", writer_sin, target_lid, comment, rating, comment, rating));
	}
	
	//SEARCHES
	
	private static String buildSearchString(SearchFilterParameters params) {
		String where = "";
		if(!(params.filterTime || (params.filterAmenities || params.filterPrice))) {
			return where;
		}
		where = "WHERE ";
		if(params.filterTime) {
			where += String.format("EXISTS (SELECT 1 FROM calendar WHERE calendar.lid = listings.lid AND '%s' <= calendar.date AND calendar.date <= '%s') AND ",
					params.startDate, params.endDate);
		}
		if(params.filterAmenities) {
			where += String.format("lid IN (SELECT lid FROM amenities WHERE am_type IN %s GROUP BY lid HAVING COUNT(am_type) >= %d) AND ", amenitiesToTuple(params.amenities), params.amenities.length);
		}
		if(params.filterPrice) {
			where += String.format("(%.2f < price AND price < %.2f) AND ", params.priceLower, params.priceUpper);
		}
		return where.substring(0, where.length() - 5);
		
	}
	
	private static String buildSortString(SearchFilterParameters params) {
		if(params.sortbyDistance) {
			return "ORDER BY distance ASC";
		}
		else if(params.sortByPrice) {
			return "ORDER BY price " + params.priceSortOrder; 
		}
		return "";
	}
	private static String amenitiesToTuple(String[] amenities) {
		String ams = "(";
		for(int i = 0 ; i < amenities.length; i++) {
			if(i == 0) {
				ams = ams.concat(String.format("'%s'", amenities[i]));
			}
			else {
				ams = ams.concat(String.format(", '%s'", amenities[i]));
			}
		}
		ams = ams.concat(")");
		return ams;
	}
	public ResultSet searchByCoords(float latitude, float longitude, float distance, SearchFilterParameters params) {
		return runQuery("Search by coordinates", String.format("SELECT *, (ST_Distance_Sphere(point(longitude, latitude), point(%f, %f))/1000) AS distance "
				+ "FROM listings "
				+ "%s "
				+ "HAVING distance < %.4f "
				+ "%s ", longitude, latitude, buildSearchString(params), distance, buildSortString(params)));
	}
	
	public ResultSet searchByZip(String postal, SearchFilterParameters params) {
		return runQuery("Search by postal", String.format("SELECT * "
				+ "FROM listings "
				+ "%s "
				+ "HAVING postal LIKE '%%%s%%'"
				+ "%s ", buildSearchString(params), postal, buildSortString(params)));
	}
	public ResultSet searchByAddress(String address, SearchFilterParameters params) {
		return runQuery("Search by address", String.format("SELECT * "
				+ "FROM listings "
				+ "%s "
				+ "HAVING address LIKE '%%%s%%'"
				+ "%s ;", buildSearchString(params), address, buildSortString(params)));
	}
	
	//REPORTS
	
	public ResultSet countListingByCountry() {
		return runQuery("Count listings by country", "SELECT country, COUNT(lid) AS count FROM listings GROUP BY country;");
	}
	
	public ResultSet countListingByCountryCity() {
		return runQuery("Count listings by country", "SELECT country, city, COUNT(lid) AS count FROM listings GROUP BY country, city;");
	}
	
	public ResultSet countListingByCountryCityPostal() {
		return runQuery("Count listings by country", "SELECT country, city, postal, COUNT(lid) AS count FROM listings GROUP BY country, city, postal;");
	}
	
	public ResultSet getCancellations() {
		return runQuery("Get cancellations", "SELECT U.name AS name, C.count AS count FROM users U INNER JOIN cancels C ON C.user_sin = U.sin ORDER BY count DESC;");
	}
	 

}
