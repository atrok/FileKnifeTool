package logprocessing;

public enum StatisticParamNaming {
	
	REGEXP("regexp"),
	FIELD("field"),
	STATTYPE("stattype");
	
	
	private String description;
	// Constructor must be package or private access:
	private StatisticParamNaming(String description) {
	this.description = description;
	}
	public String getDescription() { return description; }
	public String toString() { return description; }

}
