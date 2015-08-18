package org.khrys.util.logAnalyser.model.common.metrics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

import org.khrys.util.logAnalyser.model.common.DataType;

public class IntegerMetrics extends Metric {

	public IntegerMetrics(MetricType metricType, ArrayList<?> values) {
		super(DataType.INTEGER, metricType, values);
	}

	@Override
	protected void makeMetricResults() {
		switch (metricType) {
		case AVERAGE:
			results = getAverage();
			break;
		case MAX:
			results = getMax();
			break;
		case MEDIAN:
			results = getMedian();
			break;
		case MIN:
			results = getMin();
			break;
		case RANGE:
			ArrayList<BigInteger> range = new ArrayList<BigInteger>();
			range.add(getMin());
			range.add(getMax());
			results = range;
			break;
		case VARIANCE:
			results = getVariance();
			break;
		case STD_DEVIATION:
			results = (Double) Math.sqrt(Double.parseDouble(getVariance().toPlainString()));
		default:
			break;
		
		}
	}
	
	private BigInteger getMin() {
		BigInteger min = null;
		if (values!=null && !values.isEmpty()) {
			for (Object value: values) {
				BigInteger bigInteger = (BigInteger) value;
				if (min==null)
					min = bigInteger;
				else
					min = min.min(bigInteger);
			}
		}
		return min;
	}
	
	private BigInteger getMax() {
		BigInteger max = null;
		if (values!=null && !values.isEmpty()) {
			for (Object value: values) {
				BigInteger bigInteger = (BigInteger) value;
				if (max==null)
					max = bigInteger;
				else
					max = max.max(bigInteger);
			}
		}
		return max;
	}
	
	private BigDecimal getAverage() {
		BigDecimal average = null;
		if (values!=null && !values.isEmpty()) {
			BigInteger total = null;
			for (Object value: values) {
				BigInteger bigInteger = (BigInteger) value;
				if (total==null)
					total = bigInteger;
				else
					total = total.add(bigInteger);
			}
			average = new BigDecimal(total).divide(new BigDecimal(""+values.size()), 2, RoundingMode.HALF_UP);
		}
		return average;
	}
	
	@SuppressWarnings("unchecked")
	private BigDecimal getMedian() {
		BigDecimal median = null;
		// sort list
		Collections.sort((ArrayList<Integer>)values);
		// get median
		int index = values.size() / 2;
		if (values.size() % 2 == 1) {
			// if array size is odd, pick the middle value
			median = new BigDecimal(values.get(index+1)+"");
		} else {
			// if array size is even then pick the average of the 2 middle ones
			BigInteger left = new BigInteger(values.get(index)+"");
			BigInteger right = new BigInteger(values.get(index+1)+"");
			median = (new BigDecimal(left).add(new BigDecimal(right))).divide(new BigDecimal(2));
		}
		return median;
	}
	
	// https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#Computing_shifted_data
	public BigDecimal getVariance() {
		if (values.size()==0)
			return new BigDecimal(0);
		BigDecimal K = new BigDecimal((BigInteger)values.get(0));
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal sum_sqr = BigDecimal.ZERO;
		for (int n = 0; n<values.size();n++) {
			sum = sum.add(new BigDecimal( (BigInteger)values.get(n) ).subtract(K));
			sum_sqr = sum_sqr.add(new BigDecimal( (BigInteger)values.get(n) ).subtract(K).pow(2));
		}
		BigDecimal variance = (sum_sqr.subtract(sum.pow(2).divide(new BigDecimal(values.size()), 5, RoundingMode.HALF_UP))).divide(new BigDecimal(values.size()), 5, RoundingMode.HALF_UP);
		return variance;
	}
	
	public String getResultAsString() {
		String sResults = "";
		switch (metricType) {
		case AVERAGE:
			sResults = ((BigDecimal) results).toPlainString();
			break;
		case STD_DEVIATION:
			sResults = ((Double) results).toString();
			break;
		case MEDIAN:
			sResults = ((BigDecimal) results).toPlainString();
			break;
		case RANGE:
			@SuppressWarnings("unchecked")
			ArrayList<BigInteger> list = (ArrayList<BigInteger>) results;
			sResults = ((BigInteger) list.get(0)).toString() + " to " + ((BigInteger) list.get(1)).toString();
			break;
		case VARIANCE:
			sResults = ((BigDecimal) results).toPlainString();
			break;
		default:
			sResults = ((BigInteger) results).toString();
		}
		return sResults;
	}
}
