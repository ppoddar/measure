package com.nutanix.bpg.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathUtils {
	private static Logger logger = LoggerFactory.getLogger(ClasspathUtils.class);
	public static boolean isResource(String name) {
		return isResource(name, Thread.currentThread().getContextClassLoader());
	}
	public static boolean isResource(String name, ClassLoader cl) {
		return cl.getResource(name) != null;
	}
	
	public static InputStream getInputStream(String name) {
		return getInputStream(name, Thread.currentThread().getContextClassLoader());
	}
	
	public static InputStream getInputStream(String name, ClassLoader cl) {
		InputStream in =  cl.getResourceAsStream(name);
		if (in == null) {
			throw new IllegalArgumentException("no input stream for resource"
					+ " [" + name + "] found in classpath");
		}
		return in;
	}
	
	
	public static File getFile(String name) {
		return getFile(name, Thread.currentThread().getContextClassLoader());
	}
	
	public static DirectoryStream<Path> getDirectory(String name) {
		return getDirectory(name, Thread.currentThread().getContextClassLoader());
	}

	
	public static File getFile(String name, ClassLoader cl) {
		Path path = Paths.get(name);
		if (path.toFile().exists()) {
			return path.toFile();
		}
		
		URL url =  cl.getResource(name);
		if (url == null) {
			throw new IllegalArgumentException("no resource"
					+ "[" + name + "] found in classpath");
		}
		try {
			path = Paths.get(url.toURI());
			File file = path.toFile();
			return file;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		} catch (Exception ex) {
			throw new RuntimeException("can not get file " + name
					+ " due to following exception", ex);
		}
	}
	
	public static DirectoryStream<Path> getDirectory(String name, ClassLoader cl) {
		try {
			URL url = cl.getResource(name);
			try {
				Path dirPath = Paths.get(url.toURI());
				DirectoryStream<Path> dir = Files.newDirectoryStream(dirPath);
				return dir;
			} catch (Exception ex) {
				logger.warn("can not get directory from " + url
						+ " trying to look inside jar");
			}
			if (url != null) {
				InputStream stream = url.openStream();
				try {
					ZipInputStream zip = new ZipInputStream(stream);
					ZipEntry e = null;
					while ( (e = zip.getNextEntry()) != null) {
						logger.debug("-----> got entry " + e.getName());
					}
				} catch (Exception ex) {
					
				}
				
				Path path = Paths.get(url.toURI());
				return Files.newDirectoryStream(path);
			} else {
				throw new RuntimeException("can not get directory [" + name 
						+ "] beause " + name + " can not be resolved");
			}
		} catch (Exception ex) {
			throw new RuntimeException("can not get directory [" + name 
					+ "] due to following exception:", ex);
		}
	}

	
	public static URL getURL(String name, ClassLoader cl) {
		URL url =  cl.getResource(name);
		return url;
	}
	
	public static URL getURL(String name) {
		return getURL(name, Thread.currentThread().getContextClassLoader());
	}

}
