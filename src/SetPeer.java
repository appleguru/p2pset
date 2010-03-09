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
		token = true;
		return myGameData;
		//tell the gui to display the new game
		
	}
	
	public GameData joinGame(){
		com.sendLOOKING_FOR_GAMES();
		return receiveGameToJoin(readMessage());
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
		if (myGameData.deck.verifySet(c1, c2, c3)){
			com.sendI_CLAIM_SET(c1, c2, c3, me);
			
		}
		releaseCS();
	}
	
	public void receiveClaimSet(Message m){
		Player scorer = myGameData.playerList.get(myGameData.playerList.indexOf(m.getObjects().get(3)));
		scorer.score ++;
		myGameData.gameLog.append(scorer.name + " scores with Set " + m.getObjects().get(0).toString() + " "  + m.getObjects().get(1).toString() +" " + m.getObjects().get(2).toString());
		myGameData.deck.replaceCard(c, i);
	}
	
	public void sendDeductMe(){
		
	}
	
	public void receiveDeduction(Message m){
		
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
	
	public Message readMessage(){
		return com.receiveMsg();
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
