
public class MessageHandler implements Runnable {

	private Message msg;
	private SetPeer sp;
	
	public MessageHandler (Message _m, SetPeer _sp)
	{
		sp = _sp;
		msg = _m;		
	}//Constructor
	
	public void run() {

		if (msg.getCommand().equals("PASS_TOKEN"))
		{
			sp.receiveToken();
		}//if token
		
		else if (msg.getCommand().equals("I_CLAIM_SET"))
		{
			sp.receiveClaimSet(msg);
		}//else if I calim set
		
		else if (msg.getCommand().equals("ADDED_MORE_CARDS")){
			sp.receiveMoreCardsAdded(msg);
		}
		
	}//run
}//class
