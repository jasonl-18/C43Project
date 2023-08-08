package ui;

import listings.Listing;

public final class AppMenus {

	public static Menu startMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ MENU ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Exit") { public void action() { AppActions.farewell(); }},
			new MenuAction("Log in") { public void action() { AppActions.loginUser(); }},
			new MenuAction("Create account") { public void action() { AppActions.createUser(); }},
			new MenuAction("Queries") { public void action() { queriesMenu.execute(); }},
			new MenuAction("Reports") { public void action() { reportsMenu.execute(); }});
	
	public static Menu hostMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ HOST ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Logout") { public void action() { }},
			new MenuAction("Create a listing") { public void action() { AppActions.createListing(); }},
			new MenuAction("View your listings") { public void action() { AppActions.hostViewListings(); }},
			new MenuAction("Edit a Listing") { public void action() { AppActions.editListing(); }},
			new MenuAction("Review a Renter") { public void action() { AppActions.reviewRenter(); }},
			new MenuAction("Delete account") { public void action() { AppActions.deleteUser();}});
	
	public static Menu listingMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ Listing ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Return") { public void action() { }},
			new MenuAction("Add an availability") { public void action() { AppActions.addAvailability(); }},
			new MenuAction("Update price") { public void action() { AppActions.updatePrice(); }},
			new MenuAction("Remove an availability") { public void action() { AppActions.removeAvailability(); }},
			new MenuAction("Cancel a booking") { public void action() { AppActions.hostCancelBooking(); }},
			new MenuAction("Delete this Listing") { public void action() { AppActions.deleteListing();}});
	
	public static Menu renterMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ RENTER ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Logout") { public void action() { }},
			new MenuAction("Add Payment information") { public void action() { AppActions.addPaymentInfo(); }},
			new MenuAction("View listings") { public void action() { AppActions.viewAllListings(); }},
			new MenuAction("Book listing") { public void action() { AppActions.bookListing(); }},
			new MenuAction("View bookings") { public void action() { AppActions.viewBookings(); }},
			new MenuAction("Cancel a booking") { public void action() { AppActions.cancelBooking(); }},
			new MenuAction("Review a host") { public void action() { AppActions.reviewHost(); }},
			new MenuAction("Review a listing") { public void action() { AppActions.reviewListing(); }},
			new MenuAction("Delete account") { public void action() { AppActions.deleteUser();}});
	
	public static Menu queriesMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ QUERIES ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Back") { public void action() { }},
			new MenuAction("Search for by Latitude and Longitude") { public void action() { AppActions.searchByCoords(); }},
			new MenuAction("Search by Postal Code ") { public void action() { AppActions.searchByPostal(); }},
			new MenuAction("Search by Address ") { public void action() { AppActions.searchByAddress(); }}
			);
	
	public static Menu reportsMenu = new Menu("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ REPORTS ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯", 
			new MenuAction("Back") { public void action() { }},
			new MenuAction("Total number of bookings") { public void action() { AppActions.empty(); }},
			new MenuAction("Total number of Listings") { public void action() { AppActions.countListings(); }},
			new MenuAction("Rank Hosts") { public void action() { AppActions.empty(); }},
			new MenuAction("Find Commercial Hosts") { public void action() { AppActions.empty(); }},
			new MenuAction("Top renters with bookings") { public void action() { AppActions.empty(); }},
			new MenuAction("Top cancelling users") { public void action() { AppActions.reportTopCancels(); }},
			new MenuAction("Listing Word Cloud") { public void action() { AppActions.empty(); }});
	
	public static Menu listingTypeMenu = Menu.makeChoiceMenu("Choose your listing type:", Listing.types);
}
