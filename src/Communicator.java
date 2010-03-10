import java.io.ObjectInputStream;	//Import necessary classes
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Handles all sending and receiving of messages for SetPeer.
 * @author Ari
 *
 */
public class Communicator
{
	private boolean debug = true;	//Used to show helpful output text in the console
	private static final String MULTICAST_ADDRESS = "225.6.6.6";	//Group IP used by multicast
	private static final int MULTICAST_PORT = 6262;	//Define constant ports to use
	private static final int TCP_PORT = 2626;
	public ArrayList<Player> players;	//Keep a local liist of players we can use as an "address book"
	private ConcurrentLinkedQueue<Message> msgQueue;	//Queue of received messages that have not yet been processed 
	private msgListener ml;
	private multicastListener mcl;
	private SetPeer sp;	//Keep a reference to the set peer
	
	/**
	 * Constructor for the Communicator class.
	 * @param _sp A reference to the creating class so we have access to its variables
	 */
	public Communicator(SetPeer _sp)
	{
		sp = _sp;
		msgQueue = new ConcurrentLinkedQueue<Message>();
		players = new ArrayList<Player>();
		mcl = new multicastListener(MULTICAST_PORT, MULTICAST_ADDRESS);
		mcl.start();
		ml = new msgListener(TCP_PORT);
		ml.start();
	}//Constructor

	/**
	 * Sends a message to another peer via the Transmission Control Protocol.
	 * @param m Message to send
	 * @param recipient IP address of the recipient coded as a string
	 */
	public void sendTCPMessage(Message m, String recipient)
	{
		try
		{
			if (!m.getCommand().equals("PASS_TOKEN"))
				debug ("sending a " + m.getCommand() + " message");
			Socket sock = new Socket(recipient, TCP_PORT);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(m);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	/**
	 * Sends a message to anyone who's listening via the Universal Datagram Protocol.
	 * @param m Message to send
	 */
	public void sendMulticastMessage(String m)
	{	
		try
		{
			debug ("sending a Multicast");
			MulticastSocket mcs = new MulticastSocket(MULTICAST_PORT);
			byte[] buffer = m.substring(0, 8).getBytes();
			DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(MULTICAST_ADDRESS), MULTICAST_PORT);
			mcs.send(dp);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
		
	/**
	 * Sends a message to all players alerting them that this peer has claimed a set.
	 * @param cards Array of cards in the set
	 * @param claimant Peer who is claiming the set
	 */
	public void sendI_CLAIM_SET(Card[] cards, Player claimant)
	{
		ArrayList<Serializable> set = new ArrayList<Serializable>();
		set.add(cards);
		set.add(claimant);
		//System.out.println("Sending I_CLAIM_SET to server...");
		Message m = new Message("I_CLAIM_SET", set);
		String destination = "";
		for(Serializable p: players)
		{
			destination = ((Player)p).ip.getHostAddress();
			sendTCPMessage(m, destination);
		}
	}

	/**
	 * Send a message to anyone who's listening in attempt to find games to join.
	 */
	public void sendLOOKING_FOR_GAMES()
	{
		String m = "LOOKING!";
		sendMulticastMessage(m);
	}
	
	/**
	 * Sends a message asking for more info from a peer that just sent a "LOOKING_FOR_GAMES" message.
	 * @param ip IP address of the new player
	 * @param peteTownsend The player (or rock star) who is asking "Who are you? Who-who, who-who?"
	 */
	public void sendWHO_ARE_YOU( String ip, Player peteTownsend){
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(peteTownsend);
		Message m = new Message ("WHO_ARE_YOU", data);
		sendTCPMessage(m, ip);
	}
	
	/**
	 * Sends a response to a "WHO_ARE_YOU" message, with attached information about the sending player.
	 * @param peteTownsend The player who was asking "Who are you? Who-who, who-who?"
	 * @param me Reference to the sending player
	 */
	public void sendI_AM_ME(Player peteTownsend, Player me){
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(me);
		Message m = new Message("I_AM_ME", data);
		this.sendTCPMessage(m, peteTownsend.ip.getHostAddress());
	}
	
	/**
	 * Sends a message to a new player who has identified themselves via an "I_AM_ME" message, telling them about the game this peer is involved in.  GameData is attached.
	 * @param ip IP address of the noob
	 * @param gd GameData of this game
	 */
	public void sendHERE_IS_A_GAME(String ip, GameData gd)
	{
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(gd);
		Message m = new Message("HERE_IS_A_GAME", data);
		sendTCPMessage(m, ip);
	}
	
	/**
	 * Sends a message which alerts all players that this player has just joined the game.
	 * @param p Newly joined player
	 * @param sender Player that introduced this new player to the game
	 */
	public void sendNEW_PLAYER(Player p, Player sender)
	{
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(p);
		Message m = new Message("NEW_PLAYER", data);
		String destination = "";
		for(Player player: players)
		{
			if (!player.equals(sender)){
				destination = player.ip.getHostAddress();
				sendTCPMessage(m, destination);
			}
		}
	}
	
	/**
	 * Sends a message alerting other players that we are quitting, so they know not to send us the token anymore.
	 * @param p Player that is quitting
	 */
	public void sendQUIT(Player p){
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(p);
		Message m = new Message("QUIT", data);
		String destination = "";
		for(Serializable player: players)
		{
			if (!p.equals(player)){
				destination = ((Player)player).ip.getHostAddress();
				sendTCPMessage(m, destination);
			}
		}
	}
	
	/**
	 * Sends a message to other players that this player wants more cards dealt.  Attached is the number of players that have already indicated that they want more cards.
	 * @param numPlayersWant Number of players that have already indicated that they want more cards
	 */
	public void sendWANT_MORE_CARDS(int numPlayersWant){
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(new Integer(numPlayersWant));
		Message m = new Message ("WANT_MORE_CARDS", data);
		String dest = "";
		for (Player p : players){
			dest = p.ip.getHostAddress();
			sendTCPMessage(m, dest);
		}
	}
	
	/**
	 * Send a message to all other players that more cards have been dealt.
	 * @param data The cards that were dealt
	 */
	public void sendADDED_MORE_CARDS(ArrayList<Serializable> data){
		Message m = new Message("ADDED_MORE_CARDS", data);
		String dest = "";
		for (Player p : players){
			dest = p.ip.getHostAddress();
			sendTCPMessage(m, dest);
		}
	}
	
	/**
	 * Sends a message to an idiot to decrease their score since they clicked 3 cards that aren't a set.
	 * @param dunce The idiot who didn't look before they clicked...
	 */
	public void sendDEDUCTION(Player dunce){
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(dunce);
		Message m = new Message("DEDUCTION", data);
		String dest = "";
		for (Player p : players){
			dest = p.ip.getHostAddress();
			sendTCPMessage(m, dest);
		}
	}
	
	/**
	 * Passes the token to the given player.
	 * @param p Player to pass the token to
	 */
	public void sendPASS_TOKEN(Player p)
	{
		Message m = new Message("PASS_TOKEN", null);
		sendTCPMessage(m, p.ip.getHostAddress());
	}
	
	/**
	 * Can be called to get the next message out of the queue.
	 */
	public Message receiveMsg()
	{
		return msgQueue.poll();
	}

	/**
	 * Prints debug messages, only if the debug flag is true.
	 * @param s String to print.
	 */
	public void debug(String s){
		if (debug) System.out.println(s);
	}

	/**
	 * This interior class listens for TCP messages sent to this client.  It runs as a thread spawned from Communicator.
	 * @author Ari
	 *
	 */
	public class msgListener extends Thread
	{
		private int port;
		private ServerSocket ss;
		
		/**
		 * Constructor for the message listener.
		 * @param _port Port to receive messages on
		 */
		public msgListener(int _port)
		{
			port = _port;
			ss = null;
		}

		/**
		 * This method is run when the thread is started.  Loops infinitely, accepting connections and passing them off to the message handler.
		 */
		public void run()
		{
			try
			{
				ss = new ServerSocket(port);
				while(true)	//Main loop
				{
					Socket cs = ss.accept();	//Wait for a connection
					ObjectInputStream ois = new ObjectInputStream(cs.getInputStream());
					Message m = null;
					while((m = (Message)ois.readObject()) == null);
					cs.close();
					//msgQueue.add(m);
					Thread mh = new Thread(new MessageHandler(m, sp));
					mh.start();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	 * Constructor for the multicast message listener.
	 * @param _port Port to receive messages on
	 */
	public class multicastListener extends Thread
	{
		private int port;
		private String address;
		private MulticastSocket mcs;

		public multicastListener(int _port, String _address)
		{
			port = _port;
			address = _address;
		}
		
		/**
		 * This method is run when the thread is started.  Loops infinitely, receiving messages and passing them off to the message handler.
		 */
		public void run()
		{
			try
			{
				mcs = new MulticastSocket(port);
				mcs.joinGroup(InetAddress.getByName(address));
				while(true)
				{
					byte[] buffer = new byte[8];
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					mcs.receive(dp);
					String commandString = new String(dp.getData());
					String senderIP = dp.getAddress().getHostAddress();
					InetAddress myAddress = InetAddress.getLocalHost();
					String strAddr = myAddress.getHostAddress();
					if (!(senderIP.equals(strAddr)))
					{
					ArrayList<Serializable> objArray = new ArrayList<Serializable>();
					objArray.add(senderIP);
					Message m = new Message(commandString, objArray);
					//msgQueue.add(m);
					Thread mh = new Thread(new MessageHandler(m, sp));
					mh.start();
					}//if the message isn't from us
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
