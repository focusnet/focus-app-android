package eu.focusnet.app.model.data;

import java.util.Date;

public class UserPreference {
	
	private String schemaUrl,
			   schemaVersion,
			   	  resourceId,
			   	 resourceUrl;
	
	private int revision;
	
	private Date creationDateTime;
	private boolean active;
	private RetentionPolicy retentionPolicy;
	
	private UserData userData;
	
	private Setting setting;
	
	private Bookmark bookmark;

	public UserPreference() {}

	public UserPreference(String schemaUrl, String schemaVersion,
			String resourceId, String resourceUrl, int revision,
			Date creationDateTime, boolean active,
			RetentionPolicy retentionPolicy, UserData userData,
			Setting setting, Bookmark bookmark) {
		
		this.schemaUrl = schemaUrl;
		this.schemaVersion = schemaVersion;
		this.resourceId = resourceId;
		this.resourceUrl = resourceUrl;
		this.revision = revision;
		this.creationDateTime = creationDateTime;
		this.active = active;
		this.retentionPolicy = retentionPolicy;
		this.userData = userData;
		this.setting = setting;
		this.bookmark = bookmark;
	}

	public String getSchemaUrl() {
		return schemaUrl;
	}

	public void setSchemaUrl(String schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public String getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public Date getCreationDateTime() {
		return creationDateTime;
	}

	public void setCreationDateTime(Date creationDateTime) {
		this.creationDateTime = creationDateTime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public RetentionPolicy getRetentionPolicy() {
		return retentionPolicy;
	}

	public void setRetentionPolicy(RetentionPolicy retentionPolicy) {
		this.retentionPolicy = retentionPolicy;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}
	
}
