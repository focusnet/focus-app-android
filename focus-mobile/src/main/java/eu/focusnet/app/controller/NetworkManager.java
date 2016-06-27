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

package eu.focusnet.app.controller;

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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusNotImplementedException;

/**
 * This class contains all methods pertaining to networking.
 * <p/>
 * This class is an abstraction library for communicating with our REST server.
 */
public class NetworkManager
{

	/**
	 * Reference to the SSL Context we have built, and that validates our approved list of
	 * self-signed certificates.
	 */
	final private static SSLContext sslContext = NetworkManager.initSslContext();

	/**
	 * C'tor.
	 */
	public NetworkManager()
	{
// 		System.setProperty("http.maxConnections", "5"); // FIXME remove! useless. after concurrency tests
	}

	/**
	 * Init our custom SSL context.
	 * <p/>
	 * We must create a custom TrustManagerFactory because some of our certificates are self-signed
	 * and we also fallback to the default manager.
	 *
	 * @return The new SSL context
	 */
	private static SSLContext initSslContext()
	{
		SSLContext newSslContext;
		try {
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
			AssetManager am = ApplicationHelper.getAssets();
			List<String> certificates = Arrays.asList(am.list(Constant.AppConfig.ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER));
			for (String cert : certificates) {
				Certificate ca;
				try (InputStream caInput = new BufferedInputStream(am.open(Constant.AppConfig.ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER + "/" + cert))) {
					ca = cf.generateCertificate(caInput);
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
					}
					catch (CertificateException e) {
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
			newSslContext = SSLContext.getInstance("TLS");
			newSslContext.init(null, new TrustManager[]{customTrustManager}, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(newSslContext.getSocketFactory());
		}
		catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException ex) {
			throw new FocusInternalErrorException(ex);
		}

		return newSslContext;
	}

	public static SSLContext getSslContext()
	{
		return sslContext;
	}


	/**
	 * Is the network currently available?
	 *
	 * @return {@code true} if network is available, {@code false} otherwise.
	 */
	public static boolean isNetworkAvailable() throws RuntimeException
	{
		ConnectivityManager connMgr = (ConnectivityManager) ApplicationHelper.getSystemService(Context.CONNECTIVITY_SERVICE);

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
	 * @param url The URL of the resource to retrieve
	 * @return An {@link HttpResponse} object
	 * @throws IOException If a network error occurs.
	 */
	public HttpResponse get(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_GET, url);
		if (request.errors == 0) {
			return request.execute();
		}
		else {
			throw new IOException("Malformed URI or other reason for not being able to create an HTTP request.");
		}
	}

	/**
	 * Update an existing resource
	 *
	 * @param url  The URL of the resource to update
	 * @param data The data to use as the content of the new version of the resource
	 * @return An {@link HttpResponse} object
	 * @throws IOException If a network error occurs.
	 */
	public HttpResponse put(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_PUT, url, data);
		return request.execute();
	}

	/**
	 * Create an existing resource
	 *
	 * @param url  The URL of the resource to create
	 * @param data The data to use as the content of the new resource
	 * @return An {@link HttpResponse} object
	 * @throws IOException If a network error occurs.
	 */
	public HttpResponse post(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_POST, url, data);
		return request.execute();
	}

	/**
	 * Delete an existing resource
	 *
	 * @param url The URL of the resource to delete
	 * @return An {@link HttpResponse} object
	 * @throws IOException If a network error occurs.
	 */
	public HttpResponse delete(String url) throws IOException
	{
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_DELETE, url);
		return request.execute();
	}

	/**
	 * Do login to remote endpoint
	 * <p/>
	 * FIXME wait for authentication server. Not implemented.
	 */
	public boolean login(String user, String password, String server)
	{
		throw new FocusNotImplementedException("NetworkManager.login()");
	}

	/**
	 * Push a modification on the network
	 *
	 * @param networkOperation The type of operation to be performed on the network (an HTTP method)
	 * @param url              The URL of the resource to modify
	 * @param fo               The source object to use as the payload of the modification
	 * @return A {@link HttpResponse} object
	 * @throws IOException If a network error occurs
	 */
	public HttpResponse pushModification(String networkOperation, String url, FocusObject fo) throws IOException
	{
		switch (networkOperation) {
			case "POST":
				return this.post(url, fo);
			case "PUT":
				return this.put(url, fo);
			case "DELETE":
				return this.delete(url);
		}
		return null;
	}
}
