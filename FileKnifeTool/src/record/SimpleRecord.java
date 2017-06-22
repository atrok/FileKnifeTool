package record;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof SimpleRecord)) {
            return false;
        }
        SimpleRecord obj = (SimpleRecord) o;
        return Objects.equals(getZeroColumnName(), obj.getZeroColumnName());
    }
}
