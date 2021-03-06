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

package eu.focusnet.app.model.gson;

import java.util.ArrayList;
import java.util.HashMap;

import eu.focusnet.app.util.FocusInternalErrorException;

/**
 * Specialized {@code HashMap} for storing {@link FocusSample} data.
 */
public class FocusSampleDataMap extends HashMap<String, Object>
{

	/**
	 * Put a new key-value pair in the {@code Map}.
	 *
	 * @param s The key identifying the entry in the {@code Map}
	 * @param o The object to insert in this {@code Map}
	 * @return The inserted object (o)
	 */
	@Override
	public Object put(String s, Object o)
	{
		// it may happen that JSON objects contains null, so let's discard them
		// FIXME what happens if later accessed in array context?
		if (o == null) {
			return null;
		}
		if (!this.isValidScalarType(o)) {
			if (!(o instanceof ArrayList)) {
				// Fail
				throw new FocusInternalErrorException("Invalid type: " + o.getClass().getName());
			}
			else {
				// Because of type erasure, no knowledge of parametrized type at runtime.
				// So let's guess at runtime.
				Object first = null;

				// it may happen that we try to access an object that was not added,
				// e.g. because it was null at JSON import.
				if (!((ArrayList) o).isEmpty()) {
					try {
						first = ((ArrayList) o).get(0);
					}
					catch (NullPointerException e) {
						// ignore, first is still null
					}
				}

				if (first == null) {
					// Empty array, let's force it to contain Strings (no real importance as it is empty)
					o = new ArrayList<String>();
				}
				else {
					if (!this.isValidScalarType(first)) {
						throw new FocusInternalErrorException("Invalid type: ArrayList<" + first.getClass().getName() + ">");
					}
					// and the rest of the array should also be of this type. TODO FIXME useful? or too paranoid?
				}
			}
		}
		return super.put(s, o);
	}

	/**
	 * Tells whether the provided Object is of a type that we consider safe for scalar values.
	 *
	 * @param o The Object to inspect
	 * @return true if the Object is of acceptable type, false otherwise
	 */
	private boolean isValidScalarType(Object o)
	{
		return o instanceof String
				|| o instanceof Double
				|| o instanceof Integer;
	}


}