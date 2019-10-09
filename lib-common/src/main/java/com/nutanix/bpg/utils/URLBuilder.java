package com.nutanix.bpg.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class URLBuilder {
	private String scheme = "http";
	private String host;
	private int port;
	private String path;
	private Map<String, String> queryParams;
	
	public URLBuilder withScheme(String s) {
		scheme = s;
		return this;
	}
	public URLBuilder withHost(String h) {
		host = h;
		return this;
	}
	public URLBuilder withPath(String p) {
		if (p.startsWith("/")) {
			throw new IllegalArgumentException("path [" + p + "]"
					+ " must not begin with /");
		}
		if (!p.endsWith("/")) {
			throw new IllegalArgumentException("path [" + p + "]"
					+ " must end with /");
		}
		path = p;
		return this;
	}
	public URLBuilder withPort(int p) {
		port = p;
		return this;
	}
	
	public URLBuilder withQueryParams(String key, String value) {
		if (queryParams == null) {
			queryParams = new LinkedHashMap<>();
		}
		queryParams.put(key, value);
		
		return this;
	}

	
	public String build() {
		String url = "";
		url += scheme + "://";
		url += host + ":" + port;
		url += "/" + path;
		
		String query = "";
		if (queryParams != null) {
			for (Map.Entry<String, String> e : queryParams.entrySet()) {
				String prefix = query.isEmpty() ? "?":"&";
				query +=  prefix + e.getKey() + "=" + e.getValue();
			}
		}
		url += query;
		
		return url;
	}
	
}
