import java.io.Serializable;	//Import necessary classes
import java.net.InetAddress;

/**
 * Represents a peer involved in a game of Set.
 * @author Ari
 *
 */
public class Player implements Serializable
{

	private static final long serialVersionUID = 5419528483896313590L;
	
	public String name;	//Player's name
	public InetAddress ip;	//IP address
	public int score;	//Score
	public boolean wantsMoreCards;	//Flag for whether they have clicked the "Deal more cards" button
	
	/**
	 * Constructor for class Player.
	 * @param userName Name of the player
	 */
	public Player(String userName)
	{
	score = 0;
	name = userName;
	wantsMoreCards = false;
	
	try { ip = InetAddress.getLocalHost(); }
	catch (Exception e) { System.err.println("Error " + e + " getting local address."); }
	}//Constructor
	
	/**
	 * Determine if two players are equivalent.
	 */
	public boolean equals(Object o){
		Player other = (Player)o;
		
		return (ip.equals(other.ip));
	}
	
	/**
	 * Mutator method for the player's name.
	 * @param playerName Player's new name
	 */
	public void setPlayerName (String playerName)
	{
	name = playerName;
	}//setPlayerName
	
}
