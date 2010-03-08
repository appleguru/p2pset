import java.io.Serializable;
import java.util.*;

public class GameData implements Serializable
{
	private static final long serialVersionUID = -2312439373810012826L;

	public Deck deck;
	public int gameID;
	public int numPlayersWantCards;
	public ArrayList<Player> playerList;

	public GameData()
	{
		deck = new Deck();
		playerList = new ArrayList<Player>();
	}//Constructor
	
	public boolean equals(Object o)
	{
		GameData other = (GameData)o;
		return (deck.equals(other.deck) && gameID == other.gameID && playerList.equals(other.playerList));
	}
		
	public void addPlayer(String playerName)
	{
		playerList.add(new Player(playerName));
	}//addPlayer
}//class
