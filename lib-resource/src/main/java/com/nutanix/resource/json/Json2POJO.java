package com.nutanix.resource.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public class Json2POJO {
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
	void generateSource(
			String pkgName,
			String classname, 
			JsonNode node,
			SourcecodeModel model) {
		if (model.hasSourcecode(classname)) {
			return;
		}
		Sourcecode source = new Sourcecode();
		source.classname = new Sourcecode.ClassName(pkgName, classname);
		model.addSourcecode(source);
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode e = node.get(fieldName);
			if (e.isValueNode()) {
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
			} else if (e.isObject()) {
				generateSource(pkgName, classname, e, model);
			} else if (e.isContainerNode()) {
				for (JsonNode e1 : e) {
					generateSource(pkgName, classname, e1, model);
				}
			}
		}
	}
}
