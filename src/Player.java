import java.net.InetAddress;

public class Player
{
	public String name;
	public InetAddress ip;
	public int score;
	public boolean isServer;
	
	public Player()
	{
	score = 0;
	name = "Me!"; //TODO: Get this from text field in GUI
	
	try { ip = InetAddress.getLocalHost(); }
	catch (Exception e) { System.err.println("Error " + e + " getting local address."); }
		
	isServer = false;
	}//Constructor
	
}
