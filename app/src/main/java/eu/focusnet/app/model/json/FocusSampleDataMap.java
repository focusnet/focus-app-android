/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.json;

import java.util.ArrayList;
import java.util.HashMap;

import eu.focusnet.app.exception.FocusInternalErrorException;

public class FocusSampleDataMap extends HashMap<String, Object>
{

	/**
	 * Only accept: .... TODO doc simple implementation like hashmap, but check for focus-compatible types.
	 * <p/>
	 * FIXME TODO Custom exception.
	 * <p/>
	 * FIXME TODO : no URL for now .
	 *
	 * @param s The key identifying the entry in the HashMap
	 * @param o The object to insert in this HashMap
	 * @return The inserted object (o)
	 */
	@Override
	public Object put(String s, Object o)
	{

		if (!this.isValidScalarType(o)) {
			if (!(o instanceof ArrayList)) {
				// Fail
				throw new FocusInternalErrorException("Invalid type: " + o.getClass().getName());
			}
			else {
				// Because of type erasure, no knowledge of parameterized type at runtime.
				// So let's guess at runtime.
				Object first = null;
				try {
					first = ((ArrayList) o).get(0);
				}
				catch (NullPointerException e) {
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
		if (o instanceof String
				|| o instanceof Double
				|| o instanceof Integer) {
			//        || o instanceof URL) { // useful? or check at usage time?
			return true;
		}
		return false;
	}


}