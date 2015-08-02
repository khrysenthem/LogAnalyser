package org.khrys.util.logAnalyser.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Data {
	private HashMap<Variable, ArrayList<?>> data = new HashMap<Variable, ArrayList<?>>();
	private HashMap<Variable, Stats> stats = null;
	
	public Data(HashMap<Variable, ArrayList<?>> data) {
		this.data = data;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean addData(Variable v, ArrayList list) {
		if (v.getId() < 0 || v.getId()>=data.size()) {
			System.out.println("index out of bound");
			return false;
		} else {
			data.get(v).addAll(list);
		}
		return true;
	}
	
	public HashMap<Variable, Stats> getStats() {
		if (stats==null && data!=null) {
			stats = new HashMap<Variable, Stats>();
			for (Entry<Variable, ArrayList<?>> entry: data.entrySet()) {
				Stats stat = new Stats(entry.getKey(), entry.getValue());
				stats.put(entry.getKey(), stat);
			}
		}
		return stats;
	}
	
	public HashMap<Variable, ArrayList<?>> getData() {
		return data;
	}
}
