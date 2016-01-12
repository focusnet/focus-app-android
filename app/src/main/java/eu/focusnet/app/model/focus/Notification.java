package eu.focusnet.app.model.focus;

public class Notification {

	private boolean vibrate,
					ring;
	
	public Notification() {}

	public Notification(boolean vibrate, boolean ring) {
		this.vibrate = vibrate;
		this.ring = ring;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	public boolean isRing() {
		return ring;
	}

	public void setRing(boolean ring) {
		this.ring = ring;
	}
	
	
}
