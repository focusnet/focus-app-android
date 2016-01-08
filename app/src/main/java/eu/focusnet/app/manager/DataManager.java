package eu.focusnet.app.manager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import eu.focusnet.app.adapter.DateTypeAdapter;
import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.model.data.FocusObject;
import eu.focusnet.app.model.data.FocusSample;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.data.User;
import eu.focusnet.app.network.HttpResponse;
import eu.focusnet.app.network.NetworkManager;

/**
 * This follows a Singleton pattern.
 * <p/>
 * Created by julien on 07.01.16.
 */
public class DataManager
{
	private static DataManager ourInstance = new DataManager();
	private static Context context = null;

	private static final String TAG = DataManager.class.getName();

	// other information regarding login?
	private String loginUser = "";
	private String loginPassword = "";
	private String loginServer = "";
	private String userUrl = "";
	private String prefUrl = "";
	private String appContentUrl = "";

	// java objects
	private User user = null;
	private Preference userPreferences = null;
	private AppContent appContent = null;


	// for samples
	private HashMap<URL, Object> cache = new HashMap<URL, Object>();

	private Gson gson = null;
	private NetworkManager net = null;


	public static DataManager getInstance(Context c)
	{
		context = c;
		return ourInstance;
	}

	/**
	 * Initialize the Singleton.
	 */
	private DataManager()
	{
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
		this.net = NetworkManager.getInstance(context);


		// get login infos from local store, and use it as the default FIXME TODO


		// FIXME DEBUG for now we just use hard-coded values.
		this.loginUser = "dummy-user";
		this.loginPassword = "dummy-password";
		this.loginServer = "dummy-server";
		// get the different application configuration objects
		// FIXME that may be in acquirePrimitiveAppData();
		int userId = 123; // FIXME hard-coded. should be obtained from network.
		this.userUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/user-information";
		this.prefUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-user-preferences";
		this.appContentUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-content-definition";
	}

	/**
	 * Login and if successful, save the login information in the permanent store.
	 * <p/>
	 * This method relies on network connectivity.
	 *
	 * @param user
	 * @param password
	 * @param server
	 */
	public boolean login(String user, String password, String server)
	{

//		// FIXME no network call when just saving this. the login is done in the EntryPointActivity.
//		// if no network, then return RuntimeException
//		if (!this.net.isNetworkAvailable()) {
//			throw new RuntimeException("No network");
//		}

		// do network login
		if (!this.net.login(user, password, server)) {
			return false;
		}

		// otherwise save the content of our new login into the permanent store for later use
		/*this.loginUser = user;
		this.loginPassword = password;
		this.loginServer = server;

		// get the different application configuration objects
		// FIXME that may be in acquirePrimitiveAppData();
		int userId = 123; // FIXME hard-coded. should be obtained from network.
		this.userUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/user-information";
		this.prefUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-user-preferences";
		this.appContentUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-content-definition";
*/
		// permanently store FIXME TODO
		// e.g. in http://localhost/LOCAL/app-config (which is a FocusSample)

		return true;

	}

	/**
	 * Delete login information, and reset the content of the whole application
	 */
	public void logout()
	{
		this.loginUser = "";
		this.loginPassword = "";
		this.loginServer = "";
		this.userUrl = "";
		this.prefUrl = "";
		this.appContentUrl = "";
		this.user = null;
		this.userPreferences = null;
		this.appContent = null;
		this.cache = new HashMap<URL, Object>();

		// delete SQL db
	}


	/**
	 * Do we have login information?
	 *
	 * @return
	 */
	public boolean hasLoginInformation()
	{
		return !this.loginUser.isEmpty()
				&& !this.loginPassword.isEmpty()
				&& !this.loginServer.isEmpty()
				&& !this.userUrl.isEmpty()
				&& !this.prefUrl.isEmpty()
				&& !this.appContentUrl.isEmpty();
	}


	/**
	 * Acquire personal information about the user of the application
	 */
	public User getUser() throws RuntimeException
	{
		if (!this.hasLoginInformation()) {
			throw new RuntimeException("No login information. Cannot continue.");
		}

		if (this.user != null) {
			Log.d(TAG, "User cached!!!!");
			return this.user;
		}
		this.user = (User)(this.get(this.userUrl, User.class));
		return this.user;
	}

	/**
	 * Acquire the application's user preferences.
	 *
	 * @return
	 */
	public Preference getUserPreferences() throws RuntimeException
	{
		if (!this.hasLoginInformation()) {
			throw new RuntimeException("No login information. Cannot continue.");
		}

		if (this.userPreferences != null) {
			return this.userPreferences;
		}
		this.userPreferences = (Preference)(this.get(this.prefUrl, Preference.class));

		return this.userPreferences;
	}

	/**
	 * Acquire the application content object
	 *
	 * @return
	 */
	public AppContent getAppContent() throws RuntimeException
	{
		if (!this.hasLoginInformation()) {
			throw new RuntimeException("No login information. Cannot continue.");
		}

		if (this.appContent != null) {
			return this.appContent;
		}
		this.appContent = (AppContent)(this.get(this.appContentUrl, AppContent.class));
		return this.appContent;
	}

	/**
	 * Get the three basic informations that are required to build the application UI
	 */
	public void acquirePrimitiveAppData() throws RuntimeException
	{
		this.getUser();
		this.getUserPreferences();
		AppContent a = this.getAppContent();


		FocusSample out = this.getSample("http://focus.yatt.ch/resources-server/data/yarr"); // FIXME DEBUG
		FocusSample out2 = (FocusSample)null;

		return;
	}


	/**
	 * Get a FocusSample
	 *
	 * @param url
	 * @return
	 */
	public FocusSample getSample(String url) throws RuntimeException
	{
		if (!this.hasLoginInformation()) {
			throw new RuntimeException("No login information. Cannot continue.");
		}
		FocusSample sample = (FocusSample)(this.get(url, FocusSample.class));
		return sample;
	}


	/**
	 * Get the appropriate copy of the data identified by the provided url.
	 */
	private FocusObject get(String url, Class targetClass) throws RuntimeException
	{

		// 2 cases: with or without network
		/*
		 * NETWORK:
		 * 		download if not available in local store
		 * 			on failure return null
		 * 		put in local stores
		 *
		 * 		will be automatically reloaded by sync service -> no need to check for freshness at this point!
		 *
		 * NO NETWORK:
		 * 		get it if in local store if available
		 * 		otherwise return null
		 *
		 *
		 *
		 *
		 * GARBAGE COLLECTION:
		 * 		everytime a sample is used (.get()), we update the lastUsage field
		 * 		on garbage collection (=when max num of elements is reached) -> delete up to N based on that info
		 	* 	e.g. max usage == 100MB -> if needed, free 20MB   or   the ones that are >2 days old
		 */


		// for now everything comes from the network.

		HttpResponse response = null;

		try {
			response = this.net.get(url);
		}
		catch (IOException e) {
			Log.d(TAG, "IOEXecption HTTP get in dm");
			e.printStackTrace();
		}

		if (response.isSuccessful()) {
			String json = response.getData();
			return (FocusObject)this.gson.fromJson(json, targetClass);
			// FIXME TODO check that type corresponds to what we expected with targetClass ?
		}

// FIXME cache the result.
		return null;
		/*
		 * 1. local cache
		 * 2. local store -> push to local cache
		 * 3. network -> push in local store + local cache
		 *

		if (!this.net.isNetworkAvailable()) {
			throw new RuntimeException("No network"); // not required, may take from local store.
		}

		try {
			NetworkManager.Response r = this.net.get(url);
			if (r.hasError()) { // ignore it, but mark as error in local store?

			}
			else {
				String ret = r.getData();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		//DataProviderManager.ResponseData r = this.net.get(url);
*/


		// is resource available in local cache?
		// Y -> is it recent enough?
		// 	-> Y use it, gonetwork = false
		//  -> N discard it gonetwork = true
		// gonetwork == true ? -> download

		// here I get some JSON

	}

	public void post(URL url, Object data, Class targetClass)
	{
		/*
		 * 1. update local cache
		 * 2. update local store
		 * 3. update remote store NetworkManager.post();
		 */
	}

	public void put(URL url, Object data, Class targetClass)
	{
		/*
		 * 1. update local cache
		 * 2. update local store
		 * 3. update remote store NetworkManager.post();
		 */
	}

	public void delete(URL url)
	{
		/*
		 * 1. update local cache
		 * 2. update local store (mark as deleted)
		 * 3. update remote store NetworkManager.post();
		 */
	}

}
