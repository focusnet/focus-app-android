package eu.focusnet.app.exception;

import eu.focusnet.app.FocusApplication;

/**
 * This Exception is triggered when an unexpected type is encountered.
 *
 * This is a checked exception, so we will try to survive its occurrence as much as possible, and if that is not possible, we will throw an
 * unchecked exception (i.e. FocusInternalErrorException)
 */
public class FocusBadTypeException extends Exception
{
	public FocusBadTypeException(String detailMessage)
	{
		super(detailMessage);
		FocusApplication.reportError(this);
	}
}
