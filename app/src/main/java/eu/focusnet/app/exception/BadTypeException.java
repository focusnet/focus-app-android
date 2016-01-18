package eu.focusnet.app.exception;

/**
 * Created by julien on 18.01.16.
 */
public class BadTypeException extends RuntimeException
{
	public BadTypeException(String detailMessage)
	{
		super(detailMessage);
	}
}
