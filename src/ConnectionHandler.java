import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

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
				ArrayList<Object> messageObjects = m.getObjects();
				
				SetServer.checkSet((Card)messageObjects.get(0), (Card)messageObjects.get(1), (Card)messageObjects.get(2), (Player)messageObjects.get(3));
			}
			else if(command.equals("SYNCH_TO_ME"))
			{
				P2PSet.myGameData = (GameData) m.getObjects().get(0);
				P2PSet.boardChanged();
				System.out.println("Synch to me recieved");
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
