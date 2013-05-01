package com.jay;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
		
		Object toObject = null;
		try {
			List<Mapping> mappings = retreiveMapping(from.getClass(), toType);
			Constructor constructor = toType.getConstructor(params(mappings));
			toObject = constructor.newInstance(args(from, mappings));
		} catch (Exception e) {
			throw new AssemblyException("error assemblying object of type" + toType 
					+ " from " + from.getClass(), e);
		} 
		return (TO)toObject;
	}
	
	private List<Mapping> retreiveMapping(Class fromType, Class toType)  throws Exception{
		List<Mapping> mappings;
		String path = getPath(fromType, toType);
		if(mappingCache.containsKey(path)){
			mappings = mappingCache.get(path);
		}else{
			Document document = getDocument(path);
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
			mappingCache.put(path, mappings);
		}
		return mappings;
		
	}
	
	private String getPath(Class fromType, Class toType){
		return "/" + fromType.getSimpleName() + "." + toType.getSimpleName() + ".xml";
	}
	
	private Document getDocument(String path) throws Exception{
		InputStream source = getClass().getResourceAsStream(path);
		SAXReader reader = new SAXReader();
		return reader.read(source);
	}
	
	private Class[] params(List<Mapping> mappings){
		Class[] params = new Class[mappings.size()];
		for(int i = 0; i < mappings.size(); i++){
			params[i] = mappings.get(i).getType();
		}
		return params;
	}
	
	private Object[] args(Object source, List<Mapping> mappings) throws Exception{
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
	
	private Field field(Object from, String name) throws Exception{
		Field field = from.getClass().getDeclaredField(name);
		field.setAccessible(true);
		Field modifier = field.getClass().getDeclaredField("modifiers");
		modifier.setAccessible(true);
		modifier.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		return field;
		
	}
		
	private Object getValues(Object source, Mapping mapping) throws Exception{
		Collection collection = (Collection)source;
		Collection result = (Collection) source.getClass().newInstance();
		for(Object obj : collection){
			Object member = assemble(obj, mapping.getListValueType());
			result.add(member);
		}
		return result;

	}
	
	private Converter getConverter(Mapping mapping) throws Exception{
		// TODO use spring injection if available
		Class converterType = mapping.getConverterType();
		return (Converter) converterType.newInstance();
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

}
