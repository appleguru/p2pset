import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable
{

	private static final long serialVersionUID = -275193729503169230L;

	private String command;
	private ArrayList<Serializable> objects;
	
	public Message(String c, ArrayList<Serializable> o)
	{
		command = c;
		objects = o;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public ArrayList<Serializable> getObjects()
	{
		return objects;
	}
}
