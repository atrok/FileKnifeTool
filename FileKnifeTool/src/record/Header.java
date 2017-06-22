package record;

import java.util.Objects;

public class Header extends Record{

	public Header(String timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
	}

	public void addValue(Object v){
		if (!values.contains(v))   /// Header may contain unique names of columns only;
			values.add(v);
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
        if (!(o instanceof Header)) {
            return false;
        }
        Header obj = (Header) o;
        return Objects.equals(getZeroColumnName(), obj.getZeroColumnName());
    }
}
