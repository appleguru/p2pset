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
	
	public SetPeer(P2PSet _gui, String playerName){
		gui = _gui;
		me = new Player(playerName);
		com = new Communicator();
		//TODO finish this
	}
	
	public GameData createNewGame(){
		myGameData = new GameData(me);
		return myGameData;
		//tell the gui to display the new game
		
	}
	
	public void joinGame(){
		com.sendLOOKING_FOR_GAMES();
	}
	
	public void receiveLooking(Message m){
		requestCS();
		
		releaseCS();
	}
	
	public GameData receiveGameToJoin(Message m){
		myGameData = (GameData)m.getObjects().get(0);
		com.players = myGameData.playerList;
		com.sendNEW_PLAYER(me);
		myGameData.playerList.add(me);
		return myGameData;
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
	
	public void receiveMoreCardsRequest(Message m){
		
	}
	
	public void Quit(){
		requestCS();
		
		//TODO
		releaseCS();
	}
	
	public void recieveQuit(Message m){
		
	}
	
	public void receiveNewPlayer(Message m){
		myGameData.playerList.add((Player)m.getObjects().get(0));
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
