package org.khrys.util.logAnalyser.model.common.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.khrys.util.logAnalyser.model.common.DataType;
import org.khrys.util.logAnalyser.model.common.metrics.MetricType;

public class Constants {
	public static String PRODUCT_NAME			= "LogAnalyser";
	public static String PRODUCT_VERSION		= "0.2";
	
	
	public static HashMap<DataType, ArrayList<MetricType>> DATATYPE_TO_METRICTYPE = new HashMap<DataType, ArrayList<MetricType>>();
	static {
		ArrayList<MetricType> metricTypes = new ArrayList<MetricType>();

		metricTypes.add(MetricType.AVERAGE);
		metricTypes.add(MetricType.MIN);
		metricTypes.add(MetricType.MAX);
		metricTypes.add(MetricType.MEDIAN);
		
		DATATYPE_TO_METRICTYPE.put(DataType.INTEGER, metricTypes);
	}
}
