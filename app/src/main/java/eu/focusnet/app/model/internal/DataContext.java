package eu.focusnet.app.model.internal;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.service.DataManager;

/**
 * Created by julien on 13.01.16.
 * <p/>
 * TODO doc:
 * we always store FocusSamples, not actual scalar/leaf data
 * -> put() will retrieve a sample only
 * -> get() retrieves samples
 * -> resolve() used to get leaf-data (i.e. content of FocuSSample)
 * <p/>
 * iterators require a list of urls -> get a specific FocusSample -> resolve() it -> case to list of urls.
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
	 * Add a new FocusSample entry in our current data context, based on a description of the
	 * new data to be included. These descriptions are the ones we can find in the 'data'
	 * properties of the Application Content template JSON representation.
	 * <p/>
	 * All data context entries are FocusSample's
	 * <p/>
	 * The different options for the description are the following:
	 * <p/>
	 * - a simple URL, e.g. http://data.example.org/test/123
	 * - a reference to an existing entry in this data context, with a selector to the
	 * appropriate key in its data object; e.g. <ctx|machine-ABC|woodpile-url>
	 * - a history request, e.g. <history|URL|since=now-86400;until=now;every=240>
	 * - a lookup request, e.g. <lookup:http://schemas.focusnet.eu/my-special-type|URL>
	 * where URL is the "context" of the FocusObject.
	 * <p/>
	 * In the last 2 possibilities, the URL can also be replaced by a reference from an existing
	 * context. In this case, the separator "|" is replaced by "/" within this context description.
	 * <p/>
	 * Examples:
	 * "simple-url": "http://focus.yatt.ch/debug/focus-sample-1.json",
	 * "referenced-url": "<ctx/simple-url/url1>",
	 * "history-test": "<history|http://focus.yatt.ch/debug/focus-sample-1.json|params>",
	 * "history-test-ref": "<history|ctx/simple-url/url1|params,params2>",
	 * "lookup-test": "<lookup|http://focus.yatt.ch/debug/focus-sample-1.json|http://www.type.com>",
	 * "lookup-test-ref": "<lookup|ctx/simple-url/url1|http://www.type.com>"
	 */
	public FocusSample put(String key, String description)
	{
		// what kind of data do we have?
		FocusSample f;

		if (description.startsWith(Constant.SELECTOR_SERVICE_OPEN)
				&& description.endsWith(Constant.SELECTOR_SERVICE_CLOSE)) {

			description = description
					.replace(Constant.SELECTOR_SERVICE_OPEN, "")
					.replace(Constant.SELECTOR_SERVICE_CLOSE, "");

			if (description.startsWith(Constant.SELECTOR_SERVICE_CTX + Constant.SELECTOR_SERVICE_SEPARATOR_INNER)) {
				String u = this.resolveReferencedUrl(description);
				try {
					f = this.dataManager.getSample(u);
				}
				catch (FocusMissingResourceException ex) {
					return null;
				}
			}
			else {
				String[] parts = description.split(Constant.SELECTOR_SERVICE_SEPARATOR_OUTER_PATTERN);
				if (parts.length != 3) {
					throw new FocusInternalErrorException("Wrong number of fields for description of data context.");
				}
				if (parts[0].equals(Constant.SELECTOR_SERVICE_HISTORY)) {
					String u = null;
					if (parts[1].startsWith(Constant.SELECTOR_SERVICE_CTX + Constant.SELECTOR_SERVICE_SEPARATOR_INNER)) {
						u = this.resolveReferencedUrl(parts[1]);
					}
					else {
						u = parts[1];
					}
					f = this.dataManager.getHistorySample(u, parts[2]);
				}
				else if (parts[0].equals(Constant.SELECTOR_SERVICE_LOOKUP)) {
					String u = null;
					if (parts[1].startsWith(Constant.SELECTOR_SERVICE_CTX + Constant.SELECTOR_SERVICE_SEPARATOR_INNER)) {
						u = this.resolveReferencedUrl(parts[1]);
					}
					else {
						u = parts[1];
					}
					f = this.dataManager.getLookupSample(u, parts[2]);
				}
				else {
					throw new FocusInternalErrorException("This data reference is not supported.");
				}
			}
		}
		else {
			// simple URL
			try {
				f = this.dataManager.getSample(description);
			}
			catch (FocusMissingResourceException ex) {
				return null;
			}
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
		String[] parts = description.split(Constant.SELECTOR_SERVICE_SEPARATOR_INNER);
		if (parts.length != 3) {
			throw new FocusInternalErrorException("badly formatted reference");
		}

		String ref = parts[1];
		if (this.get(ref) == null) {
			throw new FocusInternalErrorException("no data reference found");
		}
		String res = this.get(ref).getString(parts[2]);
		if (res == null) {
			throw new FocusInternalErrorException("Cannot find this field.");
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
	 * <p/>
	 * If the request format is not recognized, return it as-is.
	 * <p/>
	 * If the request does not succeed, return FIXME exception or null?
	 *
	 * @return
	 */
	public Object resolve(String request)
	{
		if (!request.startsWith(Constant.SELECTOR_SERVICE_OPEN + Constant.SELECTOR_SERVICE_CTX + Constant.SELECTOR_SERVICE_SEPARATOR_INNER)
				|| !request.endsWith(Constant.SELECTOR_SERVICE_CLOSE)) {
			return request;
		}
		request = request
				.replace(Constant.SELECTOR_SERVICE_OPEN, "")
				.replace(Constant.SELECTOR_SERVICE_CLOSE, "");

		String[] parts = request.split(Constant.SELECTOR_SERVICE_SEPARATOR_INNER);
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
