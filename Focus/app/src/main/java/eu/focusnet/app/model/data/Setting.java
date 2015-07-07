package eu.focusnet.app.model.data;

public class Setting {
	
	private String language;

	public Setting() {}
	
	public Setting(String language, Notification notification) {

		this.language = language;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
}
