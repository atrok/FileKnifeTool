package record;

public class Header extends Record{

	public Header(String timestamp) {
		super(timestamp);
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
