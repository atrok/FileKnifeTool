package record;

public class SimpleRecord extends Record{
	
	public SimpleRecord(String first_column) {
		super(first_column);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return compareTo((Record)o);
	}

	@Override
	public int compareTo(Record arg0) {
		// TODO Auto-generated method stub
		return getZeroColumnName().compareTo(arg0.getZeroColumnName());
	}


}
