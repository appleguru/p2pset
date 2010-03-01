import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread
{
	Socket sock;
	public ConnectionHandler(Socket s)
	{
		sock = s;
	}
	public void run()
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			Object readObject;
			while((readObject = ois.readObject()) == null);	//Read until we find an object on the input stream
			Message m = (Message)readObject;	//Convert what we read into a message
			String command = m.getCommand();	//Get the command from the message
			if(command.equals("I_CLAIM_SET"))
			{
				
			}
			else if(command.equals("SYNCH_TO_ME"))
			{
				
			}
			else
			{
				System.out.println("Server received an invalid command from peer.");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
