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
	private static final String MULTICAST_ADDRESS = "225.6.6.6";
	private static final int MULTICAST_PORT = 6262;
	private static final int TCP_PORT = 2626;
	private ArrayList<Serializable> players;
	private ConcurrentLinkedQueue<Message> msgQueue;
	private msgListener ml;
	private multicastListener mcl;
	
	public Communicator()
	{
		msgQueue = new ConcurrentLinkedQueue<Message>();
		mcl = new multicastListener(MULTICAST_PORT, MULTICAST_ADDRESS);
		mcl.start();
		ml = new msgListener(TCP_PORT);
		ml.start();
	}
	
	public void sendTCPMessage(Message m, String recipient)
	{
		try
		{
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
			MulticastSocket mcs = new MulticastSocket(MULTICAST_PORT);
			byte[] buffer = m.substring(0, 7).getBytes();
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
	
	public void sendI_CLAIM_SET(Card c1, Card c2, Card c3, Player claimant)
	{
		ArrayList<Serializable> set = new ArrayList<Serializable>();
		set.add(c1);
		set.add(c2);
		set.add(c3);
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
	
	public void sendHERE_IS_A_GAME(Player p)
	{
		Message m = new Message("HERE_IS_A_GAME", players);
		sendTCPMessage(m, p.ip.getHostAddress());
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
	
	public void sendPASS_TOKEN(Player p)
	{
		Message m = new Message("PASS_TOKEN", null);
		sendTCPMessage(m, p.ip.getHostAddress());
	}
	
	public Message receiveMsg()
	{
		return msgQueue.poll();
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
					msgQueue.add(m);
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
					ArrayList<Serializable> objArray = new ArrayList<Serializable>();
					objArray.add(senderIP);
					Message m = new Message(commandString, objArray);
					msgQueue.add(m);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
