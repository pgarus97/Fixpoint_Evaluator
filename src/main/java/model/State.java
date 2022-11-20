package model;

import java.util.LinkedHashMap;

public class State {

	private LinkedHashMap<String,String> contentMap;

	public State() {
		contentMap = new LinkedHashMap<String,String>(); 
	}
	
	public LinkedHashMap<String,String> getContentMap() {
		return contentMap;
	}

	public void setContentMap(LinkedHashMap<String,String> contentMap) {
		this.contentMap = contentMap;
	}

	public String get(String key) {
		return contentMap.get(key);
	}
	
	public void put(String key, String value) {
		contentMap.put(key, value);
	}
	
}
