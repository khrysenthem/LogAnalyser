package org.khrys.util.logAnalyser.model.commandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.khrys.util.logAnalyser.exceptions.ParsingException;
import org.khrys.util.logAnalyser.model.common.Data;
import org.khrys.util.logAnalyser.model.common.DataType;
import org.khrys.util.logAnalyser.model.common.QueryBase;
import org.khrys.util.logAnalyser.model.common.Variable;

public class Query extends QueryBase {
	private HashMap<QueryOption, List<String>> options = new HashMap<QueryOption, List<String>>();
	private ArrayList<String> searchStringParts = new ArrayList<String>();
	
	protected Query() {
		super();
	}
	
	private void addOptions(QueryOption option, List<String> values) {
		this.options.put(option, values);
	}

	public HashMap<QueryOption, List<String>> getOptions() {
		return options;
	}

	public static Query getQuery(String[] args) throws ParsingException {
		Query query = new Query();
		try {
			for (int i=0;i<args.length;i++) {
				QueryOption queryOption = QueryOption.getQueryOption(args[i]);
				if (queryOption==null) {
					throw new ParsingException("Command not valid: '"+args[i]+"'");
				}
//				System.out.println("queryoption: "+queryOption.name());
				ArrayList<String> queryOptionValues = new ArrayList<String>();
				for (int j=0; j< queryOption.getNbOfParameters(); j++) {
					i++;
					queryOptionValues.add(args[i]);
//					System.out.println("value["+j+"]: "+args[i]);
				}
				query.addOptions(queryOption, queryOptionValues);
			}
		} catch(Exception e) {
			throw new ParsingException("Invalid/Missing argument");
		}
		
		return query;
	}
	
	@Override
	public Data parseData() throws ParsingException, FileNotFoundException, IOException {
		// ----------------------------
		// check the file
		String filename = null;
		if (options.get(QueryOption.FILE) == null || options.get(QueryOption.FILE).isEmpty()) {
			throw new ParsingException("filename was missing");
		}
		filename = options.get(QueryOption.FILE).get(0);
		File file = new File(filename);
		
		// ----------------------------
		// prepare the search string
		if (options.get(QueryOption.PRINTFSTYLESEARCH) == null || options.get(QueryOption.PRINTFSTYLESEARCH).isEmpty()) {
			throw new ParsingException("search string was missing");
		}

		// ----------------------------
		// prepare Variables
		prepareVariables();
		
		// ----------------------------
		// prepare Data
		Data data = prepareData();
		
		// ----------------------------
		// line by line
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		String line;
	    while ((line = br.readLine()) != null) {
	    	// process the line.
	    	scanLine(data, line);
	    }
	    br.close();
	    fileReader.close();
	    //----------------------------
		
		
		return data;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void scanLine(Data data, String line) {
		// the search string doesn't need to start at char 0 of the line
		int firstIndex = line.indexOf(searchStringParts.get(0)); 
		if (firstIndex < 0)
			return;
		int searchStringIndex = 0;
		HashMap<Variable, ArrayList> lineScanResult = new HashMap<Variable, ArrayList>();
		line = line.substring(firstIndex);
		System.out.println("initial line: \""+line+"\"");
		while (line.length() > 0 && searchStringIndex < this.variables.size() && line.startsWith(searchStringParts.get(searchStringIndex))) {
			line = line.substring(searchStringParts.get(searchStringIndex).length());
			System.out.println("line: \""+line+"\"");
			int toScanPartEnd = line.length();
			if (searchStringParts.size() > searchStringIndex+1) {
				toScanPartEnd = line.indexOf(searchStringParts.get(searchStringIndex+1));
				System.out.println("after if; line: \""+line+"\"");
			}
			if (toScanPartEnd == -1) {
				System.out.println("couldn't find the next search string");
				return;		// couldn't find the next string
			}
			// now to scan the interesting bit
			String sValue = line.substring(0, toScanPartEnd);
			System.out.println("String to parse: "+sValue);
			// parse
			Variable tempVar = this.variables.get(searchStringIndex);
			Object value = parseValue(sValue, tempVar);			
			if (value == null)
				return;
			System.out.println("identified variable: "+tempVar.toString()+"; value: "+value.toString());

			// store
			ArrayList tempList = new ArrayList();
			tempList.add(value);
			lineScanResult.put(tempVar, tempList);
			// next
			line = line.substring(toScanPartEnd);
			searchStringIndex++;
		}
		if (searchStringIndex == searchStringParts.size()-1 ) {
			// we have all the parts we need
			// the search string doesn't need to end the line
			for (Entry<Variable, ArrayList> entry: lineScanResult.entrySet()) {
				data.addData(entry.getKey(), entry.getValue());
			}
		} else {
			System.out.println("Skipped line results because searchStringIndex count was wrong ; searchStringIndex: "+searchStringIndex+"; expected: "+(searchStringParts.size()-1));
		}
	}

	private Object parseValue(String sValue, Variable v) {
		Object value = null;
		try {
			if (v.getDataType().getClazz().isAssignableFrom(String.class)) {
				value = sValue;
			}
			if (v.getDataType().getClazz().isAssignableFrom(BigDecimal.class)) {
				value = new BigDecimal(sValue);
			}
			if (v.getDataType().getClazz().isAssignableFrom(BigInteger.class)) {
				value = new BigInteger(sValue);
			}
			if (v.getDataType().getClazz().isAssignableFrom(Character.class)) {
				if (sValue.length()==1)
					value = (Character) sValue.charAt(0);
			}
		} catch (Exception e) {
			// nothing, we couldn't parse and that's it
		}
		return value;
	}

	private void prepareVariables() {
		String scanfstring = this.options.get(QueryOption.PRINTFSTYLESEARCH).get(0);
		int index = -1;
		int i = 0;
		int partStart = 0;
		int partEnd = 0;
		while ((index=scanfstring.indexOf("%", partStart))>=0) {
			char variabbleType = scanfstring.charAt(index+1);
			Variable v = null;
			switch(variabbleType) {
				case 'c':
					v = new Variable(i, DataType.CHARACTER);
					break;
				case 'd':
					v = new Variable(i, DataType.INTEGER);
					break;
				case 'f':
					v = new Variable(i, DataType.DECIMAL);
					break;
				case 's':
					v = new Variable(i, DataType.STRING);
				default:
					break;
			}
			if (v!=null) {
				this.variables.add(v);
//				System.out.println("found variable "+v.toString());
				partEnd = index;
				String tempSub = scanfstring.substring(partStart, partEnd);
				searchStringParts.add(tempSub);
//				System.out.println("added tempSub: \""+tempSub+"\"");
				partStart = partEnd+2;
				i++;
			}
		}
		String lastbit = scanfstring.substring(partStart);
		if (lastbit.length() > 0) {
			searchStringParts.add(lastbit);
//			System.out.println("added tempSub: \""+lastbit+"\"");
		}
	}

	private Data prepareData() {
		Data data = null;
		HashMap<Variable, ArrayList<?>> dataMap = new HashMap<Variable, ArrayList<?>>();
		for (Variable v: this.variables) {
			if (v.getDataType().getClazz().isAssignableFrom(String.class)) {
				ArrayList<String> dataList = new ArrayList<String>();
				dataMap.put(v, dataList);
			}
			if (v.getDataType().getClazz().isAssignableFrom(BigDecimal.class)) {
				ArrayList<BigDecimal> dataList = new ArrayList<BigDecimal>();
				dataMap.put(v, dataList);
			}
			if (v.getDataType().getClazz().isAssignableFrom(BigInteger.class)) {
				ArrayList<BigInteger> dataList = new ArrayList<BigInteger>();
				dataMap.put(v, dataList);
			}
			if (v.getDataType().getClazz().isAssignableFrom(Character.class)) {
				ArrayList<Character> dataList = new ArrayList<Character>();
				dataMap.put(v, dataList);
			}
		}
		data = new Data(dataMap);
		return data;
	}
	
	/*
	 * for testing purposes
	 */
	public static void main(String[] args) {
		String s = "here is a test line";
		int toScanPartEnd = s.length();
		s = s.substring(0, s.length());
		System.out.println(s);
		
	}
}
