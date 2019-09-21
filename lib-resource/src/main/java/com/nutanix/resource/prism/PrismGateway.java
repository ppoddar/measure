package com.nutanix.resource.prism;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutanix.resource.model.Cluster;


/**
 * A HTTPS connection to Prism.
 * 
 * @author pinaki.poddar
 *
 */

public class PrismGateway {
	private String base     = "PrismGateway/services/rest";
	private String version  = "v2.0";
	private String protocol = "https";
	Cluster cluster;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final Logger logger = LoggerFactory.getLogger(PrismGateway.class);
	
//	public static void main(String[] args) throws Exception {
//		String host = "tomahawk-v1.eng.nutanix.com";
//		String path = "clusters";
//		JsonNode json = new PrismGateway(host).getResponse(path);
//		mapper.writerWithDefaultPrettyPrinter()
//		.writeValue(System.out, json);
//	}
	
	static {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			TrustManager[] tm = new TrustManager[] {
					new AllTrustManager()
			};
			sc.init(null, tm, new SecureRandom());
			HttpsURLConnection.setDefaultHostnameVerifier(new AllHostNameVarifier());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
				
	}
	
	public PrismGateway(Cluster cluster) {
		this.cluster = cluster;
		
	}
	
	URL getURLForPath(String path) {
		String uri = protocol + "://"
				+ cluster.getHost() + ":" 
				+ cluster.getPort()
				+ "/" + base + "/"
				+ version + "/" + path;
		URL url;
		try {
			url = new URL(uri);
			return url;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void verifyConnection() {
		try {
			getResponse("/");
		} catch (UnknownHostException ex) {
			throw new RuntimeException("host [" + cluster.getHost() + "]"
					+ " is not recognized. Check if you can "
					+ " conncet to cluster", ex);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Gets JSON response of given path.
	 * 
	 * @param path a URL GET path 
	 * @return response parsed as JSON
	 * 
	 * @throws Exception
	 */
	public JsonNode getResponse(String path) throws Exception {
		URL url = getURLForPath(path);
		HttpURLConnection con = (HttpURLConnection)
				url.openConnection();
		con.setRequestProperty(ACCEPT, APPLICATION_JSON);
		String encoded = Base64.getEncoder()
				.encodeToString((cluster.getUser()+":"+cluster.getPassword())
				.getBytes(StandardCharsets.UTF_8)); 
		con.setRequestProperty("Authorization", "Basic "+encoded);
		
		logger.info("Prism request:" + url);
		InputStream in = con.getInputStream();
		return mapper.readTree(in);
	}
	

	/**
	 * A dummy trust manager that trusts everybody
	 */
	public static class AllTrustManager extends X509ExtendedTrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
			
		}

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
				throws CertificateException {
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {
			
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2)
				throws CertificateException {
			
		}
	};
	
	public static class AllHostNameVarifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
}