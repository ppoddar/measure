package com.nutanix.resource.prism;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
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
	private String host     = null;
	private int port        = 9440;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final Logger logger = LoggerFactory.getLogger(PrismGateway.class);
	
	public static void main(String[] args) throws Exception {
		String host = "tomahawk-v1.eng.nutanix.com";
		String path = "clusters";
		JsonNode json = new PrismGateway(host).getResponse(path);
		mapper.writerWithDefaultPrettyPrinter()
		.writeValue(System.out, json);
		
	}
	
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
	
	public PrismGateway(String host) {
		this(host, 9440);
	}
	
	public PrismGateway(String host, int port) {
		this.host = host;
		this.port = port;
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
		String uri = protocol + "://"
				+ host + ":" + port
				+ "/" + base + "/"
				+ version + "/" + path;
		URL url = new URL(uri);
		
		HttpURLConnection con = (HttpURLConnection)
				url.openConnection();
		con.setRequestProperty(ACCEPT, APPLICATION_JSON);
		String username = "admin";
		String password = "Nutanix.1";
		String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes(StandardCharsets.UTF_8)); 
		con.setRequestProperty("Authorization", "Basic "+encoded);
		
		logger.debug("Prism request:" + url);
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