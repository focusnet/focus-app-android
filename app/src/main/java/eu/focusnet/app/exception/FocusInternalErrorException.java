package eu.focusnet.app.exception;

/**
 * An FocusInternalErrorException denotes a problem that should not happen if the logic of the
 * application was correct. This is an additional sanity check.
 */
public class FocusInternalErrorException extends RuntimeException
{
	public FocusInternalErrorException(String detailMessage)
	{
		super(detailMessage);
	}
}