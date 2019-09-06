package com.nutanix.bpg.measure.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * A {@link PluginMetadata} captures metadata for
 * a measurement process.
 * <p>
 * Captures two major aspects of any measurement process, namely,
 * <ul>
 * <li><em>what</em> to measure. It is described by 
 * {@link Metrics metrics}. 
 * 
 * <li><em>how</em> to measure. 
 * This aspect is specified <em>indirectly</em>. 
 * It is realized by {@link Plugin plug-in} created by
 * {@link #createPlugin() createPlugin()} method. 
 * <p>
 * For example, a {@link ScriptPluginMetadata plug-in} 
 * can measure by executing a parameterized OS/Python/PSQL 
 * script and is specified by name of the script and
 * script arguments.
 * </ul>
 * 
 * <p>
 * Theory of operation:<br>
 * <ul>
 * <li>A plug-in metadata is described in a a YAML descriptor.
 * <li>A plug-in metadata {@link #createPlugin(Map) creates} a plug-in instance.
 * <li>One or more {@link Callback callbacks} are to be 
 * attached to a plug-in before {@link Plugin#run(Map, Object) run}
 * <li>A plug-in runs to take a {@link Measurement}. 
 * <li>The measurement is {@link Observer#update(Observable, Object)
 * notified} to all registered {@link Callback}s.
 * It is left a callback what to do with the measurement.
 * </ul>
 * <p>
 * The measured quantities are notified to all registered observers.
 * One typical observer is a {@link MetricsSaver saves} the
 * measured quantities in a persistent storage for later analysis.
 * <p>
 * A {@link PluginMetadata} is identified by name.
 *
 * @author pinaki.poddar
 *
 */
@SuppressWarnings("serial")
public abstract class PluginMetadata implements Named, Serializable {
	private String name;
	private String description;
	private Metrics metrics;
	protected static Logger logger = LoggerFactory.getLogger(PluginMetadata.class);

	
	protected static final String PARAM_NAME        = "name";
	protected static final String PARAM_DESCRIPTION = "description";
	protected static final String PARAM_PARSER      = "parser";
	
	
	public PluginMetadata() {
		
	}
	
	
	public void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("null/empty plugin name");
		}
		this.name = name;
	}
	
	public List<String> getRequiredVariables() {
		return Collections.emptyList();
	}
	

	/**
	 * name of this plug-in metadata. 
	 * Plug-ins are looked up by this name
	 * from a {@link Catalog catalog}.
	 */
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? "" : description;
	}
	
	public Metrics getMetrics() {
		if (metrics == null) {
			throw new IllegalStateException("metrics is not resolved");
		}
		return metrics;
	}

	/**
	 * Construct and customizes a plug-in.
	 * 
	 * @param params map whose keys are
	 * parameters defined in this descriptor.
	 * The values are 
	 * @return
	 * @throws Exception
	 */
	
	
	public void setMetrics(Metrics m) {
		metrics = m;
	}

	
	/**
	 * configures this receiver. The implementation 
	 * must implement this method.
	 * @param json
	 */
	public void configure(JsonNode json) {
		name = json.get(PARAM_NAME).asText();
		description = json.path(PARAM_DESCRIPTION).asText();
		
	}
	
	public String toString() {
		return "plugin:" + getName();
	}

}
