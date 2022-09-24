package model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Fixpoint {
	
	private LinkedHashMap<String,String> contentMap;

	private String contentString;
	
	//empty standard constructor
	public Fixpoint() {
		contentMap = new LinkedHashMap<String,String>();
		contentString = "";
	}
	
	//constructor from given fixpoint string
	public Fixpoint(String contentString) {
		contentMap = new LinkedHashMap<String,String>();
		this.contentString = contentString;
		setMapFromString();
	}
	
	//constructor from given fixpoint map
		public Fixpoint(LinkedHashMap<String,String> contentMap) {
			this.contentMap = contentMap;
			setContentString(contentString);
			setStringFromMap();
		}
	
	
	/*
	 * Function that transforms a fixpoint from a mathematical iff-term format into a map.
	 */
	public LinkedHashMap<String,String> setMapFromString() {
		String tempString = contentString;
		tempString = tempString.substring(4,tempString.length()-1);
		tempString += ";";
		System.out.println("Fixpoint: "+tempString);

		while(tempString.length()>0) {
			String identifier = tempString.substring(0,tempString.indexOf(","));
			System.out.println("Id: " + identifier);
			tempString = tempString.substring(identifier.length()+1);
			String value = tempString.substring(0,tempString.indexOf(";"));
			System.out.println("Value: " + value);
			contentMap.put(identifier, value);
			tempString = tempString.substring(value.length()+1);
		}	
		return contentMap;
	}
	
	/*
	 * Function that transforms a fixpoint from map format into a mathematical iff-term format.
	 */
	public String setStringFromMap() {
		contentString = "iff(";
		for(Map.Entry<String, String> entry : contentMap.entrySet()) {
			contentString += ";" + entry.getKey()+","+entry.getValue();
		}
		contentString = contentString.replaceFirst(";", "");
		contentString += ")";
		return contentString;
	}
	
	public void addContentFromMap(String identifier, String value) {
		contentMap.put(identifier, value);
	}
	
	
	public LinkedHashMap<String, String> getContentMap() {
		return contentMap;
	}



	public void setContentMap(LinkedHashMap<String, String> contentMap) {
		this.contentMap = contentMap;
	}



	public String getContentString() {
		return contentString;
	}



	public void setContentString(String contentString) {
		this.contentString = contentString;
	}

}
