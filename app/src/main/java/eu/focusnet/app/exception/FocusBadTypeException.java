package eu.focusnet.app.exception;

/**
 * Created by julien on 18.01.16.
 */
public class FocusBadTypeException extends RuntimeException
{
	public FocusBadTypeException(String detailMessage)
	{
		super(detailMessage);
	}
}
