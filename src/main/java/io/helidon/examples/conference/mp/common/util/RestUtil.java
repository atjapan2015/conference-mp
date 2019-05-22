package io.helidon.examples.conference.mp.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.json.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.tomitribe.auth.signatures.PEM;

import io.helidon.examples.conference.mp.common.util.Signing.RequestSigner;

public class RestUtil {

	public static String RestGet(String apiKey, String privateKeyFilename, String uri) {

		try {
			HttpRequestBase request;

			// This is the keyId for a key uploaded through the console
			PrivateKey privateKey = loadPrivateKey(privateKeyFilename);
			RequestSigner signer = new RequestSigner(apiKey, privateKey);

			request = new HttpGet(uri);
			signer.signRequest(request);

			HttpClient httpClient = HttpClientBuilder.create().build();

			HttpResponse response = httpClient.execute(request);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
					|| HttpStatus.SC_ACCEPTED == response.getStatusLine().getStatusCode()) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					return EntityUtils.toString(responseEntity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static String RestPost(String apiKey, String privateKeyFilename, String uri, byte[] bodyByte) {

		try {
			HttpRequestBase request;

			// This is the keyId for a key uploaded through the console
			PrivateKey privateKey = loadPrivateKey(privateKeyFilename);
			RequestSigner signer = new RequestSigner(apiKey, privateKey);

			request = new HttpPost(uri);
			HttpEntity entity = new ByteArrayEntity(bodyByte);
			((HttpPost) request).setEntity(entity);
			signer.signRequest(request);
			request.removeHeaders("content-length");

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(request);

			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
					|| HttpStatus.SC_ACCEPTED == response.getStatusLine().getStatusCode()) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					return EntityUtils.toString(responseEntity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static String RestPut(String apiKey, String privateKeyFilename, String uri, byte[] bodyByte) {

		try {
			HttpRequestBase request;

			// This is the keyId for a key uploaded through the console
			PrivateKey privateKey = loadPrivateKey(privateKeyFilename);
			RequestSigner signer = new RequestSigner(apiKey, privateKey);

			request = new HttpPut(uri);
			signer.signRequest(request);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(request);

			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
					|| HttpStatus.SC_ACCEPTED == response.getStatusLine().getStatusCode()) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					return EntityUtils.toString(responseEntity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static String RestDelete(String apiKey, String privateKeyFilename, String uri) {

		try {
			HttpRequestBase request;

			// This is the keyId for a key uploaded through the console
			PrivateKey privateKey = loadPrivateKey(privateKeyFilename);
			RequestSigner signer = new RequestSigner(apiKey, privateKey);

			request = new HttpDelete(uri);
			signer.signRequest(request);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(request);

			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()
					|| HttpStatus.SC_ACCEPTED == response.getStatusLine().getStatusCode()) {
				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					return EntityUtils.toString(responseEntity);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public static String[] RestHeaders(String apiKey, String privateKeyFilename, String method, String uri,
			JsonObject jsonObject) {

		String[] httpHeaders = new String[] { "", "", "", "", "" };

		HttpRequestBase request;

		// This is the keyId for a key uploaded through the console
		PrivateKey privateKey = loadPrivateKey(privateKeyFilename);
		RequestSigner signer = new RequestSigner(apiKey, privateKey);

		if (method.equals("get")) {
			request = new HttpGet(uri);
		} else if (method.equals("post")) {
			request = new HttpPost(uri);
			setentity(request, jsonObject);
		} else if (method.equals("put")) {
			request = new HttpPut(uri);
			setentity(request, jsonObject);
		} else if (method.equals("patch")) {
			request = new HttpPatch(uri);
			setentity(request, jsonObject);
		} else if (method.equals("delete")) {
			request = new HttpDelete(uri);
		} else {
			request = new HttpGet(uri);
		}

		signer.signRequest(request);

		httpHeaders[0] = request.getFirstHeader("x-date").getValue();
		httpHeaders[1] = request.getFirstHeader("Authorization").getValue();
		if (method.equals("post") || method.equals("put") || method.equals("patch")) {
			httpHeaders[2] = request.getFirstHeader("x-content-sha256").getValue();
			httpHeaders[3] = request.getFirstHeader("content-type").getValue();
			httpHeaders[4] = request.getFirstHeader("content-length").getValue();
		}

		return httpHeaders;
	}

	private static PrivateKey loadPrivateKey(String privateKeyFilename) {

		try (InputStream privateKeyStream = Files.newInputStream(Paths.get(privateKeyFilename))) {
			return PEM.readPrivateKey(privateKeyStream);
		} catch (InvalidKeySpecException e) {
			throw new RuntimeException("Invalid format for private key");
		} catch (IOException e) {
			throw new RuntimeException("Failed to load private key");
		}
	}

	private static void setentity(HttpRequestBase request, JsonObject jsonObject) {

		try {
			HttpEntity entity = new StringEntity(jsonObject.toString());
			((HttpPost) request).setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
