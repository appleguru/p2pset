import java.net.ServerSocket;
import java.net.Socket;

public class PeerListener extends Thread
{
	public static final int peerConnectionSocket = 62626; 
	
	public void run()
	{
		ServerSocket ss = null;
		try	//Creates the socket on the server side
		{
			ss = new ServerSocket(peerConnectionSocket);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
		};
		while(true)	//Main loop
		{
			try
			{
				Socket cs = ss.accept();	//Wait for a connection
				ConnectionHandler ch = new ConnectionHandler(cs);	//Create a new connection handler to deal with this client
				ch.start();	//Start the run method of the connection handler
			}
			catch(Exception e)
			{
				System.err.println(e);
				e.printStackTrace(System.err);
			};
		}
	}
}
