package eu.focusnet.app.model.focus;

public class User extends FocusObject
{

	private Long id;
	private String firstName,
			lastName,
			email,
			company;

	public User()
	{
	}

	public User(Long id, String firstname, String lastname, String email,
				String company)
	{
		this.email = email;
		this.company = company;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}


}
