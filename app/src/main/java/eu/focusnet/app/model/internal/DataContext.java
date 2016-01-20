package eu.focusnet.app.model.internal;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.focus.FocusSample;
import eu.focusnet.app.util.TypesHelper;

/**
 * Created by julien on 13.01.16.
 *
 * TODO doc:
 * we always store FocusSamples, not actual scalar/leaf data
 * -> put() will retrieve a sample only
 * -> get() retrieves samples
 * -> resolve() used to get leaf-data (i.e. content of FocuSSample)
 *
 * iterators require a list of urls -> get a specific FocusSample -> resolve() it -> case to list of urls.
 *
 */
public class DataContext extends HashMap<String, FocusSample>
{

	private DataManager dataManager;

	/**
	 * Default c'tor
	 */
	public DataContext()
	{
		this.dataManager = DataManager.getInstance();
	}

	/**
	 * Construct a new DataContext from another one.
	 * FIXME TODO check that the values are kept? shold they by string or FocusSamples?
	 *
	 * @param c
	 */
	public DataContext(DataContext c)
	{
		super(c);
		this.dataManager = DataManager.getInstance();
	}

	/**
	 *
	 *
	 * Add a new FocusSample entry in our current data context, based on a description of the
	 * new data to be included. These descriptions are the ones we can find in the 'data'
	 * properties of the Application Content template JSON representation.
	 * <p>
	 * All data context entries are FocusSample's
	 * <p>
	 * The different options for the description are the following:
	 * <p>
	 * - a simple URL, e.g. http://data.example.org/test/123
	 * - a reference to an existing entry in this data context, with a selector to the
	 * appropriate key in its data object; e.g. <ref|machine-ABC|data.woodpile-url>
	 * - a history request, e.g. <history|URL|since=now-86400;until=now;every=240>
	 * - a lookup request, e.g. <lookup:http://schemas.focusnet.eu/my-special-type|URL>
	 * where URL is the "context" of the FocusObject.
	 *
	 * FIXME TODO document exactly what can be accepted here.
	 *
	 * <p>
	 * In the last three possibilities, the URL can be interpolated by a "ref"
	 * (but in this case the separator is ":" and not "|"
	 *
	 * @param key
	 * @param description
	 */
	public FocusSample put(String key, String description)
	{
		// what kind of data do we have?
		FocusSample f = null;

		if (description.startsWith("<") && description.endsWith(">")) {

			description = description.replace("<", "").replace(">", "");

			if (description.startsWith("ctx/")) {
				String u = this.resolveReferencedUrl(description);
				f = this.dataManager.getSample(u);
			}
			else {
				String[] parts = description.split("\\|");
				if (parts.length != 3) {
					throw new RuntimeException("Wrong number of fields for description of data context.");
				}
				if (parts[0].equals("history")) {
					String u = null;
					if (parts[1].startsWith("ctx/")) {
						u = this.resolveReferencedUrl(parts[1]);
					}
					else {
						u = parts[1];
					}
					f = this.dataManager.getHistorySample(u, parts[2]);
				}
				else if (parts[0].equals("lookup")) {
					String u = null;
					if (parts[1].startsWith("ctx/")) {
						u = this.resolveReferencedUrl(parts[1]);
					}
					else {
						u = parts[1];
					}
					f = this.dataManager.getLookupSample(u, parts[2]);
				}
				else {
					throw new RuntimeException("This data reference is not supported.");
				}

			}
		}
		else {
			// simple URL
			f = this.dataManager.getSample(description);
		}

		if (f == null) {
			return null; // for now FIXME DEBUG
//			throw new RuntimeException("No FocusSample obtained");
		}
		return super.put(key, f);
	}

	/**
	 * We know that we have a context reference (starting with "ctx/")
	 * and we want to return the corresponding URL.
	 *
	 * @param description
	 * @return
	 */
	private String resolveReferencedUrl(String description)
	{
		String[] parts = description.split("/");
		if (parts.length != 3) {
			throw new RuntimeException("badly formatted reference");
		}

		String ref = parts[1];
		if (this.get(ref) == null) {
			throw new RuntimeException("no data reference found");
		}
		String res = this.get(ref).getString(parts[2]);
		if (res == null) {
			throw new RuntimeException("Cannot find this field.");
		}
		// if that's indeed a url, then we can retrieve it.
		return res;
	}


	/**
	 * Fill data in this context based on the provided data input definition.
	 *
	 * @param data
	 */
	public void provideData(HashMap<String, String> data)
	{
		if (data == null) {
			return;
		}
		for (Map.Entry<String, String> entry : data.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Resolve the provided request considering the present data context. e.g.
	 * <ctx/simple-example/field-to-get> -> will retrieve the field-to-get field of the
	 * data being stored under the simple-example entry.
	 *
	 * If the request format is not recognized, return it as-is.
	 *
	 * If the request does not succeed, return FIXME exception or null?
	 *
	 * @return
	 */
	public Object resolve(String request)
	{
		if (!request.startsWith("<ctx/") || !request.endsWith(">")) {
			return request;
		}
		request = request.replace("<", "").replace(">", "");

		String[] parts = request.split("/");
		if (parts.length < 2) {
			return null;
		}

		FocusSample fs = this.get(parts[1]);
		if (parts.length > 2) {
			if (fs == null) {
				return null;
			}
			return fs.get(parts[2]);
		}
		return fs;
	}
}