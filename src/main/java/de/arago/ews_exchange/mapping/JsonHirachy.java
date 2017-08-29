package de.arago.ews_exchange.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHirachy {

	private static final Logger log = LoggerFactory.getLogger(JsonHirachy.class); 
	
	public JsonHirachy(int level, AttributeFormat format, String name) {
		this.level = level;
		this.format = format;
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	@Override
	public String toString() {
		return "JsonHirachy [level=" + level + ", format=" + format + ", name=" + name + "]";
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public AttributeFormat getFormat() {
		return format;
	}
	public void setFormat(AttributeFormat format) {
		this.format = format;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private int level; 
	private AttributeFormat format; 
	private String name; 
	

}
