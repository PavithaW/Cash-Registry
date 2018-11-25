/**
 * Copyright (C) 2015 - Tharaka Nirmana
 *
 */
package com.cbasolutions.cbapos.service;


import android.content.Context;
import android.util.Log;

import com.cbasolutions.cbapos.helper.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.security.KeyStore;

import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * @author Tharaka Nirmana
 * loopj service client class: used in doing web service communication
 */
public class PayableClient {

	//http://stackoverflow.com/questions/11573108/self-signed-certificate-and-loopj-for-android
	private static AsyncHttpClient client = new AsyncHttpClient();



	public static void get(String url, String token, JsonHttpResponseHandler responseHandler) {
		client.setTimeout(Config.DEFAULT_TIMEOUT);
		String header1 = "Token " + token;
		client.addHeader("Authorization", header1);
		client.get(getAbsoluteUrl(url), responseHandler);
		createSelfSingedCert();
		client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
	}

	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(Config.DEFAULT_TIMEOUT);
		Log.e("Params", params + "");
		createSelfSingedCert();
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}



	public static void post(String url, String header, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(Config.DEFAULT_TIMEOUT);

		String header1 = "Token " + header;
		client.addHeader("Authorization", header1);
		String url1 = getAbsoluteUrl(url);
		createSelfSingedCert();
		client.post(url1, responseHandler);
	}

	public static void postWithAuth(String url, String header, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(Config.DEFAULT_TIMEOUT);

		String header1 = "Token " + header;
		client.addHeader("Authorization", header1);
		String url1 = getAbsoluteUrl(url);
		createSelfSingedCert();
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}



	public static void post(String url, AsyncHttpResponseHandler responseHandler) {
		client.setTimeout(Config.DEFAULT_TIMEOUT);
		createSelfSingedCert();
		client.post(getAbsoluteUrl(url), responseHandler);
	}

	private static String getAbsoluteUrl(String url) {
		return Config.SERVICE_URL + url;
	}

	private static void createSelfSingedCert(){
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			client.setSSLSocketFactory(sf);
		}
		catch (Exception e) {}
	}



	public static void post(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			client.setSSLSocketFactory(sf);
		}
		catch (Exception e) {
		}

//		try {
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(null, null);
//			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//			sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			client.setSSLSocketFactory(sf);
//		}
//		catch (Exception e) {}


		//client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());


		client.setTimeout(Config.DEFAULT_TIMEOUT);
//		client.addHeader(Config.AUTH_HEADER_KEY, Config.AUTH_HEADER_VALUE);
//		client.addHeader(Config.USER_AGENT_KEY, Config.USER_AGENT_VALUE);
		String url1 = getAbsoluteUrl(url);
		client.post(context, url1, entity,  "application/json", responseHandler);
	}


}
