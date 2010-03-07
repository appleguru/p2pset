import java.io.Serializable;
import java.util.*;

public class GameData implements Serializable
{
	private static final long serialVersionUID = -2312439373810012826L;

	public Deck deck;
	public int gameID;
	public HashMap<String, Player> playerList = new HashMap<String, Player>();

	public GameData()
	{
	deck = new Deck();
	}//Constructor
	
	public boolean equals(Object o)
	{
		GameData other = (GameData)o;
		return (deck.equals(other.deck) && gameID == other.gameID && playerList.equals(other.playerList));
	}
		
	public void addPlayer(String playerName)
	{
		playerList.put(playerName, new Player());		
	}//addPlayer
	
	public void changePlayerName(String oldPlayerName, String newPlayerName)
	{
		Player tempPlayer = playerList.remove(oldPlayerName);
		playerList.put(newPlayerName, tempPlayer);
	}//addPlayer
}
