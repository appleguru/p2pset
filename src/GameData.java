import java.util.*;

public class GameData
{
	public Deck deck;
	public int gameID;
	public LinkedList<Player> playerList = new LinkedList<Player>();

	
	public boolean equals(GameData gd)
	{
		return true;
	}
}
