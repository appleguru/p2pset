import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class SetPeer {

	private final int messagePort = 6262;
	public GameData myGameData;
	public Player me;
	public P2PSet gui;
	public Communicator com;
	public boolean token;
	public boolean wantCS;
	
	public SetPeer(P2PSet _gui){
		gui = _gui;
		//TODO finish this
	}
	
	public void createNewGame(){
		//TODO
	}
	
	public void joinGame(int gameID){
		//TODO
	}
	
	public void receiveLooking(Message m){
		requestCS();
		
		releaseCS();
	}
	
	public void claimSet(Card c1, Card c2, Card c3){
		requestCS();
		
		//TODO
		releaseCS();
	}
	
	public void receiveClaimSet(Message m){
		
	}
	
	public void askMoreCards(){
		requestCS();
		
		//TODO
		releaseCS();
	}
	
	public void Quit(){
		requestCS();
		
		//TODO
		releaseCS();
	}
	
	public void readMessage(){
		Message m = com.receiveMsg();
	}
	
	public void requestCS(){
		wantCS=true;
		if(!token)
			myWait(this);
		wantCS=false;
		releaseCS();

	}
	
	public void releaseCS(){
		if(myGameData.playerList.size()>1){
			token = false;
			int idx = myGameData.playerList.indexOf(me);
			int i = (idx+1)%myGameData.playerList.size();
			com.sendPASS_TOKEN(myGameData.playerList.get(i));
			}
	}
	
	public synchronized void myWait(Object o){
		try{
			o.wait();
		}catch(InterruptedException e){
		}
	}
	
	public synchronized void receiveToken(){
		this.notify();
		token = true;
		if(!wantCS){
			releaseCS();
		}
	}
}
