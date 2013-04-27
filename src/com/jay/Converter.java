package com.jay;

public interface Converter<FROM, TO> {
	
	TO convert(FROM obj);

}
