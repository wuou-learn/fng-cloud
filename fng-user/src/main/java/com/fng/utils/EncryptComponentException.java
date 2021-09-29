package com.fng.utils;

import java.util.logging.Logger;

/**
 * EncryptComponentException
 */
public class EncryptComponentException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -207438168403761317L;
	
	private static final Logger LOGGER  =Logger.getLogger(EncryptComponentException.class.toString());
	public EncryptComponentException()
	{
		super();
	}
	public EncryptComponentException(String msg){
		super(msg);
		LOGGER.severe(msg);
	}
	
}
