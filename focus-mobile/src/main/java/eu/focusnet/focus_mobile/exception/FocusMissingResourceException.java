/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.focus_mobile.exception;

/**
 * A FocusMissingResourceException is triggered when a resource cannot be retrieved when it should.
 * <p/>
 * This is a checked exception.
 */
public class FocusMissingResourceException extends Exception
{
	/**
	 * Exception constructor
	 *
	 * @param detailMessage Message summarizing the encountered issue
	 */
	public FocusMissingResourceException(String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * Constructor with more details on the missing resource
	 *
	 * @param detailMessage    Message summarizing the encountered issue
	 * @param missingRessource A String describing the missing resource
	 */
	public FocusMissingResourceException(String detailMessage, FocusInternalErrorException missingRessource)
	{
		super(detailMessage + "; MISSING RESSOURCE: |" + missingRessource + "|");
	}
}
