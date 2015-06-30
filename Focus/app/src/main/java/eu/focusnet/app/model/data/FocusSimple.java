package eu.focusnet.app.model.data;

import java.util.Map;

public class FocusSimple {

	private Map<String, Object> my_custom_property;
	
	public FocusSimple() {}
	
	public FocusSimple(Map<String, Object> my_custom_property) {
		this.my_custom_property = my_custom_property;
	}

	public Map<String, Object> getMy_custom_property() {
		return my_custom_property;
	}
	
	public void setMy_custom_property(Map<String, Object> my_custom_property) {
		this.my_custom_property = my_custom_property;
	}
}
