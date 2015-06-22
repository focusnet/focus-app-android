package eu.focusnet.focus.model;

public class Setting {
	
	private String language;
	Notification notification;
	
	public Setting() {}
	
	public Setting(String language, Notification notification) {

		this.language = language;
		this.notification = notification;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Notification getNotification() {
		return notification;
	}
	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
}
