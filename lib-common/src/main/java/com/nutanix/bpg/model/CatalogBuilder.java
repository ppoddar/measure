package com.nutanix.bpg.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CatalogBuilder<T extends Named> 
	extends SimpleFileVisitor<Path> {
	private Factory<T> factory;
	private File root;
	Catalog<T> catalog;
	private static Logger logger = LoggerFactory.getLogger(CatalogBuilder.class);
	
	public CatalogBuilder<T> withFactory(Factory<T> factory) {
		this.factory = factory;
		return this;
	}
	
	public CatalogBuilder<T> withDirectory(Path dir) {
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
		if (factory == null) {
			throw new IllegalArgumentException("no factory to build a catalog");
		}
		if (root == null) {
			throw new IllegalArgumentException("no root directory to build a catalog");
		}
		catalog = new Catalog<T>();
		catalog.setName(factory.getType().getSimpleName());
		logger.info("building " + catalog);
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
				T e = factory.build(new FileInputStream(file));
				if (e != null) {
					catalog.add(e);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.info("built " + catalog + " of " + catalog.size() + " " + factory.getType().getSimpleName());
		
		return catalog;
	}
	
	
	
}
