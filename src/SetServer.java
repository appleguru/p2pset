import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SetServer {

	static boolean isServer;
	String myName;
	static GameData gd;
	private static int messagePort = 62626;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerListener sl = new ServerListener();
		sl.start();
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
		sendMessage(m, "localhost");
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
			sendMessage(m, "localhost");
		}//if the set is good
	}
	
	public static void sendMessage(Message m, String recipient)
	{
		try
		{
			Socket sock = new Socket(recipient, messagePort);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());	//Create an object output stream to send messages to the server
			oos.writeObject(m);	//Send a PUT message to the server with the given object
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}//sendMessage

}
