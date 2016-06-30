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

package eu.focusnet.app.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import eu.focusnet.app.util.FocusBadTypeException;

/**
 * This class provides static methods for handling types and reporting inconsistencies
 */
public class TypesHelper
{

	/**
	 * Acquire object as string
	 *
	 * @param obj The evaluated object
	 * @return A Stringified version of the object
	 * @throws FocusBadTypeException If the input object is not compatible with our application
	 *                               casting capabilities.
	 */
	public static String asString(Object obj) throws FocusBadTypeException
	{
		if (obj == null) {
			return "";
		}
		if (obj instanceof String || obj instanceof Double || obj instanceof Integer) {
			String x = obj.toString();
			if (obj instanceof Double && (((Double) obj) == Math.floor(((Double) obj))) && !Double.isInfinite(((Double) obj))) {
				x = x.replaceFirst("\\.\\d+$", "");
			}
			return x;
		}
		throw new FocusBadTypeException("Unsupported type");
	}

	/**
	 * Acquire object as a Double
	 *
	 * @param obj The evaluated object
	 * @return A Double version of the object
	 * @throws FocusBadTypeException If the input object is not compatible with our application
	 *                               casting capabilities.
	 */
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


	/**
	 * Acquire object as an array of Strings
	 *
	 * @param obj The evaluated object
	 * @return An array of Strings version of the object
	 * @throws FocusBadTypeException If the input object is not compatible with our application
	 *                               casting capabilities.
	 */
	public static ArrayList<String> asArrayOfStrings(Object obj) throws FocusBadTypeException
	{
		// obj should be an array list,
		// each element should be a scalar -> toSTring() it.
		// if any error, ClassCastException | FocusBadTypeException
		if (!(obj instanceof ArrayList)) {
			throw new FocusBadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<String> ret = new ArrayList<>();
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

	/**
	 * Acquire object as an array of Doubles
	 *
	 * @param obj The evaluated object
	 * @return An array of Double version of the object
	 * @throws FocusBadTypeException If the input object is not compatible with our application
	 *                               casting capabilities.
	 */
	public static ArrayList<Double> asArrayOfDoubles(Object obj) throws FocusBadTypeException
	{
		if (!(obj instanceof ArrayList)) {
			throw new FocusBadTypeException("Provided values are not an ArrayList");
		}
		ArrayList<Double> ret = new ArrayList<>();
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
	 * Acquire object as an array of valid Urls
	 *
	 * @param o The evaluated object
	 * @return An array of Urls version of the object
	 * @throws FocusBadTypeException If the input object is not compatible with our application
	 *                               casting capabilities.
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


}
