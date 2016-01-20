package eu.focusnet.app.util;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import eu.focusnet.app.exception.BadTypeException;

/**
 * Created by julien on 18.01.16.
 */
public class TypesHelper
{
	public enum TargetType
	{
		STRING,
		DOUBLE,
		INTEGER,
		ARRAY_OF_STRINGS,
		ARRAY_OF_DOUBLES,
		ARRAY_OF_INTEGERS,
		ARRAY_OF_URLS
	}

	/**
	 * double, int, float, string, url
	 *
	 * @param obj
	 * @return
	 * @throws BadTypeException
	 */
	public static String asString(Object obj) throws BadTypeException
	{
		if (obj == null) {
			return "";
		}
		if (obj instanceof ArrayList) {
			throw new BadTypeException("Array cannot be converted to string.");
		}
		if (obj instanceof String || obj instanceof Double || obj instanceof Integer) {
			return obj.toString();
		}
		throw new BadTypeException("Unsupported type");
	}

	public static Double asDouble(Object obj) throws BadTypeException
	{
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Integer) {
			return (Double) obj;
		}
		if (obj instanceof String) {
			try {
				return Double.parseDouble((String)obj);
			}
			catch (NumberFormatException e) {
				throw new BadTypeException("not a valid double string");
			}
		}
		throw new BadTypeException("Not a Double");
	}

	public static Integer asInteger(Object obj) throws BadTypeException
	{
		return null;
	}

	public static ArrayList<String> asArrayOfStrings(Object obj) throws BadTypeException
	{
		// obj should be an array list,
		// each element should be a scalar -> toSTring() it.
		// if any error, ClassCastException | BadTypeException
		if (!(obj instanceof ArrayList)) {
			throw new BadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<String> ret = new ArrayList<String>();
		for(Object o2 : (ArrayList)obj) {
			if (o2 instanceof String || o2 instanceof Double || o2 instanceof Integer) {
				ret.add(o2.toString());
			}
			else {
				throw new BadTypeException("Invalid value in array");
			}
		}
		return ret;
	}

	public static ArrayList<Integer> asArrayOfIntegers(Object obj) throws BadTypeException
	{
		return null;
	}

	public static ArrayList<Double> asArrayOfDoubles(Object obj) throws BadTypeException
	{
		return null;
	}


	/**
	 * Make sure that the provided object in an ArrayList of URLs (as Strings), or throw an exception.
	 * <p/>
	 * We are a bit more paranoiac with this data types because it is used to follow references
	 * and in iterators, so we must be sure it contains only valid data.
	 *
	 * @param o
	 * @return
	 * @throws BadTypeException
	 */
	public static ArrayList<String> asArrayOfUrls(Object o) throws BadTypeException
	{
		if (!(o instanceof ArrayList)) {
			throw new BadTypeException("Not an ArrayList");
		}
		for (Object o2 : (ArrayList) o) {
			if (!(o2 instanceof String)) {
				throw new BadTypeException("Entry of ArrayList is not a String.");
			}
			try {
				new URL((String) o2);
			}
			catch (MalformedURLException e) {
				throw new BadTypeException("Entry of ArrayList is not a URL.");
			}
		}
		return (ArrayList<String>) o;
	}


}
