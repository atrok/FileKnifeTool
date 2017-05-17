package record;

import java.util.ArrayList;
import java.util.List;

public abstract class Record implements Comparable{

	protected String first_column;
	protected List values=new ArrayList();
	public Record(String timestamp){
		this.first_column=timestamp;
	}
	
	public void addValue(Object v){
		
			values.add(v);
	}

	public void putValue(int index,Object v){
		values.add(index, v);
	}
	
	public List getValues(){
		return values;
	}
	
	protected String getZeroColumnName(){
		return first_column;
	}
	
	
	public abstract int compareTo(Record arg0);

	public String toString(){
		String line=first_column;
		for(Object o: values){
			String s=o.toString();
			line=line+","+s;
		}
		return line+"\n";
	}
}
