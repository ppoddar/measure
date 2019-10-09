package com.nutanix.bpg.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.nutanix.bpg.utils.Named;


public class CatalogBuilder<T extends Named> 
	extends SimpleFileVisitor<Path> {
	private Factory<T> factory;
	private File root;
	private static Logger logger = LoggerFactory.getLogger(CatalogBuilder.class);
	
	public CatalogBuilder<T> withFactory(Factory<T> factory) {
		this.factory = factory;
		return this;
	}
	/**
	 * path to a root directory.
	 * All files under this directory will be processed.
	 * 
	 * @param dir
	 * @return
	 */
	public CatalogBuilder<T> withDirectory(Path dir) {
		if (dir == null) {
			logger.warn("can not build catalog with null directory");
			return this;
		}
		if (!dir.toFile().exists()) {
			logger.warn("can not build catalog with non-existent directory " 
					+ dir.toUri());
			return this;
		}
		root = dir.toFile();
		return this;
	}
	
	/**
	 * walks file system from a directory, 
	 * builds an element from every selected file
	 * and adds the to a catalog.
	 * 
	 * @return a catalog
	 * @throws IllegalArgumentException when things go wrong
	 */
	public Catalog<T> build() {
		Catalog<T> catalog = new Catalog<T>();
		if (factory == null) {
			logger.warn("no factory to build a catalog. An empty catalog woud be built");
			return catalog;
			
		}
		if (root == null) {
			logger.warn("no root directory to build a catalog");
			return catalog;
		}
		catalog.setName(factory.getType().getSimpleName());
		logger.info("building " + catalog + " reading from " + root.getAbsolutePath());
		Stack<File> files = new Stack<>();
		files.push(root);
		while (!files.isEmpty()) {
			File file = files.pop();
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					files.push(f);
				}
				continue;
			}
			if (!file.getName().endsWith(".yml")) continue;
			try {
				logger.debug("building " + factory.getType().getSimpleName()
						+ " from " + file);
				T e = factory.build(new FileInputStream(file));
				if (e != null) {
					catalog.add(e);
				}
			} catch (JsonMappingException ex) {
				logger.warn("error reading " + file
						+ " check if file content is valid JSON");
			} catch (IOException e) {
				logger.warn("error reading " + file
						+ " check if file content is valid JSON");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("built " + catalog + " of " + catalog.size() + " " + factory.getType().getSimpleName());
		
		return catalog;
	}
	
	
	
}
