package helpers;

import java.util.Scanner;

public class InputHandler {

	public static final Scanner sc = new Scanner(System.in);
	public static String readLine() {
		return sc.nextLine();
	}
	public static void waitEnter() {
		System.out.println("Press enter to continue: ");
		sc.nextLine();
	}
}
