package eu.focusnet.app.model.json;

public class RetentionPolicy
{
	//eventual use an enum for this
	private String policy;

	public RetentionPolicy()
	{
	}

	public RetentionPolicy(String policy)
	{
		this.policy = policy;
	}

	public String getPolicy()
	{
		return policy;
	}

	public void setPolicy(String policy)
	{
		this.policy = policy;
	}


}
