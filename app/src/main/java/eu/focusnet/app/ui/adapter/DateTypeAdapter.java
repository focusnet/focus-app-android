/**
 *
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
 *
 */

package eu.focusnet.app.ui.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateTypeAdapter is used by the GSON parser to translate DateTime formats
 */
public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date>
{
	private final DateFormat dateFormat;

	/**
	 * C'tor
	 */
	public DateTypeAdapter()
	{
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * Serialization
	 *
	 * @param date
	 * @param type
	 * @param jsonSerializationContext
	 * @return
	 */
	@Override
	public synchronized JsonElement serialize(Date date, Type type,
											  JsonSerializationContext jsonSerializationContext)
	{
		return new JsonPrimitive(dateFormat.format(date));
	}

	/**
	 * Deserialization
	 *
	 * @param jsonElement
	 * @param type
	 * @param jsonDeserializationContext
	 * @return
	 */
	@Override
	public synchronized Date deserialize(JsonElement jsonElement, Type type,
										 JsonDeserializationContext jsonDeserializationContext)
	{
		try {
			return dateFormat.parse(jsonElement.getAsString());
		}
		catch (ParseException e) {
			throw new JsonParseException(e);
		}
	}
}