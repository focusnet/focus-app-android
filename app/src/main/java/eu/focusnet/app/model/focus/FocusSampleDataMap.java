package eu.focusnet.app.model.focus;

import java.util.ArrayList;
import java.util.HashMap;

public class FocusSampleDataMap extends HashMap<String, Object>
{


	/**
	 * Only accept: .... TODO doc simple implementation like hashmap, but check for focus-compatible types.
	 *
	 * FIXME TODO Custom exception.
	 *
	 * FIXME TODO : no URL for now .
	 *
	 * @param s The key identifying the entry in the HashMap
	 * @param o The object to insert in this HashMap
	 * @return The inserted object (o)
	 */
	@Override
	public Object put(String s, Object o) {

		if (!this.isValidScalarType(o)) {
			if (!(o instanceof ArrayList)) {
				// Fail
				throw new RuntimeException("Invalid type: " + o.getClass().getName());
			}
			else {
				// Because of type erasure, no knowledge of parameterized type at runtime.
				// So let's guess at runtime.
				Object first = null;
				try {
					first = ((ArrayList) o).get(0);
				}
				catch(NullPointerException e) { }

				if (first == null) {
					// Empty array, let's force it to contain Strings (no real importance as it is empty)
					o = new ArrayList<String>();
				}
				else {
					if (!this.isValidScalarType(first)) {
						throw new RuntimeException("Invalid type: ArrayList<" + first.getClass().getName() + ">");
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