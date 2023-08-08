package ui;

public abstract class MenuAction implements Action{

	String text;
	public MenuAction(String text) {
		this.text = text;
	}
}
