package com.jay;

public class AssemblyException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AssemblyException(Throwable t){
		super(t);
	}
	
	public AssemblyException(String msg, Throwable t){
		super(msg, t);
	}
}
