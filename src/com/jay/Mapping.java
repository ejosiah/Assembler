package com.jay;

import lombok.Data;
import lombok.SneakyThrows;

@Data
@SuppressWarnings("rawtypes")
public class Mapping {
	private final String fieldName;
	private final Class type;
	private final Class converterType;
	private final Class from;
	private final Class listValueType;
	private final Pair<Class, Class> keyPair;
	private final Pair<Class, Class> valuePair;
	
	@SneakyThrows
	public Mapping(String field, String type, String converter, String from, String key, String value){
		fieldName = field;
		this.type = resolveType(type);
		converterType = converter != null ? Class.forName(converter) : null;
		this.from = from != null ? Class.forName(from) : null;
		this.keyPair = resolveKeyPair(key, type);
		this.valuePair = resolveValuePair(value, type);
		this.listValueType = resolveListType(type);
	}
	
	@SneakyThrows
	private Pair<Class, Class> resolveKeyPair(String key, String type) {
		Pair<Class, Class> pair = null;
		if(key != null){
			String k = type.substring(type.indexOf("[") + 1, type.indexOf(","));
			pair = new Pair<Class, Class>(Class.forName(key), Class.forName(k));
		}
		return pair;
	}
	
	@SneakyThrows
	private Pair<Class, Class> resolveValuePair(String value, String type) {
		Pair<Class, Class> pair = null;
		if(value != null){
			String v = type.substring(type.indexOf(",") + 1, type.indexOf("]")).trim();
			pair = new Pair<Class, Class>(Class.forName(value), Class.forName(v));			
		}
		return pair;
	}

	@SneakyThrows
	private Class resolveType(String className){
		int end = className.indexOf("[");
		end = end == -1 ? className.length() : end;
		className = className.substring(0, end);
		return Class.forName(className);
	}
	
	@SneakyThrows
	private Class resolveListType(String className) {
		Class listValueType = null;
		if(className.contains("[") && !className.contains(",")){
			int from = className.indexOf("[") + 1;
			int to = className.lastIndexOf("]");
			String typeName = className.substring(from, to);
			listValueType = Class.forName(typeName);
		}
		return listValueType;
	}
	
	public boolean isMap(){
		return keyPair != null && valuePair != null;
	}
	
	public boolean isList(){
		return listValueType != null;
	}

}
