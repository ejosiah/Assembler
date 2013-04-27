package com.jay;

import java.util.Date;

public class LongToDate implements Converter<Long, Date> {

	@Override
	public Date convert(Long obj) {
		return new Date(obj);
	}

}
