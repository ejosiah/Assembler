package com.jay;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import lombok.SneakyThrows;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * A generic assembler that assembles an object 
 * from another object. It works by searching the
 * classpath for an xml mapping file with this
 * naming convernsion fromClass#simpleName.toClass#simpleName.xml
 * then useses this mapping file to assemble the target object
 * 
 * @author jay
 *
 */
// TODO - handle Map & converter
@SuppressWarnings("all")
public class Assembler {
	
	private static final ConcurrentHashMap<String, List<Mapping>> mappingCache = new ConcurrentHashMap<>();
	
	public <FROM, TO> TO assemble(FROM from, Class<TO> toType){
		Document document = getDocument(from, toType);
		List<Mapping> mappings = retreiveMapping(document, getPath(from.getClass(), toType));
		
		Object toObject = null;
		try {
			Constructor constructor = toType.getConstructor(params(mappings));
			toObject = constructor.newInstance(args(from, mappings));
		} catch (Exception e) {
			throw new AssemblyException("error assemblying object of type" + toType 
					+ " from " + from.getClass(), e);
		} 
		return (TO)toObject;
	}
	
	private String getPath(Class fromType, Class toType){
		return "/" + fromType.getSimpleName() + "." + toType.getSimpleName() + ".xml";
	}
	
	@SneakyThrows
	private List<Mapping> retreiveMapping(Document document, String path){
		List<Mapping> mappings;
		if(mappingCache.containsKey(path)){
			mappings = mappingCache.get(path);
		}else{
			List<Element> elements = document.selectNodes("//mapping");
			mappings = new ArrayList<Mapping>();
			for(Element element : elements){
				
				String field = element.attributeValue("field");
				String type = element.attributeValue("type");
				String from = element.attributeValue("from");
				String converter = element.attributeValue("converter");
				String key = element.attributeValue("key");
				String value = element.attributeValue("value");
				
				mappings.add(new Mapping(field, type, converter, from, key, value));
				
			}
		}
		return mappings;
		
	}
	
	private Class[] params(List<Mapping> mappings){
		Class[] params = new Class[mappings.size()];
		for(int i = 0; i < mappings.size(); i++){
			params[i] = mappings.get(i).getType();
		}
		return params;
	}
	
	@SneakyThrows
	private Object[] args(Object source, List<Mapping> mappings){
		Object[] args = new Object[mappings.size()];
		for(int i = 0; i < args.length; i++){
			Mapping mapping = mappings.get(i);
			Object arg = null;
			Field field = field(source, mapping.getFieldName());
			if(mapping.getFrom() != null){
				Object from = field.get(source);
				if(mapping.isList()){
					arg = getValues(from, mapping);
				}else{					
					arg = assemble(from, mapping.getType());
				}
			}else if(mapping.getConverterType() != null){
				Object from = field.get(source);
				Converter converter = getConverter(mapping);
				arg = converter.convert(from);
			}else if(mapping.isMap()){
				Object from = field.get(source);
				arg = getMap(from, mapping);
			}else{
				arg = field.get(source);
			}
			args[i] = arg;
			
		}
		return args;
	}
	
	private Object getMap(Object from, Mapping mapping) {
		Map map = (Map)from;
		Map result = new HashMap();
		for(Entry entry : (Set<Entry>)map.entrySet()){
			Object key = assemble(entry.getKey(), mapping.getKeyPair().getB());
			Object value = assemble(entry.getValue(), mapping.getValuePair().getB());
			result.put(key, value);
		}
		return result;
	}

	@SneakyThrows
	private Converter getConverter(Mapping mapping){
		// TODO use spring injection if available
		Class converterType = mapping.getConverterType();
		return (Converter) converterType.newInstance();
	}
	
	@SneakyThrows
	private Object getValues(Object source, Mapping mapping){
		Collection collection = (Collection)source;
		Collection result = getCollectionInstance(mapping);
		for(Object obj : collection){
			Object member = assemble(obj, mapping.getListValueType());
			result.add(member);
		}
		return result;

	}
	
	@SneakyThrows
	private Collection getCollectionInstance(Mapping mapping){
		Class superType = mapping.getType();
		Class concreteType = null;
		if(List.class.isAssignableFrom(superType)){
			concreteType = ArrayList.class;
		}else if(Set.class.isAssignableFrom(superType)){
			concreteType = HashSet.class;
		}
		return (Collection) concreteType.newInstance();
	}
	
	@SneakyThrows
	private Field field(Object from, String name){
		Field field = from.getClass().getDeclaredField(name);
		field.setAccessible(true);
		Field modifier = field.getClass().getDeclaredField("modifiers");
		modifier.setAccessible(true);
		modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
		
	}
	
	@SneakyThrows
	private Class getClass(String className){
		return className != null ? Class.forName(className) : null;
	}
	
	@SneakyThrows
	private Document getDocument(Object from, Class toType){
		String path =  "/" + from.getClass().getSimpleName() + "." + toType.getSimpleName() + ".xml";
	//	System.out.println(path);
		InputStream source = getClass().getResourceAsStream(path);
		SAXReader reader = new SAXReader();
		return reader.read(source);
	}
}
