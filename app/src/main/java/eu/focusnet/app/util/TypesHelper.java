package eu.focusnet.app.util;

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
		for (Object o2 : (ArrayList)o) {
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
		return (ArrayList<String>)o;
	}


}
