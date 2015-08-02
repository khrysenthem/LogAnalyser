package org.khrys.util.logAnalyser.model.common;

public class Variable {
	int id = 0;
	DataType dataType;
	
	public Variable(int id, DataType dt) {
		this.id = id;
		this.dataType = dt;
	}

	public int getId() {
		return id;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	public String toString() {
		return "Variable id: "+id+"; dataType: "+dataType.name();
	}
}
