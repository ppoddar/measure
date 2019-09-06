package com.nutanix.bpg.measure.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CatalogBuilder<T extends Named> 
	extends SimpleFileVisitor<Path> {
	private Factory<T> factory;
	private List<String> extensions = new ArrayList<>();
	private DirectoryStream<Path> root;
	Catalog<T> catalog;
	private static Logger logger = LoggerFactory.getLogger(CatalogBuilder.class);
	
	public CatalogBuilder<T> withFactory(Factory<T> factory) {
		this.factory = factory;
		return this;
	}
	
	public CatalogBuilder<T> withDirectory(DirectoryStream<Path> dir) {
		if (dir == null) {
			throw new IllegalArgumentException("can not set null file/directory for catalog builder");
		}
		
		root = dir;
		
		return this;
	}
	public CatalogBuilder<T> withExtension(String ext) {
		if (extensions==null) extensions = new ArrayList<String>();
		extensions.add(ext);
		return this;
	}
	
	
	
	boolean isSelected(Path path) {
		String name = path.getName(path.getNameCount()-1).toString();
		boolean selected = name.endsWith(".yml");
		if (!selected) {
			logger.warn("ignored " + name);
		}
		return selected;
	}
	
	/**
	 * walks file system from a directory, 
	 * builds an element from every selected file
	 * and adds the to a catalog.
	 * 
	 * @return
	 * @throws Exception
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
		for (Path path : root) {
			if (!isSelected(path)) continue;
			try {
				T e = factory.build(Files.newInputStream(path));
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
