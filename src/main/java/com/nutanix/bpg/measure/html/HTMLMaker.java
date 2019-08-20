package com.nutanix.bpg.measure.html;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nutanix.bpg.measure.model.Database;
import com.nutanix.bpg.measure.model.Metrics;
import com.nutanix.bpg.measure.model.Named;
import com.nutanix.bpg.measure.model.PluginMetadata;

/**
 * Generates HTML output using freemaker and template.
 * 
 * @author pinaki.poddar
 *
 */

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

public class HTMLMaker {
	private static String TEMPLATES_DIR = "html-templates";
	private static Configuration cfg;
	private static Map<Class<?>, Template> templates;
	Logger logger = LoggerFactory.getLogger(HTMLMaker.class);
	
	static {
		cfg = new Configuration(new Version(2, 3, 20));
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setClassLoaderForTemplateLoading(Thread.currentThread()
				.getContextClassLoader(), TEMPLATES_DIR);
	
		templates = new HashMap<Class<?>, Template>();
		try {
			templates.put(Metrics.class,        cfg.getTemplate("metrics.ftl"));
			templates.put(PluginMetadata.class, cfg.getTemplate("plugin.ftl"));
			templates.put(Database.class,       cfg.getTemplate("database.ftl"));
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		} 
	}
	
	/**
	 * generates template for given class.
	 */
	public String generateHTML(Class<?> cls, Map<String, Object> inputs) throws Exception {
		Template template = findTemplate(cls);
		if (template == null) {
			return null;
		}
		logger.debug("generting HTML by Freemaker template " + cls);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(stream);
		template.process((Object) inputs, out);
		
		String htmlString = new String(stream.toByteArray());
		if (Boolean.getBoolean("test.html")) {
			File file = new File("test.html");
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(htmlString);
			}
			
		}
		return htmlString;
	}
	
	public String generateHTML(Named obj) throws Exception {
		Map<String, Object> inputs = createDisplayInputs(obj);
		if (inputs == null) {
			logger.warn("no input found for " + obj
					+ " html would not be produced");
			return null;
		}
		return generateHTML(obj.getClass(), inputs);
	}
	
	/**
	 * Gets name of a HTML Template for given class
	 * or null if none is recorded.
	 * @param cls
	 * @return
	 */
	
	public Template findTemplate(Class<?> c) {
		for (Class<?> cls : templates.keySet()) {
			if (cls.isAssignableFrom(c)) {
				return templates.get(cls);
			}
		}
		return null;
		
	}
	
	/**
	 * create inputs for display 
	 * 
	 * @param obj an instance of Metrics, PluginMetadata, Database
	 * @return null if object is not recognized
	 */
	public Map<String, Object> createDisplayInputs(Object obj) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		if (Metrics.class.isInstance(obj)) {
			Metrics m = Metrics.class.cast(obj);
			inputs.put("pojo", m);
			inputs.put("dimensions", m.getDimensions());
			return inputs;
		} else if (Database.class.isInstance(obj)) {
			Database db = Database.class.cast(obj);
			inputs.put("pojo", db);
			return inputs;
		} else if (PluginMetadata.class.isInstance(obj)) {
			PluginMetadata plugin = PluginMetadata.class.cast(obj);
			inputs.put("pojo", plugin);
			return inputs;
		} else {
			return null;
		}
	}



}
