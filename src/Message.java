import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String command;
	private ArrayList<Object> objects;
	
	public Message(String c, ArrayList<Object> o)
	{
		command = c;
		objects = o;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public ArrayList<Object> getObjects()
	{
		return objects;
	}
}
