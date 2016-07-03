package eu.focusnet.app.model;/*
 * The MIT License (MIT)
 *
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
 *
 */

/**
 * An interface for instances that can be compared and therefore sorted in
 * the navigation listings
 */
public interface IterableInstance
{
	/**
	 * Get the Path. When sorting, we do not reorder elements if the path does not reflect the same
	 * iterator for the 2 evaluated elements.
	 * <p/>
	 * See {@link AbstractInstance#getComparator()}
	 *
	 * @return The path of the current instance
	 */
	String getPath();

	/**
	 * Get the title
	 * <p/>
	 * See {@link AbstractInstance#getComparator()}
	 *
	 * @return The title
	 */
	String getTitle();


}
