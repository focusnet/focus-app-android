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

package eu.focusnet.app.model.internal;

/**
 * Created by julien on 2/19/16.
 */
abstract public class AbstractInstance
{
	protected DataContext dataContext;
	private boolean valid;

	public AbstractInstance()
	{
		this.valid = true;
	}

	public DataContext getDataContext()
	{
		return this.dataContext;
	}


	/**
	 * Mark this widget as invalid
	 */
	protected void markAsInvalid()
	{
		this.valid = false;
	}

	protected void markAsInvalid(String why)
	{
		this.markAsInvalid();
	}

	/**
	 * Tells whether this widget instance is valid
	 */
	public boolean isValid()
	{
		return this.valid;
	}


	public void freeDataContext()
	{
		this.dataContext = null;
	}
}
