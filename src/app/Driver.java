package app;
import java.sql.SQLException;
import java.util.Scanner;
import database.Database;
import ui.AppMenus;

public class Driver {
	
	public static Database db = initDatabase();
	public static void main(String[] args) {
		
		
		if (db == null) { 
			System.err.println("There was an error initiating the database connection");
			System.exit(1);
		}
		
		AppMenus.startMenu.execute();
	}
	
	static Database initDatabase() {
		Database db = null;
		try {
			db = new Database();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.err.println("There was an error initiating the database connection");
			System.exit(1);
		}
		return db;
	}
}
