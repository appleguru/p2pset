import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener extends Thread
{
	public static final int serverConnectionSocket = 6262; 
	
	public void run()
	{
		ServerSocket ss = null;
		try	//Creates the socket on the server side
		{
			ss = new ServerSocket(serverConnectionSocket);
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
				System.out.println("Connection Accepted...");
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
