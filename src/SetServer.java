public class SetServer {

	static boolean isServer;
	String myName;
	GameData gd;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		GUI myGUI = new GUI();
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
	private void synchronizeSend()
	{
		
	}
	/**
	 * Synchronizes this player's GameData object to the incoming one.
	 * @param gd The incoming GameData object from the server.
	 */
	private void synchronizeSelfTo(GameData gd)
	{
		
	}
	
	public void checkSet(Card card1, Card card2, Card card3, Player claimant){
		if (gd.deck.verifySet(card1, card2, card3)){
			gd.deck.removeSet(card1, card2, card3);
			gd.playerList.get(gd.playerList.get(claimant.name)).score++;
			synchronizeSend();
		}//if the set is good
	}

}
