package eu.focusnet.app.exception;

/**
 * An InternalErrorException denotes a problem that should not happen if the logic of the
 * application was correct. This is an additional sanity check.
 */
public class InternalErrorException extends RuntimeException
{
	public InternalErrorException(String detailMessage)
	{
		super(detailMessage);
	}
}