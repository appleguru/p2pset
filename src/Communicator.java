import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;

public class Communicator
{
	private static final String MULTICAST_ADDRESS = "225.6.6.6";
	private static final int MULTICAST_PORT = 6262;
	private static final int TCP_PORT = 2626;
	private ArrayList<Player> players;
	private Queue<Message> msgQueue;
	private msgListener ml;
	private multicastListener mcl;
	
	public Communicator()
	{
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
		for (Player p: players){
			
			destination = p.ip.getHostAddress();
			sendTCPMessage(m, destination);
		}
	}
	
	public void sendLOOKING_FOR_GAMES()
	{
		
	}
	
	public void sendHERE_IS_A_GAME()
	{
		
	}
	
	public void sendNEW_PLAYER()
	{
		
	}
	
	public void sendSYNCHRONIZE_TO_ME()
	{
		
	}
	
	public void sendPASS_TOKEN()
	{
		
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
					Message m = (Message)ois.readObject();
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
					ArrayList<Object> objArray = new ArrayList<Object>();
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
