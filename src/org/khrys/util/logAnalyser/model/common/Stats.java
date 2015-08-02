package org.khrys.util.logAnalyser.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.khrys.util.logAnalyser.model.common.metrics.Metric;
import org.khrys.util.logAnalyser.model.common.metrics.MetricType;
import org.khrys.util.logAnalyser.model.common.utils.Constants;

public class Stats {
	private Variable variable = null;
	private ArrayList<?> values = null;
	
	private HashMap<MetricType, Metric> metrics = null;
	
	public Stats(Variable variable, ArrayList<?> values) {
		this.variable = variable;
		this.values = values;
	}
	
	public Variable getVariable() {
		return variable;
	}
	
	public ArrayList<?> getValues() {
		return values;
	}
	
	public HashMap<MetricType, Metric> getMetrics() {
		if (metrics== null) {
			computeMetrics();
		}
		return metrics;
	}
	
	@Override
	public String toString() {
		if (metrics == null) {
			computeMetrics();
		}
		StringBuilder bob = new StringBuilder();
		for (Metric metric: metrics.values()) {
			bob.append(metric.toString());
		}
		return bob.toString();
	}
	
	private void computeMetrics() {
		this.metrics = new HashMap<MetricType, Metric>();
		DataType dataType = variable.getDataType();
		for (MetricType metricType: Constants.DATATYPE_TO_METRICTYPE.get(dataType)) {
			Metric metric = Metric.getMetric(dataType, metricType, values);
			metric.getResults();
			metrics.put(metricType, metric);
		}
	}
}
