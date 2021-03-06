/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.util;

/**
 * An FocusInternalErrorException denotes a problem that should not happen if the logic of the
 * application was correct.
 * <p/>
 * This is an unchecked exception that will generally crash the application.
 */
public class FocusInternalErrorException extends RuntimeException
{
	/**
	 * Exception constructor
	 *
	 * @param detailMessage Message summarizing the encountered issue
	 */
	public FocusInternalErrorException(String detailMessage)
	{
		super(detailMessage);
	}

	/**
	 * Exception constructor to be used when we catch another exception and want to keep
	 * its detailed information for further propagation.
	 * <p/>
	 * FIXME probably bad practice. We should keep the original exception.
	 *
	 * @param ex Inherited exception
	 */
	public FocusInternalErrorException(Exception ex)
	{
		super(ex);
	}
}