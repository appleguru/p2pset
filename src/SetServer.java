import java.util.ArrayList;

public class SetServer {

	static boolean isServer;
	String myName;
	static GameData gd;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
	/**
	 * Sets the server to the next player in the player list.
	 * @return Returns the name of the new server.
	 */
	private String setServer()
	{
		return "";
	}
	/**
	 * If this player is the server, this method can be called to broadcast its GameData object to all other players, forcing them to synchronize to it.
	 */
	public static void sendSYNCH_TO_ME()
	{
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(gd);
		Message m = new Message("SYNCH_TO_ME", data);
		P2PSet.sendMessage(m, "localhost");
	}
	/**
	 * Synchronizes this player's GameData object to the incoming one.
	 * @param gd The incoming GameData object from the server.
	 */
	private void synchronizeSelfTo(GameData gd)
	{
		
	}
	
	public static void checkSet(Card card1, Card card2, Card card3, Player claimant){
		if (gd.deck.verifySet(card1, card2, card3)){
			gd.deck.removeSet(card1, card2, card3);
			gd.playerList.get(gd.playerList.get(claimant.name)).score++;
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(gd);
			Message m = new Message("SYNCH_TO_ME", data);
			P2PSet.sendMessage(m, "localhost");
		}//if the set is good
	}

}
