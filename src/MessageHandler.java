
public class MessageHandler implements Runnable {

	boolean debug = false;
	private Message msg;
	private SetPeer sp;
	
	public MessageHandler (Message _m, SetPeer _sp)
	{
		sp = _sp;
		msg = _m;		
	}//Constructor
	
	public void run() {

		debug ("processing a " + msg.getCommand() + " message");
		
		if (msg.getCommand().equals("PASS_TOKEN"))
		{
			sp.receiveToken();
		}//if token
		
		else if (msg.getCommand().equals("I_CLAIM_SET"))
		{
			sp.receiveClaimSet(msg);
		}//else if I claim set
		
		else if (msg.getCommand().equals("ADDED_MORE_CARDS")){
			sp.receiveMoreCardsAdded(msg);
		}
		
		else if (msg.getCommand().equals("LOOKING!")){
			sp.receiveLooking(msg);
		}
		
		else if (msg.getCommand().equals("HERE_IS_A_GAME")){
			sp.receiveHereIsAGame(msg);
		}
		
		else if (msg.getCommand().equals("NEW_PLAYER")){
			sp.receiveNewPlayer(msg);
		}
		else if (msg.getCommand().equals("DEDUCTION")){
			sp.receiveDeduction(msg);
		}
		else if (msg.getCommand().equals("WANT_MORE_CARDS")){
			sp.receiveMoreCardsRequest(msg);
		}
		else if (msg.getCommand().equals("QUIT"));{
			sp.recieveQuit(msg);
		}
		
	}//run
	
	public void debug (String s){
		if (debug) System.out.println(s);
	}
}//class
