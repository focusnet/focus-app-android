package eu.focusnet.app.exception;

/**
 * Exception trigerred when a resource cannot be retrieved when it should.
 *
 */
public class FocusMissingResourceException extends Exception
{
	public FocusMissingResourceException(String detailMessage)
	{
		super(detailMessage);
	}
}
