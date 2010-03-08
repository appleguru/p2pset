import java.io.Serializable;
import java.net.InetAddress;

public class Player implements Serializable
{

	private static final long serialVersionUID = 5419528483896313590L;
	
	public String name;
	public InetAddress ip;
	public int score;
	public boolean wantsMoreCards;
	
	public Player(String userName)
	{
	score = 0;
	name = userName;
	wantsMoreCards = false;
	
	try { ip = InetAddress.getLocalHost(); }
	catch (Exception e) { System.err.println("Error " + e + " getting local address."); }
	}//Constructor
	
	public boolean equals(Object o){
		Player other = (Player)o;
		
		return (name.equals(other.name) && ip.equals(other.ip));
	}
	
	public void setPlayerName (String playerName)
	{
	name = playerName;
	}//setPlayerName
	
}
