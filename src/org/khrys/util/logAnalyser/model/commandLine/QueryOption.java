package org.khrys.util.logAnalyser.model.commandLine;

public enum QueryOption {
	HELP("--help", 0),
	VERSION("--version", 0),
	FILE("-f", 1),
	PRINTFSTYLESEARCH("-s", 1),
	OUTPUTFILE("-o", 1),
	;
	
	private String code;
	private Integer nbOfParameters;
	
	private QueryOption(String code, Integer nbParams) {
		this.code = code;
		this.nbOfParameters = nbParams;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public Integer getNbOfParameters() {
		return nbOfParameters;
	}

	public static QueryOption getQueryOption(String code) {
		for(QueryOption queryOption: QueryOption.values()) {
			if (queryOption.getCode().equals(code)) {
				return queryOption;
			}
		}
		return null;
	}
	
}
