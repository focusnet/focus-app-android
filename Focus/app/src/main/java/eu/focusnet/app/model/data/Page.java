package eu.focusnet.app.model.data;

public class Page {

	private String name,
				   path;
	
	private int order;
	
	public Page() {}

	public Page(String name, String path, int order) {
		this.name = name;
		this.path = path;
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	
}
