import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Communicator
{
	private boolean debug = false;
	private static final String MULTICAST_ADDRESS = "225.6.6.6";
	private static final int MULTICAST_PORT = 6262;
	private static final int TCP_PORT = 2626;
	public ArrayList<Player> players;
	private ConcurrentLinkedQueue<Message> msgQueue;
	private msgListener ml;
	private multicastListener mcl;
	private SetPeer sp;
	
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
	
	public void sendTCPMessage(Message m, String recipient)
	{
		try
		{
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
	
	public void sendTEST_MESSAGE()
	{
		sendTCPMessage(new Message("Hello", null), "localhost");
	}
	
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
	
	public void sendLOOKING_FOR_GAMES()
	{
		String m = "LOOKING!";
		sendMulticastMessage(m);
	}
	
	public void sendHERE_IS_A_GAME(String ip, GameData gd)
	{
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(gd);
		Message m = new Message("HERE_IS_A_GAME", data);
		sendTCPMessage(m, ip);
	}
	
	public void sendNEW_PLAYER(Player p)
	{
		ArrayList<Serializable> data = new ArrayList<Serializable>();
		data.add(p);
		Message m = new Message("NEW_PLAYER", data);
		String destination = "";
		for(Serializable player: players)
		{
			destination = ((Player)player).ip.getHostAddress();
			sendTCPMessage(m, destination);
		}
	}
	
	public void sendSYNCHRONIZE_TO_ME()
	{
		Message m = new Message("SYNCHRONIZE_TO_ME", null);
		String destination = "";
		for(Serializable p: players)
		{
			destination = ((Player)p).ip.getHostAddress();
			sendTCPMessage(m, destination);
		}
	}
	
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
	
	public void sendADDED_MORE_CARDS(ArrayList<Serializable> data){
		Message m = new Message("ADDED_MORE_CARDS", data);
		String dest = "";
		for (Player p : players){
			dest = p.ip.getHostAddress();
			sendTCPMessage(m, dest);
		}
	}
	
	public void sendPASS_TOKEN(Player p)
	{
		Message m = new Message("PASS_TOKEN", null);
		sendTCPMessage(m, p.ip.getHostAddress());
	}
	
	public Message receiveMsg()
	{
		return msgQueue.poll();
	}
	
	public void debug(String s){
		if (debug) System.out.println(s);
	}
	
	public class msgListener extends Thread
	{
		private int port;
		private ServerSocket ss;
		
		public msgListener(int _port)
		{
			port = _port;
			ss = null;
		}
		
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
