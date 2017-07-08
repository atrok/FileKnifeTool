package message;

public interface Message {
	
	public String header();
	public boolean addLine(String s);
	public String getMessage();
	
}
