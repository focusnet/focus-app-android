package eu.focusnet.app.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private final DateFormat dateFormat;

    public DateTypeAdapter() {
      dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override 
    public synchronized JsonElement serialize(Date date, Type type,
        JsonSerializationContext jsonSerializationContext) {
      return new JsonPrimitive(dateFormat.format(date));
    }

    @Override 
    public synchronized Date deserialize(JsonElement jsonElement, Type type,
        JsonDeserializationContext jsonDeserializationContext) {
      try {
        return dateFormat.parse(jsonElement.getAsString());
      } catch (ParseException e) {
        throw new JsonParseException(e);
      }
    }
  }