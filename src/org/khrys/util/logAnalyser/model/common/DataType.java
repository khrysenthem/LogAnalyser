package org.khrys.util.logAnalyser.model.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public enum DataType {
	STRING (String.class), 
	CHARACTER (Character.class), 
	INTEGER (BigInteger.class), 
	DECIMAL (BigDecimal.class),
	DATE (Date.class)
	;
	
	
	private Class<?> clazz;
	
	private DataType(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<? >getClazz() {
		return clazz;
	}
}
