package com.nutanix.resource.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SourcecodeModel implements Iterable<Sourcecode>{
	private Map<String, Sourcecode> model
		= new HashMap<String, Sourcecode>();
	
	public void addSourcecode(Sourcecode code) {
		model.put(code.getClassname(), code);
	}
	
	public boolean hasSourcecode(String classname) {
		return model.containsKey(classname);
	}
	
	public Sourcecode getSourcecode(String classname) {
		return model.get(classname);
	}

	@Override
	public Iterator<Sourcecode> iterator() {
		return model.values().iterator();
	}

}
