package eu.focusnet.app.model.focus;

/**
 * A FocusSample is a FocusObject containing a specialized HashMap (FocusSampleDataMap)
 */
public class FocusSample extends FocusObject
{
	FocusSampleDataMap data = null;
	// more complex deserializers like URL or BigInteger cannot work because this object Object is too generic.

	/**
	 * C'tor
	 */
	public FocusSample()
	{
		this.data = new FocusSampleDataMap();
	}

	/**
	 * Add a key-value pair to the data HashMap
	 *
	 * @param s Key
	 * @param o Value
	 */
	public void add(String s, Object o)
	{
		this.data.put(s, o);
	}

	/**
	 * Generic get() method for the content of the data map
	 *
	 * @param identifier
	 * @return
	 */
	public Object get(String identifier)
	{
		return this.data.get(identifier);
	}


	/*
	FIXME TODO
    public Object get(key) { return data.get(key); }

    getInteger() , getString(), getDouble, getArrayDouble() ... ?
     */

	public String getString(String key)
	{
		return this.data.get(key).toString();
	}

}
