package org.khrys.util.logAnalyser.model.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.khrys.util.logAnalyser.exceptions.ParsingException;

public abstract class QueryBase {

	protected ArrayList<Variable> variables = new ArrayList<Variable>();
	
	protected QueryBase() {}
	
	public ArrayList<Variable> getVariables() {
		return variables;
	}

	public abstract Data parseData() throws ParsingException, FileNotFoundException, IOException;
	
}
