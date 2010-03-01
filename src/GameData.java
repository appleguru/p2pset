import java.util.*;

public class GameData
{
	public Deck deck;
	public int gameID;
	public LinkedList<Player> playerList = new LinkedList<Player>();

	
	public boolean equals(Object o)
	{
		GameData other = (GameData)o;
		return (deck.equals(other.deck) && gameID == other.gameID && playerList.equals(other.playerList));
	}
}
