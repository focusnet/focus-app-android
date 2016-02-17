package eu.focusnet.app.exception;

/**
 * NotImplementedException is thrown when some code branch that has not been implemented is reached
 */
public class NotImplementedException extends RuntimeException
{
	public NotImplementedException(String detailMessage)
	{
		super(detailMessage);
	}
}
