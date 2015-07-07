package eu.focusnet.app.model.data;

import java.util.Map;

import eu.focusnet.app.common.FocusType;

public class FocusSample extends FocusObject {

	private Map<String, FocusType> data;

	public FocusSample() {}

	public FocusSample(Map<String, FocusType> data) {
		this.data = data;
	}

	public Map<String, FocusType> getData() {
		return data;
	}

	public void setData(Map<String, FocusType> data) {
		this.data = data;
	}
}
