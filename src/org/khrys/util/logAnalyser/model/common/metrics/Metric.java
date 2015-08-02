package org.khrys.util.logAnalyser.model.common.metrics;

import java.util.ArrayList;

import org.khrys.util.logAnalyser.model.common.DataType;

public abstract class Metric {
	protected DataType dataType;
	protected MetricType metricType;
	protected ArrayList<?> values;
	protected Object results;
	
	protected Metric(DataType dataType, MetricType metricType, ArrayList<?> values) {
		this.dataType = dataType;
		this.metricType = metricType;
		this.values = values;
	}

	public DataType getDataType() {
		return dataType;
	}

	public MetricType getMetricType() {
		return metricType;
	}
	
	public ArrayList<?> getValues() {
		return values;
	}

	public Object getResults() {
		if (results == null)
			makeMetricResults();
		return results;
	}

	protected abstract void makeMetricResults();
	
	public String toString() {
		StringBuilder bob = new StringBuilder();
		bob.append("----------------");
		bob.append("DataType: "+dataType.toString());
		bob.append("--------");
		bob.append("MetricType: "+metricType.toString());
		for (int i=0; i< 16-metricType.toString().length(); i++)
			bob.append("-");
		bob.append("#Values: "+(values==null?"null":values.size()));
		bob.append("--------");
		bob.append("Results: "+getResultAsString());
//		bob.append("----------------");
		bob.append("\n");
		return bob.toString();
	}

	public String getResultAsString() {
		return results.toString();
	}

	public static Metric getMetric(DataType dataType, MetricType metricType, ArrayList<?> values) {
		Metric metric = null;
		switch (dataType) {
			case CHARACTER:
				break;
			case DECIMAL:
				break;
			case INTEGER:
				metric = new IntegerMetrics(metricType, values);
				break;
			case STRING:
				break;
			default:
				break;
		}
		return metric;
	}
}
