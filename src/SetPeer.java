public class SetPeer {

	private static boolean isServer;
	private String myName;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GUI myGUI = new GUI();
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

}
