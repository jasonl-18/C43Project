package ui;

import java.util.LinkedList;
import java.util.List;

import helpers.InputHandler;

public class Menu {
	
	String title;
	int opBound;
	MenuAction[] actions;
	int exitCode = 0;
	public static boolean earlyExit = false;
	
	public Menu(String title, MenuAction... actions) {
		this.title = title;
		this.opBound = actions.length;
		this.actions = actions;
	}
	public static Menu makeChoiceMenu(String title, String...options) {
		List<MenuAction> actions = new LinkedList<>();
		actions.add(new MenuAction("Cancel") {public void action() {};});
		for(String str : options) {
			actions.add(new MenuAction(str) {public void action() {};});
		}
		
		return new Menu(title, actions.toArray(new MenuAction[0]));
	}
	public Menu(String title, int exitCode, MenuAction...actions) {
		this(title, actions);
		this.exitCode = exitCode;
	}
	
	private void printPrompt() {
		System.out.printf("Please enter a selection [0-%d]: ", opBound-1);
	}
	private void display() {
		System.out.println(title);
		for (int i = 0; i < opBound; i++) {
			System.out.printf("%d) %s\n", i,actions[i].text);
		}
		printPrompt();
	}
	
	private int inputValidator() {
		int out = 0;
		boolean invalid;
		do {
			invalid = false;
			try {
				out = Integer.parseInt(InputHandler.readLine());
				invalid = out < 0 || out >= opBound;
			}
			catch (NumberFormatException e){
				invalid = true;
			}
			if (invalid ) { this.printError(); }
		} while (invalid);
		return out;
	}
	private void printError() {
		System.out.print("Invalid Input - ");
		printPrompt();
	}
	
	public void execute() {
		int selection = -1;
		while (selection != exitCode) {
			display();
			selection = inputValidator();
			actions[selection].action();
			
			if(earlyExit) {
				earlyExit = false;
				break;
			}
		}
	}
}
