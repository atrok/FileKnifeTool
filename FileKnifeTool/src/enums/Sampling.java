package enums;

public enum Sampling {
    ZERO("raw timestamps", 0),
    ONEMINUTE("1 minute", 1),
    TENMINUTE("10 minute", 10),
	HOUR("1 hour", 60),
	DAY("1 day", 24);
	

    private String label;
    private Integer value;

    Sampling(String label, Integer value) {
        this.label = label;
        this.value = value;
    }

    public String toString() {
        return label;
    }
    
    public String getValue(){
    	
    	return Integer.toString(value);
    	
    }

}
