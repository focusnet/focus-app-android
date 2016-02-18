package eu.focusnet.app.exception;

/**
 * FocusNotImplementedException is thrown when some code branch that has not been implemented is reached
 */
public class FocusNotImplementedException extends RuntimeException
{
	public FocusNotImplementedException(String detailMessage)
	{
		super(detailMessage);
	}
}
