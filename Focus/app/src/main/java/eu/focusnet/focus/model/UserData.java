package eu.focusnet.focus.model;

public class UserData {

	private String firstname,
				   lastname,
				   email,
				   company;

	public UserData() {}
	
	public UserData(String firstname, String lastname, String email,
			String company) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.company = company;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	
}
