import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;


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
		com = new Communicator(this);
		//TODO finish this
	}
	
	public GameData createNewGame(){
		myGameData = new GameData(me);
		com.players.add(me);
		token = true;
		return myGameData;
		//tell the gui to display the new game
		
	}
	
	public void joinGame(){
		com.sendLOOKING_FOR_GAMES();
	}
	
	public void receiveLooking(Message m){
		requestCS();
		com.sendHERE_IS_A_GAME((String)m.getObjects().get(0), myGameData);
		releaseCS();
	}
	
	public void receiveHereIsAGame(Message m){
		myGameData = (GameData) m.getObjects().get(0);
		while (myGameData == null);
		com.players = myGameData.playerList;
		com.sendNEW_PLAYER(me);
		myGameData.playerList.add(me);
		gui.myGameData = myGameData;
		gui.boardChanged();
	}//Here is a game!

	
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
			int[] indicesToReplace = {myGameData.deck.boardCards.indexOf(c1), myGameData.deck.boardCards.indexOf(c2), myGameData.deck.boardCards.indexOf(c3)};
			myGameData.deck.removeSet(c1, c2, c3);
			Card[] newCards = new Card[3];
			for (int i = 0; i < 3; i++){
				newCards[i] = myGameData.deck.boardCards.get(indicesToReplace[i]);
			}
			com.sendI_CLAIM_SET(newCards[0], newCards[1], newCards[2], indicesToReplace, me);
			
		}
		releaseCS();
	}
	
	public void receiveClaimSet(Message m){
		Player scorer = myGameData.playerList.get(myGameData.playerList.indexOf(m.getObjects().get(4)));
		scorer.score ++;
		myGameData.numPlayersWantCards = 0;
		gui.reqMoreCards.setSelected(false);
		myGameData.gameLog.append(scorer.name + " scores with Set: " + m.getObjects().get(0).toString() + " "  + m.getObjects().get(1).toString() +" " + m.getObjects().get(2).toString() + "\n");
		int[] indicesToReplace = (int[])m.getObjects().get(3);
		for (int i = 0; i < 3; i ++){
			myGameData.deck.replaceCard((Card)m.getObjects().get(i), indicesToReplace[i]);
			myGameData.gameLog.append("Adding Card: " + m.getObjects().get(i) + "\n");
		}
		gui.boardChanged();
	}
	
	public void sendDeductMe(){
		
	}
	
	public void receiveDeduction(Message m){
		
	}
	
	public void askMoreCards(){
		requestCS();
		myGameData.numPlayersWantCards ++;
		if (myGameData.numPlayersWantCards >= myGameData.playerList.size() / 2){
			ArrayList<Serializable> data = new ArrayList<Serializable>();
			for (int i = 0; i < 3; i++){
				data.add(myGameData.deck.dealCardNoRemove());
			}
			com.sendADDED_MORE_CARDS(data);
		}//if at least half of players now want more cards
		releaseCS();
	}
	
	public void receiveMoreCardsRequest(Message m){
		
	}
	
	public void receiveMoreCardsAdded(Message m){
		for (Serializable s : m.getObjects()){
			myGameData.deck.dealCard((Card)s);
		}
		gui.reqMoreCards.setSelected(false);
		myGameData.numPlayersWantCards = 0;
		gui.boardChanged();
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
		gui.boardChanged();
	}
	
	public Message readMessage(){
		return com.receiveMsg();
	}
	
	public void requestCS(){
		/*
		wantCS=true;
		if(!token)
			myWait(this);
		wantCS=false;
		releaseCS();
		*/
	}
	
	public void releaseCS(){
		/*
		if(myGameData.playerList.size()>1){
			token = false;
			int idx = myGameData.playerList.indexOf(me);
			int i = (idx+1)%myGameData.playerList.size();
			com.sendPASS_TOKEN(myGameData.playerList.get(i));
			}
			*/
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
