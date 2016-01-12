package eu.focusnet.app.model.focus;

/**
 * A FocusSample is a FocusObject containing a specialized HashMap (FocusSampleDataMap)
 *
 */
public class FocusSample extends FocusObject
{
    FocusSampleDataMap data;
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

    /*
    FIXME TODO
    public Object get(key) { return data.get(key); }

    getInteger() , getString(), getDouble, getArrayDouble() ... ?
     */
}
