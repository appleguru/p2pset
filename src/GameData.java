import java.io.Serializable;	//Import necessary classes
import java.util.*;

/**
 * Stores the state of this game, including where all the cards are and what everyone's score is.
 * @author Ari
 *
 */
public class GameData implements Serializable
{
	private static final long serialVersionUID = -2312439373810012826L;

	public Deck deck;	//Current state of the Set deck
	public int gameID;
	public int numPlayersWantCards;	//Number of players that have clicked the "Deal more cards" button
	public ArrayList<Player> playerList;

	/**
	 * Constructor for the GameData object.
	 * @param creator Player to whom this GameData belongs
	 */
	public GameData(Player creator)
	{
		deck = new Deck();
		playerList = new ArrayList<Player>();
		playerList.add(creator);
	}//Constructor

	/**
	 * Compares this game data to another game data object.
	 */
	public boolean equals(Object o)
	{
		GameData other = (GameData)o;
		return (deck.equals(other.deck) && gameID == other.gameID && playerList.equals(other.playerList));
	}

	/**
	 * Adds a player to the game.
	 * @param playerName Name of this player
	 */
	public void addPlayer(String playerName)
	{
		playerList.add(new Player(playerName));
	}//addPlayer
}//class
