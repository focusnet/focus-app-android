/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted; free of charge; to any person obtaining a copy of this software
 * and associated documentation files (the "Software"); to deal in the Software without restriction;
 * including without limitation the rights to use; copy; modify; merge; publish; distribute;
 * sublicense; and/or sell copies of the Software; and to permit persons to whom the Software is
 * furnished to do so; subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS"; WITHOUT WARRANTY OF ANY KIND; EXPRESS OR IMPLIED; INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY; FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM;
 * DAMAGES OR OTHER LIABILITY; WHETHER IN AN ACTION OF CONTRACT; TORT OR OTHERWISE; ARISING FROM;
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.util;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.PageInstance;
import eu.focusnet.app.model.ProjectInstance;

/**
 * Contains constants that are not class-specific.
 */
final public class Constant
{
	/**
	 * Constants related to Application configuration
	 */
	final public static class AppConfig
	{

		/**
		 * Name of our properties file in the assets directory.
		 */
		final public static String ASSETS_PROPERTY_FILE = "focus.properties";

		/**
		 * Date format being used internally in JSON structures. This is ISO 8601.
		 */
		final public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

		/**
		 * Property key for the ACRA form-uri parameter
		 */
		final public static String PROPERTY_ACRA_FORM_URI = "acra.form-uri";

		/**
		 * Property key for the ACRA username parameter
		 */
		final public static String PROPERTY_ACRA_USERNAME = "acra.username";

		/**
		 * Property key for the ACRA password parameter
		 */
		final public static String PROPERTY_ACRA_PASSWORD = "acra.password";

		/**
		 * Path to the asset with the content of the About dialog
		 */
		final public static String ASSETS_ABOUT_PAGE = "documentation/about.html";

		/**
		 * We do not allow data synchronization to occur too often and this constant defines
		 * the minimum interval between 2 consecutive data synchronizations. This applies to
		 * periodic tasks (see {@link eu.focusnet.app.service.CronService}) as well to manual
		 * synchronization.
		 */
		final public static int CRON_SERVICE_MINIMUM_DURATION_BETWEEN_SYNC_DATA_IN_MILLISECONDS = 10 * 60 * 1_000;

		/**
		 * Periodic tasks are run only once every
		 * {@link #CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES}, but this value is quite high and
		 * the user may not be using the application for that long before putting his device in
		 * sleep mode. So we check if a new synchronization is required more often, and this
		 * polling interval is defined here, in minutes.
		 */
		final public static int CRON_SERVICE_POLLING_INTERVAL_IN_MINUTES = 2;

		/**
		 * How long should we wait between 2 consecutive automatic data synchronization?
		 */
		final public static int CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES = 30;

		/**
		 * How long should we wait before the first data synchronization after application startup?
		 */
		final public static int CRON_SERVICE_DURATION_TO_WAIT_BEFORE_FIRST_SYNC_IN_MINUTES = 30;

		/**
		 * Identifier of the wake lock for periodic operations
		 */
		final public static String CRON_WAKE_LOCK_NAME = "FOCUS_CRON_TASK";

		/**
		 * Login activity
		 */
		final public static Class LOGIN_ACTIVITY = eu.focusnet.app.ui.activity.DemoUseCaseSelectionActivity.class;

		/**
		 * Minimum duration of splashscreen showing, in milliseconds.
		 */
		final public static long SPLASHSCREEN_MINIMUM_DISPLAY_DURATION_IN_MILLISECONDS = 2_000;

		/**
		 * Prefix of properties that define the alterations to be made to HTTP requests headers
		 * for authorization purpose.
		 */
		final public static String PROPERTY_HTTP_REQUEST_MODIFIER_PREFIX = "http-request-header-modification.";

		/**
		 * This property defines the endpoint where our application-specific data are stored
		 * (User information, User preferences)
		 */
		final public static String PROPERTY_TARGET_PERMANENT_STORAGE_SERVER = "resource-server.endpoint";

		/**
		 * Asset folder containing the self-signed certificates.
		 */
		final public static String ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER = "self-signed-certificates";

		/**
		 * Property name for the default locale
		 */
		final public static String PROPERTY_DEFAULT_LOCALE = "i18n.default-locale";

		/**
		 * Fallback language if everything goes wrong.
		 */
		final public static String LOCALE_FALLBACK_LANGUAGE = "en";

		/**
		 * Fallback country code if everything goes wrong.
		 */
		final public static String LOCALE_FALLBACK_COUNTRY = "";
	}

	/**
	 * SharedPreferences contain basic information for logging in and starting the application
	 * when no network is available.
	 */
	final public static class SharedPreferences
	{
		/**
		 * Name of the file for SharedPreferences
		 */
		public static final String SHARED_PREFERENCES_STORE_NAME = "eu.focusnet.app.INTERNAL_CONFIGURATION";

		/**
		 * Key name for Server name as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_LOGIN_SERVER = "login-server";

		/**
		 * Key name for Username for authentication as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_LOGIN_USERNAME = "login-username";

		/**
		 * Key name for Password for authentication as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_LOGIN_PASSWORD = "login-password";

		/**
		 * Key name for URI to user information as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_USER_INFOS = "user-infos";

		/**
		 * Key name for URI to application preferences as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_APPLICATION_SETTINGS = "user-preferences";

		/**
		 * Key name for URI to application content as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_APPLICATION_CONTENT = "application-content";

		/**
		 * Key name for URI to demo use case ID as stored in the SharedPreferences.
		 *
		 * @deprecated Used only in the prototype. Can be safely removed in production version.
		 */
		public static final String SHARED_PREFERENCES_DEMO_USE_CASE = "demo-use-case";

		/**
		 * Key name for Last data synchronization as stored in the SharedPreferences.
		 */
		public static final String SHARED_PREFERENCES_LAST_SYNC = "last-sync";

	}

	/**
	 * Constants related to local SQLite database
	 */
	final public static class Database
	{
		/**
		 * Current version of the database
		 */
		final public static int DATABASE_VERSION = 1;

		/**
		 * Database name
		 */
		final public static String DATABASE_NAME = "focus";

		/**
		 * Name of the table holding our stored samples (FocusObjects)
		 */
		final public static String DATABASE_TABLE_SAMPLES = "sample";

		/**
		 * A JSON schema identifier (URL) defining the type of resource stored in the row.
		 */
		final public static String TYPE = "type";

		/**
		 * Unique resource identifier.
		 */
		final public static String URL = "url";

		/**
		 * Owner of the resource
		 * <p/>
		 * FIXME: hard-coded and not really used for now, as we do not have per-user access control in FOCUS, yet.
		 */
		final public static String OWNER = "owner";

		/**
		 * Editor of the sample, i.e. the individual or organization that edited this version
		 * of the resource.
		 * <p/>
		 * FIXME: hard-coded and not really used for now, as we do not have per-user access control in FOCUS, yet.
		 */
		final public static String EDITOR = "editor";

		/**
		 * Epoch defining when the resource has been created.
		 */
		final public static String CREATION_EPOCH = "creation_date_time";

		/**
		 * Epoch defining when this version of the resource (sample) has been edited.
		 */
		final public static String EDITION_EPOCH = "edition_date_time";

		/**
		 * Current version number of the resource sample.
		 */
		final public static String VERSION = "version";

		/**
		 * Tells whether this resource is active or not.
		 */
		final public static String ACTIVE = "active";

		/**
		 * Raw representation of the sample. This is a JSON object that complies to the type
		 * defined in {@link #TYPE}.
		 */
		final public static String DATA = "data";

		/**
		 * Tells whether the current sample is marked for deletion (will occur right away or
		 * during periodic tasks, see {@link eu.focusnet.app.service.CronService}).
		 */
		final public static String TO_DELETE = "to_delete";

		/**
		 * Tells whether the current sample is marked for update (will occur right away or
		 * during periodic tasks, see {@link eu.focusnet.app.service.CronService}).
		 */
		final public static String TO_UPDATE = "to_update";

		/**
		 * Tells whether the current sample is marked for creation (will occur right away or
		 * during periodic tasks, see {@link eu.focusnet.app.service.CronService}).
		 */
		final public static String TO_CREATE = "to_create";

		/**
		 * Current data set id. The data set id is used to distinguish between different data
		 * synchronisation sets: the application may be running using a set A, but if the user
		 * triggers data syncronization or if the periodic task requests data synchronization, we
		 * then have to store the new data set in the database without interfering with the
		 * currently used data set, and this id is used for that purpose. Once the data sync
		 * operation is successfully terminated, the application will use the new data set and the
		 * old one will be deleted.
		 */
		final public static String DATA_SET_ID = "data_set_id";

		/**
		 * Table creation string
		 */
		final public static String CREATE_TABLE_SAMPLES_QUERY =
				"CREATE TABLE " + DATABASE_TABLE_SAMPLES +
						"(" +
						URL + " TEXT, " +
						VERSION + " INTEGER, " +
						TYPE + " TEXT, " +
						OWNER + " TEXT, " +
						CREATION_EPOCH + " INTEGER, " +
						EDITION_EPOCH + " INTEGER, " +
						EDITOR + " TEXT, " +
						DATA + " TEXT, " +
						TO_DELETE + " BOOL, " +
						TO_UPDATE + " BOOL, " +
						TO_CREATE + " BOOL, " +
						DATA_SET_ID + " INTEGER, " +
						ACTIVE + " BOOL NOT NULL DEFAULT TRUE, " +
						"UNIQUE(" + URL + ", " + VERSION + " , " + DATA_SET_ID + ") ON CONFLICT REPLACE" +
						")";
	}

	/**
	 * Constants related to the navigation system of our application. All Projects, Dashboards &
	 * Tools (Pages) and widgets are uniquely identified by a path within the application, and these
	 * constants define the format of this path.
	 * <p/>
	 * The different list item types and constants used for referencing specific menu entries
	 * are also defined here.
	 */
	final public static class Navigation
	{
		/**
		 * Root of the path. This String prefixes any path.
		 */
		final public static String PATH_ROOT = "FOCUS";

		/**
		 * The different levels of paths are separated by this String.
		 * <p/>
		 * For example: FOCUS|project1|DASHBOARD|page1
		 */
		final public static String PATH_SEPARATOR = "|";

		/**
		 * The corresponding Regexp pattern for Regexp matching.
		 */
		final public static String PATH_SEPARATOR_PATTERN = "\\|";

		/**
		 * When iterating over multiple objects, we distinguish the different paths by defining a
		 * selector and this constant defines the opening character for that selector.
		 * <p/>
		 * For example: FOCUS|projectA[http://object-of-interest-1]|...
		 */
		final public static String PATH_SELECTOR_OPEN = "[";

		/**
		 * When iterating over multiple objects, we distinguish the different paths by defining a
		 * selector and this constant defines the closing character for that selector.
		 * <p/>
		 * For example: FOCUS|projectA[http://object-of-interest-1]|...
		 */
		final public static String PATH_SELECTOR_CLOSE = "]";

		/**
		 * When iterating over multiple objects at the project level, a variable is created in the
		 * {@link ProjectInstance}'s
		 * {@link DataContext} and this constant defines the
		 * name of this variable. This variable is also available
		 * to child {@link PageInstance}s.
		 */
		final public static String LABEL_PROJECT_ITERATOR = "$project-iterator$";

		/**
		 * When iterating over multiple objects at the page level, a variable is created in the
		 * {@link PageInstance}'s
		 * {@link DataContext} and this constant defines the
		 * name of this variable.
		 */
		final public static String LABEL_PAGE_ITERATOR = "$page-iterator$";

		/**
		 * List item type corresponding the a header
		 * (non clickable, {@link eu.focusnet.app.ui.common.SimpleListItem}).
		 */
		final public static int LIST_TYPE_HEADER = 0;

		/**
		 * List item type corresponding the a link
		 * (clickable, {@link eu.focusnet.app.ui.common.FeaturedListItem}).
		 */
		final public static int LIST_TYPE_LINK = 1;

		/**
		 * List item type corresponding the an empty entry, i.e. an item that announces that
		 * the list is empty. (non clickable, {@link eu.focusnet.app.ui.common.SimpleListItem}).
		 */
		final public static int LIST_TYPE_EMPTY = 2;

		/**
		 * Target fragment index for the drawer menu: non-clickable header image.
		 */
		final public static int UI_MENU_ENTRY_HEADER_NON_CLICKABLE = 0;

		/**
		 * Target fragment index for the drawer menu: projects listing.
		 */
		final public static int UI_MENU_ENTRY_PROJECTS_LISTING = 1;

		/**
		 * Target fragment index for the drawer menu: bookmark page.
		 */
		final public static int UI_MENU_ENTRY_BOOKMARK = 2;

		/**
		 * Target fragment index for the drawer menu: About dialog.
		 */
		final public static int UI_MENU_ENTRY_ABOUT = 3;

		/**
		 * Target fragment index for the drawer menu: logout.
		 */
		final public static int UI_MENU_ENTRY_LOGOUT = 4;
	}

	/**
	 * Constants definining the different types being used within the application content definition.
	 */
	final public static class DataModelTypes
	{

		/**
		 * JSON schema of the object used to represent user's information
		 * (see {@link eu.focusnet.app.model.UserInstance}).
		 */
		final public static String FOCUS_DATA_MODEL_TYPE_USER = "http://reference.focusnet.eu/schemas/focus-user-information/v1.0";

		/**
		 * JSON schema of the object used to represent user's preferences
		 * (see {@link eu.focusnet.app.model.UserPreferencesInstance}).
		 */
		final public static String FOCUS_DATA_MODEL_TYPE_USER_PREFERENCES = "http://reference.focusnet.eu/schemas/focus-mobile-app-user-preferences/v1.0";

		/**
		 * JSON schema of the object used to represent FOCUS Samples, which are used to represent
		 * arbitrary data being consumed by the application.
		 * See {@link eu.focusnet.app.model.gson.FocusSample}.
		 */
		final public static String FOCUS_DATA_MODEL_TYPE_FOCUS_SAMPLE = "http://reference.focusnet.eu/schemas/focus-data-sample/v1.0";

		/**
		 * Identifier for a text widget.
		 */
		final public static String WIDGET_TYPE_TEXT = "#/definitions/widget/visualize/text";

		/**
		 * Identifier for a table widget.
		 */
		final public static String WIDGET_TYPE_TABLE = "#/definitions/widget/visualize/table";

		/**
		 * Identifier for a pie chart widget.
		 */
		final public static String WIDGET_TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";

		/**
		 * Identifier for a bar chart widget.
		 */
		final public static String WIDGET_TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";

		/**
		 * Identifier for a line chart widget.
		 */
		final public static String WIDGET_TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";

		/**
		 * Identifier for a camera widget.
		 */
		final public static String WIDGET_TYPE_CAMERA = "#/definitions/widget/collect/camera";

		/**
		 * Identifier for a GPS widget.
		 */
		final public static String WIDGET_TYPE_GPS = "#/definitions/widget/collect/gps";

		/**
		 * Identifier for a form widget.
		 */
		final public static String WIDGET_TYPE_FORM = "#/definitions/widget/collect/form";

		/**
		 * Identifier for an external app widget.
		 */
		final public static String WIDGET_TYPE_EXTERNAL_APP = "#/definitions/widget/collect/external-app";

		/**
		 * Identifier for a webapp widget.
		 */
		final public static String WIDGET_TYPE_HTML5_WEBAPP = "#/definitions/widget/visualize/html5-widget";

		/**
		 * Identifier for a submit button widget.
		 */
		final public static String WIDGET_TYPE_SUBMIT = "#/definitions/widget/collect/submit";

		/**
		 * Identifier for a textfield field in a form widget.
		 */
		final public static String FIELD_TYPE_TEXTFIELD = "#/definitions/widget/collect/form/fields/textfield";

		/**
		 * Identifier for a textarea field in a form widget.
		 */
		final public static String FIELD_TYPE_TEXTAREA = "#/definitions/widget/collect/form/fields/textarea";

		/**
		 * Identifier for a checkbox field in a form widget.
		 */
		final public static String FIELD_TYPE_CHECKBOX = "#/definitions/widget/collect/form/fields/checkbox";

		/**
		 * Identifier for a selection list field in a form widget.
		 */
		final public static String FIELD_TYPE_SELECT = "#/definitions/widget/collect/form/fields/select";
	}

	/**
	 * Constants defining the syntax for the different means of accessing data.
	 */
	final public static class DataReference
	{
		/**
		 * A data selector is identified by an opening character, a selector type keyword
		 * and parameters, and a closing character. This constant defines the opening character.
		 * <p/>
		 * There are 2 types of data selectors: context accessor and services. context accessor
		 * (ctx) definitions are separated with / and services are separated with |. Context
		 * accessor can be nested into services.
		 * <p/>
		 * For example:
		 * - {@code <ctx/example/prop1>}
		 * - {@code <history|example|since=...>}
		 * - {@code <history|ctx/example/prop2|since=...>}
		 */
		final public static String SELECTOR_OPEN = "<";

		/**
		 * Closing character of a data selector. See {@link #SELECTOR_OPEN}.
		 */
		final public static String SELECTOR_CLOSE = ">";

		/**
		 * Data selector keywork for accessing the current
		 * {@link DataContext}. See {@link #SELECTOR_OPEN}.
		 */
		final public static String SELECTOR_CONTEXT_LABEL = "ctx";

		/**
		 * Data selector keywork for accessing the history of
		 * a {@link eu.focusnet.app.model.gson.FocusSample}. See {@link #SELECTOR_OPEN}.
		 */
		final public static String SELECTOR_SERVICE_HISTORY = "history";

		/**
		 * Character separating the different elements of a data selector.
		 * See {@link #SELECTOR_OPEN}.
		 */
		final public static String SELECTOR_CONTEXT_SEPARATOR = "/";

		/**
		 * Services separator pattern. See {@link #SELECTOR_OPEN}.
		 */
		final public static String SELECTOR_SERVICE_SEPARATOR_PATTERN = "\\|";

		/**
		 * Pattern matching the {@link #SELECTOR_CONTEXT_LABEL} selector.
		 */
		final public static String SELECT_CONTEXT_FULL_PATTERN = SELECTOR_OPEN
				+ SELECTOR_CONTEXT_LABEL
				+ SELECTOR_CONTEXT_SEPARATOR
				+ "[^" + SELECTOR_CONTEXT_SEPARATOR + SELECTOR_CLOSE + "]+("
				+ SELECTOR_CONTEXT_SEPARATOR
				+ "[^" + SELECTOR_CLOSE + "]+)?"
				+ SELECTOR_CLOSE;
	}


	/**
	 * Constants related to the networking infrastructure.
	 */
	final public static class Networking
	{
		/**
		 * Maximum number of concurrent downloads.
		 */
		final public static int MAX_CONCURRENT_DOWNLOADS = 40;

		/**
		 * Success status code. This is a bitmask.
		 */
		final public static int NETWORK_REQUEST_STATUS_SUCCESS = 0x0;

		/**
		 * Network failure status code. This is a bitmask.
		 */
		final public static int NETWORK_REQUEST_STATUS_NETWORK_FAILURE = 0x1;

		/**
		 * Bad response status code, e.g. 404, 403. This is a bitmask.
		 */
		final public static int NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE = 0x2;

		/**
		 * HTTP GET
		 */
		final public static String HTTP_METHOD_GET = "GET";

		/**
		 * HTTP POST
		 */
		final public static String HTTP_METHOD_POST = "POST";

		/**
		 * HTTP PUT
		 */
		final public static String HTTP_METHOD_PUT = "PUT";

		/**
		 * HTTP DELETE
		 */
		final public static String HTTP_METHOD_DELETE = "DELETE";
	}


	/**
	 * Extras for inter-activity/Fragment communication
	 */
	final public static class Extra
	{
		/**
		 * Saved title of the page
		 */
		final public static String UI_EXTRA_TITLE = "eu.focusnet.app.extra.TITLE";

		/**
		 * Path as defined in {@link Constant.Navigation}
		 */
		final public static String UI_EXTRA_PATH = "eu.focusnet.app.extra.PATH";

		/**
		 * Text to display on the splash loading screen
		 */
		final public static String UI_EXTRA_LOADING_INFO_TEXT = "eu.focusnet.app.extra.LOADING_INFO_TEXT";

		/**
		 * Weight information for the widget to display. This is used to actually define the width
		 * taken by widgets on our grid.
		 */
		final public static String UI_EXTRA_LAYOUT_WEIGHT = "eu.focusnet.app.extra.LAYOUT_WEIGHT";

		/**
		 * Defines where the widget is located in the row. This is used to decide whether we set a
		 * margin around the widget or not.
		 */
		final public static String UI_EXTRA_LAYOUT_POSITION_IN_ROW = "eu.focusnet.app.extra.POSITION_IN_ROW";

		/**
		 * Target URI of the image being captured by the Camera widget
		 */
		final public static String UI_EXTRA_IMAGE_URI = "eu.focusnet.app.extra.IMAGE_URI";


		// Extras for communication with third-party apps
		/**
		 * Used for transmitting input object to external application. We send a stringified
		 * JSON FocusSample in this extra.
		 * <p/>
		 * See {@link eu.focusnet.app.ui.fragment.widget.ExternalAppFragment}.
		 */
		final public static String UI_EXTRA_EXTERNAL_APP_INPUT = "eu.focusnet.app.extra.EXTERNAL_APP_INPUT";

		/**
		 * Used to acquire return value from external application. We receive a stringified
		 * JSON FocusSample in this extra.
		 * <p/>
		 * FIXME not used, yet, but ready.
		 * <p/>
		 * See {@link eu.focusnet.app.ui.fragment.widget.ExternalAppFragment}.
		 */
		final public static String UI_EXTRA_EXTERNAL_APP_OUTPUT = "eu.focusnet.app.extra.EXTERNAL_APP_OUTPUT";
	}

	/**
	 * UI-related constants
	 */
	final public static class Ui
	{
		/**
		 * The layout in pages is organized based on this number of columns. This integer defines the
		 * maximum number of columns
		 */
		final public static int LAYOUT_NUM_OF_COLUMNS = 4;

		/**
		 * This String defines the separator used to define the number of columns.
		 * E.g. 2of4 means that the UI component will take 2 columns
		 * out of 4 available. See {@link #LAYOUT_NUM_OF_COLUMNS}.
		 */
		final public static String WIDGET_LAYOUT_OF = "of";

		/**
		 * Default width of widgets
		 */
		final public static String WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE = "4of4";

		/**
		 * Layout property defining the width taken by the widget.
		 */
		final public static String WIDGET_LAYOUT_WIDTH_LABEL = "width";


		/**
		 * Default margin size around widgets in dp.
		 */
		final public static int UI_MARGIN_SIZE_DP = 22;
	}


}
