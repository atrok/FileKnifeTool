package record;

import java.util.Objects;

public class SQLRecord extends Record{

	private String table_name="TestTable";
	public SQLRecord(String timestamp) {
		super(timestamp);
		//table_name=timestamp;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return compareTo((Record)arg0);
	}

	@Override
	public int compareTo(Record arg0) {
		// TODO Auto-generated method stub
		return getZeroColumnName().compareTo(arg0.getZeroColumnName());
	}
	
	public String toString(){
		//String line=first_column;
		
		StringBuilder sb=new StringBuilder();
		
		sb.append("INSERT INTO "+this.table_name+" VALUES ('"+first_column+"',");
		for(int i=0;i<values.size();i++){
			String s=values.get(i).toString();
			sb.append("'"+s+"'");
			if ((values.size()-i)>1)
				sb.append(',');
		}
		sb.append(")\n");
		return sb.toString();
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof SQLRecord)) {
            return false;
        }
        SQLRecord obj = (SQLRecord) o;
        return Objects.equals(getZeroColumnName(), obj.getZeroColumnName());
    }
}
