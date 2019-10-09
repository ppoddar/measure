package com.nutanix.resource.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.nutanix.resource.json.Sourcecode.Field;
/**
 * Generates Java source code from JSON.
 * 
 * @author pinaki.poddar
 *
 */
public class Json2POJO {
	/**
	 * Generates Java source code
	 * 
	 * @param pkgName package name
	 * @param classname class name
	 * @param node JSON node
	 * @return a model as container of source code
	 */
	public SourcecodeModel generateModel(
			String pkgName,
			String classname, 
			JsonNode node) {
		SourcecodeModel model = new SourcecodeModel();
		generateSource(pkgName, classname, node, model);
		return model;
	}
	
	/**
	 * generate source code from given json node
	 * @param node
	 */
	Sourcecode generateSource(
			String pkgName,
			String classname, 
			JsonNode node,
			SourcecodeModel model) {
		if (model.hasSourcecode(classname)) {
			return model.getSourcecode(classname);
		}
		Sourcecode source = new Sourcecode();
		source.setClassname(pkgName + '.' +  classname);
		model.addSourcecode(source);
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode e = node.get(fieldName);
			if (e.isValueNode()) {
				addField(source, e, fieldName);
			} else if (e.isObject()) {
				String fieldClass = Field.capitalize(fieldName);
				source.addField(fieldName, fieldClass);
				generateSource(pkgName, fieldClass, e, model);
			} else if (e.isContainerNode()) {
				for (JsonNode e1 : e) {
					generateSource(pkgName, fieldName, e1, model);
				}
			}
		}
		return source;
	}
	
	void addField(Sourcecode source, JsonNode e, String fieldName) {
		if (e.isBigDecimal()) {
			source.addField(fieldName, BigDecimal.class);
		} else if (e.isBigInteger()) {
			source.addField(fieldName, BigInteger.class);
		} else if (e.isBoolean()) {
			source.addField(fieldName, Boolean.class);
		} else if (e.isInt()) {
			source.addField(fieldName, Boolean.class);
		} else if (e.isLong()) {
			source.addField(fieldName, Long.class);
		} else if (e.isDouble()) {
			source.addField(fieldName, Long.class);
		} else if (e.isTextual()) {
			source.addField(fieldName, String.class);
		} 
	}
}
