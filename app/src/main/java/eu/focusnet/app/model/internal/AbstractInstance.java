package eu.focusnet.app.model.internal;

/**
 * Created by julien on 2/19/16.
 */
abstract public class AbstractInstance
{
	protected DataContext dataContext;
	protected boolean isValid;

	public DataContext getDataContext()
	{
		return this.dataContext;
	}

}
