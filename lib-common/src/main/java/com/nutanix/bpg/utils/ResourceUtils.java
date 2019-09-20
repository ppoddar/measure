package com.nutanix.bpg.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class ResourceUtils {
	public static URL getURL(String path) {
		URL url = null;
		try {
		if (path.contains("://")) {
			url = new URL(path);
		} else {
			String cwd = System.getProperty("user.dir");
				url = new URL("file:///" + cwd + "/" + path);
		}
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return url;
	}
}
