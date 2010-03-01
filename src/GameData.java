import java.util.*;

public class GameData
{
	public Deck deck;
	public int gameID;
	public HashMap<String, Player> playerList = new HashMap<String, Player>();

	public GameData()
	{
	deck = new Deck();
	playerList.put("Me!", new Player());
	}//Constructor
	
	public boolean equals(Object o)
	{
		GameData other = (GameData)o;
		return (deck.equals(other.deck) && gameID == other.gameID && playerList.equals(other.playerList));
	}
}
