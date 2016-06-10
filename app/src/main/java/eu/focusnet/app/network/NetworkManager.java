/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.network;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.json.FocusObject;


/**
 * This class contains all methods pertaining to networking.
 * <p/>
 * This class is an abstraction library for communicating with our REST server.
 * <p/>
 */
public class NetworkManager
{
	private static final String ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER = "self-signed-certificates";
	private SSLContext sslContext;
	private Context context = null;

	public final static int NETWORK_REQUEST_STATUS_SUCCESS = 0x0;
	public final static int NETWORK_REQUEST_STATUS_NETWORK_FAILURE = 0x1;
	public final static int NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE = 0x2;


	// FIXME get the root of REST server on first request (such that we have the root of services)

	/**
	 * C'tor.
	 */
	public NetworkManager()
	{
		this.context = FocusApplication.getInstance();

		// we do this in the NetworkManager such that we do it only once for the whole app
		try {
			this.initSSLContext();
		}
		catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
			throw new FocusInternalErrorException("Error when importing our local trusted certificates.");
		}
	}

	/**
	 * init SSL context
	 * <p/>
	 * We must create a custom TrustManagerFactory because some of our certificates are self-signed.
	 * <p/>
	 * Android developer doc: https://developer.android.com/training/articles/security-ssl.html#SelfSigned
	 * and we also fallback to the default manager
	 *
	 * FIXME FIXME DEBUG: we probably should not accept self-signed certificates in the future.
	 *
	 * @return
	 */
	//
	private void initSSLContext() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException
	{
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init((KeyStore) null); // null -> use default trust store

		// save default trust manager
		X509TrustManager defaultTrustManager = null;
		for (TrustManager tm : tmf.getTrustManagers()) {
			if (tm instanceof X509TrustManager) {
				defaultTrustManager = (X509TrustManager) tm;
				break;
			}
		}

		// Init certificate factory
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		// Create an empty KeyStore to hold our trusted certificates
		String keyStoreType = KeyStore.getDefaultType();
		KeyStore myKeyStore = KeyStore.getInstance(keyStoreType);
		myKeyStore.load(null, null);

		// Add each certificate in the keystore
		AssetManager am = FocusApplication.getInstance().getAssets();
		List<String> certificates = Arrays.asList(am.list(ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER));
		for (String cert : certificates) {
			InputStream caInput = new BufferedInputStream(am.open(ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER + "/" + cert));
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
			}
			finally {
				caInput.close();
			}
			myKeyStore.setCertificateEntry(cert, ca);
		}

		// Create a TrustManager that trusts the CAs in our KeyStore
		TrustManagerFactory myTrustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		myTrustManagerFactory.init(myKeyStore);

		// Get hold of the new trust manager
		X509TrustManager myTrustManager = null;
		for (TrustManager tm : myTrustManagerFactory.getTrustManagers()) {
			if (tm instanceof X509TrustManager) {
				myTrustManager = (X509TrustManager) tm;
				break;
			}
		}

		// merge the results of the default and custom managers
		// into a custom trust manager
		final X509TrustManager finalMyTrustManager = myTrustManager;
		final X509TrustManager finalDefaultTrustManager = defaultTrustManager;
		X509TrustManager customTrustManager = new X509TrustManager()
		{

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				if (finalDefaultTrustManager == null) {
					throw new FocusInternalErrorException("No default trust store found");
				}
				finalDefaultTrustManager.checkClientTrusted(chain, authType);
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
			{
				if (finalMyTrustManager == null || finalDefaultTrustManager == null) {
					throw new FocusInternalErrorException("No custom or default trust store found");
				}
				try {
					finalMyTrustManager.checkServerTrusted(chain, authType);
				} catch (CertificateException e) {
					// This will throw another CertificateException if this fails too.
					finalDefaultTrustManager.checkServerTrusted(chain, authType);
				}
			}

			@Override
			public X509Certificate[] getAcceptedIssuers()
			{
				if (finalDefaultTrustManager == null) {
					throw new FocusInternalErrorException("No default trust store found");
				}
				return finalDefaultTrustManager.getAcceptedIssuers();
			}
		};

		// make the SSL context available to the rest of the app
		this.sslContext = SSLContext.getInstance("TLS");
		this.sslContext.init(null, new TrustManager[]{customTrustManager}, null);
	}


	// FIXME TODO copy RefreshData service from Yandy's code
	// FIXME useful in the end?


	/**
	 * Is the network currently available?
	 * <p/>
	 * FIXME perhaps move to another Helper class, such that NetworkManager does not need a Context at all
	 *
	 * @return true if network is available, false otherwise.
	 */
	public boolean isNetworkAvailable() throws RuntimeException
	{
		ConnectivityManager connMgr = (ConnectivityManager)
				FocusApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

		// wifi
		boolean isWifiConn = false;
		NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			isWifiConn = networkInfo.isConnected();
		}

		// mobile
		networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = false;
		if (networkInfo != null) {
			isMobileConn = networkInfo.isConnected();
		}

		return isWifiConn || isMobileConn;
	}


	/**
	 * Get the latest version of an existing resource
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse get(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_GET, url);
		return request.execute();
	}

	/**
	 * Get a specific version of an existing resource
	 * <p/>
	 * <p/>
	 * FIXME any use?
	 *
	 * @param url
	 * @param version
	 * @return
	 * @throws IOException
	 */
	public HttpResponse get(String url, int version) throws IOException
	{
		url = url + "/" + version;
		return this.get(url);
	}

	/**
	 * Update an existing resource
	 *
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public HttpResponse put(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_PUT, url, data);
		return request.execute();
	}

	/**
	 * Create a new resource
	 *
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public HttpResponse post(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_POST, url, data);
		return request.execute();
	}

	/**
	 * Delete a remote resource.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse delete(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(HttpRequest.HTTP_METHOD_DELETE, url);
		return request.execute();
	}

	/**
	 * Do login to remote endpoint
	 * FIXME wait for Jussi's authentication server
	 *
	 * @param user
	 * @param password
	 * @param server
	 * @return true on success or false on failure (access forbidden). If a network error occurs,
	 * throw a IOException.
	 */
	public boolean login(String user, String password, String server) throws IOException
	{
		throw new FocusNotImplementedException("NetworkManager.login()");
	}

	/**
	 * Get the SSL context
	 */
	public SSLContext getSSLContext()
	{
		return this.sslContext;
	}


	/**
	 * Custom (and dummy) host name verifier
	 * <p/>
	 * This is prototype code and is not secure. It may be used to bypass hostname validation of SSL certificates.
	 *
	 * If required, put in NetworkManager constructor
	 * HttpsURLConnection.setDefaultHostnameVerifier(new DummyHostNameVerifier());
	 *
	 */
	private static class DummyHostNameVerifier implements HostnameVerifier
	{
		@Override
		public boolean verify(String hostname, SSLSession session)
		{
			return true;
		}
	}
}
