import java.io.Serializable;	//Import necessary classes
import java.util.ArrayList;

/**
 * Stores a message that can be sent between peers through a socket.
 * @author Ari
 *
 */
public class Message implements Serializable
{

	private static final long serialVersionUID = -275193729503169230L;

	private String command;	//The main text of the message
	private ArrayList<Serializable> objects;	//An array for storing any associated objects

	/**
	 * Constructor for a message.
	 * @param c Command
	 * @param o Collection of objects
	 */
	public Message(String c, ArrayList<Serializable> o)
	{
		command = c;
		objects = o;
	}
	
	/**
	 * Accessor method for the command part of this message.
	 * @return Command
	 */
	public String getCommand()
	{
		return command;
	}
	
	/**
	 * Accessor method for the array of objects in the message.
	 * @return The array of objects
	 */
	public ArrayList<Serializable> getObjects()
	{
		return objects;
	}
}//class
