package record;

import java.util.Objects;

public class TimeStamp extends Record{
	
	int[] timestamp_split;
	
	public TimeStamp(String timestamp) {
		super(timestamp);
		// TODO Auto-generated constructor stub
		
		
		timestamp_split=toIntArray(timestamp);
	}

	@Override
	public int compareTo(Record arg0) {
		
		int[] comparable=toIntArray(arg0.getZeroColumnName());
		
		for(int i=0; i<timestamp_split.length;i++){

			if (timestamp_split[i]>comparable[i])
				return 1;
			
			if (timestamp_split[i]<comparable[i])
				return -1;
			
		}
		
		return 0;
	}
	
	private int[] toIntArray(String s){
		String[] ss=s.split("[\\s\\p{Punct}]");
		int[] split=new int[ss.length];
		
		for(int i=0;i<ss.length;i++){
			if(!ss[i].equals("null"))
			split[i]=Integer.valueOf(ss[i]);
			else split[i]=0;
		}
		return split;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return compareTo((Record)o);
	}

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof TimeStamp)) {
            return false;
        }
        TimeStamp obj = (TimeStamp) o;
        return Objects.equals(getZeroColumnName(), obj.getZeroColumnName());
    }

}
