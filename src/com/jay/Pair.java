package com.jay;

import lombok.Data;

@Data
public class Pair<A, B> {
	private final A a;
	private final B b;
}
