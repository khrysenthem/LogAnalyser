package org.khrys.util.logAnalyser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.khrys.util.logAnalyser.exceptions.ParsingException;
import org.khrys.util.logAnalyser.model.commandLine.Query;
import org.khrys.util.logAnalyser.model.commandLine.QueryOption;
import org.khrys.util.logAnalyser.model.common.Data;
import org.khrys.util.logAnalyser.model.common.Stats;
import org.khrys.util.logAnalyser.model.common.Variable;
import org.khrys.util.logAnalyser.model.common.utils.Constants;

public class LogAnalyser {

	public static void main(String[] args) {
		if (args==null) 
			return;
		try {
			Query query = Query.getQuery(args);
			
			if (query.getOptions().containsKey(QueryOption.HELP)) {
				getHelp();
				return;
			}
			
			if (query.getOptions().containsKey(QueryOption.VERSION)) {
				getVersion();
				return;
			}
			
			Data data = query.parseData();
			if (data == null)
				System.out.println("data null");
			else
				System.out.println("data parsed");
			
			if (query.getOptions().containsKey(QueryOption.OUTPUTFILE)) {
				writeDataToOutput(query, data);
				System.out.println("files written");
				return;
			}
			
			HashMap<Variable, Stats> stats = data.getStats();
			if (stats == null)
				System.out.println("data stats null");
			else {
				System.out.println("data stats done");
				// TODO: select output
				for (Entry<Variable, Stats> entry: stats.entrySet()) {
					String sKey = ((Variable) entry.getKey()).toString();
					String sValue = ((Stats) entry.getValue()).toString();
					System.out.println(sKey);
					System.out.println(sValue);
				}
			}
		} catch (ParsingException pe) {
			System.out.println("Sorry, input was invalid");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return;
	}

	private static void writeDataToOutput(Query query, Data data) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO
		String outputdir = query.getOptions().get(QueryOption.OUTPUTFILE).get(0);
		// for each variable
		for(Entry<Variable, ArrayList<?>> entry: data.getData().entrySet()) {
			String variableName = "Variable_"+entry.getKey().getId()+"_"+entry.getKey().getDataType();
			System.out.println("exporting "+variableName);
			PrintWriter writer = new PrintWriter(outputdir+"/"+variableName+".csv", "UTF-8");
			for (Object o: entry.getValue()) {
				writer.println(o.toString());
			}
			writer.close();
		}
	}

	private static void getVersion() {
		System.out.println(Constants.PRODUCT_NAME+" v"+Constants.PRODUCT_VERSION);
	}

	private static void getHelp() {
		System.out.println(Constants.PRODUCT_NAME+" aims to provide a quick statistical view of a particular event out of a (likely) large log file. If you are after more detailed statistics, you can export the parsed data to simple files (using option '-o') and use your favorite software for analysis.");
		System.out.println("Options:");
		System.out.println("--help: returns this help.");
		System.out.println("--version: returns the program's version.");
		System.out.println("-f: to specify the input file (required).");
		System.out.println("-s: to specify a scanf style search string, ie: \"score: %d points\" (required). Note that the search string doesn't necessarily start at the beginning of a log line, or end at the end of a log line.");
		System.out.println("-o: if you prefer all scanned values to be extracted and written into files, instead of getting stats. Note that a file will be created for each variable.");
		System.out.println("Examples:");
		System.out.println("LogAnalyser -f \"myfile\" -s \"transfer: %d ms.\"");
		System.out.println("LogAnalyser -f \"myfile\" -s \"transfer: %d ms.\" -o \"outputdir\"");
		System.out.println("");
	}
	
//	-f "/Users/chs/Documents/Cartell/temp/tomcat.log.2015-07-08" -s "Received XML response, round trip was %d milliseconds." -o "/Users/chs/Documents/Cartell/temp/results"
}
