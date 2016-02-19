package eu.focusnet.app.model.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import eu.focusnet.app.exception.FocusBadTypeException;

/**
 * This class provides static methods for handling types and reporting inconsistencies
 */
public class TypesHelper
{

	public static String asString(Object obj) throws FocusBadTypeException
	{
		if (obj == null) {
			return "";
		}
		if (obj instanceof ArrayList) {
			throw new FocusBadTypeException("Array cannot be converted to string.");
		}
		if (obj instanceof String || obj instanceof Double || obj instanceof Integer) {
			return obj.toString();
		}
		throw new FocusBadTypeException("Unsupported type");
	}

	public static Double asDouble(Object obj) throws FocusBadTypeException
	{
		if (obj instanceof Double) {
			return (Double) obj;
		}
		if (obj instanceof Integer) {
			return (Double) obj;
		}
		if (obj instanceof String) {
			try {
				return Double.parseDouble((String) obj);
			}
			catch (NumberFormatException e) {
				throw new FocusBadTypeException("not a valid double string");
			}
		}
		throw new FocusBadTypeException("Not a Double");
	}

	public static Integer asInteger(Object obj) throws FocusBadTypeException
	{
		return null;
	}

	public static ArrayList<String> asArrayOfStrings(Object obj) throws FocusBadTypeException
	{
		// obj should be an array list,
		// each element should be a scalar -> toSTring() it.
		// if any error, ClassCastException | FocusBadTypeException
		if (!(obj instanceof ArrayList)) {
			throw new FocusBadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<String> ret = new ArrayList<String>();
		for (Object o2 : (ArrayList) obj) {
			if (o2 instanceof String || o2 instanceof Double || o2 instanceof Integer) {
				ret.add(o2.toString());
			}
			else {
				throw new FocusBadTypeException("Invalid value in array");
			}
		}
		return ret;
	}

	public static ArrayList<Integer> asArrayOfIntegers(Object obj) throws FocusBadTypeException
	{
		if (!(obj instanceof ArrayList)) {
			throw new FocusBadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (Object o2 : (ArrayList) obj) {
			if (o2 instanceof Integer) {
				ret.add((Integer) o2);
			}
			else if (o2 instanceof String) {
				ret.add(Integer.parseInt((String) o2));
			}
			else {
				throw new FocusBadTypeException("Invalid value in array");
			}
		}
		return ret;
	}

	public static ArrayList<Double> asArrayOfDoubles(Object obj) throws FocusBadTypeException
	{
		if (!(obj instanceof ArrayList)) {
			throw new FocusBadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<Double> ret = new ArrayList<Double>();
		for (Object o2 : (ArrayList) obj) {
			if (o2 instanceof Double || o2 instanceof Integer) {
				ret.add((Double) o2);
			}
			else if (o2 instanceof String) {
				ret.add(Double.parseDouble((String) o2));
			}
			else {
				throw new FocusBadTypeException("Invalid value in array");
			}
		}
		return ret;
	}

	/**
	 * Make sure that the provided object in an ArrayList of URLs (as Strings), or throw an exception.
	 * <p/>
	 * We are a bit more paranoiac with this data types because it is used to follow references
	 * and in iterators, so we must be sure it contains only valid data.
	 *
	 * @param o
	 * @return
	 * @throws FocusBadTypeException
	 */
	public static ArrayList<String> asArrayOfUrls(Object o) throws FocusBadTypeException
	{
		if (!(o instanceof ArrayList)) {
			throw new FocusBadTypeException("Not an ArrayList");
		}
		for (Object o2 : (ArrayList) o) {
			if (!(o2 instanceof String)) {
				throw new FocusBadTypeException("Entry of ArrayList is not a String.");
			}
			try {
				new URL((String) o2);
			}
			catch (MalformedURLException e) {
				throw new FocusBadTypeException("Entry of ArrayList is not a URL.");
			}
		}
		return (ArrayList<String>) o;
	}


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


}
