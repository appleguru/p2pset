import java.io.Serializable;
import java.net.InetAddress;

public class Player implements Serializable
{

	private static final long serialVersionUID = 5419528483896313590L;
	
	public String name;
	public InetAddress ip;
	public int score;
	
	public Player()
	{
	score = 0;
	name = "Me!"; //TODO: Get this from text field in GUI
	
	try { ip = InetAddress.getLocalHost(); }
	catch (Exception e) { System.err.println("Error " + e + " getting local address."); }
	}//Constructor
	
	public boolean equals(Object o){
		Player other = (Player)o;
		
		return (name.equals(other.name) && ip.equals(other.ip));
	}
	
}
