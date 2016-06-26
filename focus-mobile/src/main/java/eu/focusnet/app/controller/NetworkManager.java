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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusNotImplementedException;
import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;


/**
 * This class contains all methods pertaining to networking.
 * 
 * This class is an abstraction library for communicating with our REST server.
 * 
 */
public class NetworkManager
{

	private final static SSLContext sslContext = NetworkManager.initSslContext();


	// FIXME get the root of REST server on first request (such that we have the root of services)

	/**
	 * C'tor.
	 */
	public NetworkManager()
	{
		System.setProperty("http.maxConnections", "5"); // FIXME remove!

	}

	/**
	 * init SSL context
	 * 
	 * We must create a custom TrustManagerFactory because some of our certificates are self-signed.
	 * 
	 * Android developer doc: https://developer.android.com/training/articles/security-ssl.html#SelfSigned
	 * and we also fallback to the default manager
	 * 
	 * FIXME FIXME DEBUG: we probably should not accept self-signed certificates in the future.
	 * 
	 * FIXME we do a big try/catch, that quite ugly.
	 *
	 * @return
	 */
	//
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
			KeyStore myKeyStore = null;

			myKeyStore = KeyStore.getInstance(keyStoreType);

			myKeyStore.load(null, null);


			// Add each certificate in the keystore
			AssetManager am = ApplicationHelper.getAssets();
			List<String> certificates = Arrays.asList(am.list(Constant.AppConfig.ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER));
			for (String cert : certificates) {
				InputStream caInput = new BufferedInputStream(am.open(Constant.AppConfig.ASSETS_SELF_SIGNED_CERTIFICATES_FOLDER + "/" + cert));
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


	// FIXME TODO copy RefreshData service from Yandy's code
	// FIXME useful in the end?


	/**
	 * Is the network currently available?
	 * 
	 * FIXME perhaps move to another Helper class, such that NetworkManager does not need a Context at all
	 *
	 * @return true if network is available, false otherwise.
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
	 * @param url
	 * @return
	 * @throws IOException
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
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public HttpResponse put(String url, FocusObject data) throws IOException
	{
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_PUT, url, data);
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
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_POST, url, data);
		HttpResponse r = request.execute();
		return r;
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
		HttpRequest request = new HttpRequest(Constant.Networking.HTTP_METHOD_DELETE, url);
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


	/**
	 * Custom (and dummy) host name verifier
	 * 
	 * This is prototype code and is not secure. It may be used to bypass hostname validation of SSL certificates.
	 * 
	 * If required, put in NetworkManager constructor
	 * HttpsURLConnection.setDefaultHostnameVerifier(new DummyHostNameVerifier());
	 *
	 * @deprecated prototpye
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
