package com.nutanix.resource.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * container of source code.
 * works as a closure group.
 * 
 * @author pinaki.poddar
 *
 */
public class SourcecodeModel implements Iterable<Sourcecode>{
	private Map<String, Sourcecode> model
		= new HashMap<String, Sourcecode>();
	
	/**
	 * adds given source code.
	 *  
	 * @param code
	 */
	public void addSourcecode(Sourcecode code) {
		model.put(code.getClassname(), code);
	}
	
	/**
	 * affirms if source code exists for given fully
	 * qualified class name
	 * @param classname
	 * @return
	 */
	public boolean hasSourcecode(String classname) {
		return model.containsKey(classname);
	}
	
	/**
	 * gets source code for given fully
	 * qualified class name, or null
	 * @param classname
	 * @return
	 */
	public Sourcecode getSourcecode(String classname) {
		return model.get(classname);
	}

	@Override
	public Iterator<Sourcecode> iterator() {
		return model.values().iterator();
	}

}
