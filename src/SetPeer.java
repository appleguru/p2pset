import java.io.Serializable;
import java.util.ArrayList;


public class SetPeer {
	private boolean debug = true;
	public GameData myGameData;
	public Player me;
	public P2PSet gui;
	public Communicator com;
	public boolean token;
	public boolean wantCS;
	private boolean tokenPassingStarted;
	
	public SetPeer(P2PSet _gui, String playerName){
		gui = _gui;
		me = new Player(playerName);
		com = new Communicator(this);
	}
	
	public GameData createNewGame(){
		myGameData = new GameData(me);
		com.players = myGameData.playerList;
		token = true;
		tokenPassingStarted = false;
		return myGameData;		
	}
	
	public void joinGame(){
		tokenPassingStarted = true;
		com.sendLOOKING_FOR_GAMES();
	}
	
	public void receiveLooking(Message m){
		requestCS();
		if (myGameData.playerList.indexOf(me) == 0)
			com.sendHERE_IS_A_GAME((String)m.getObjects().get(0), myGameData);
		releaseCS();
	}
	
	public void receiveHereIsAGame(Message m){
		myGameData = (GameData) m.getObjects().get(0);
		while (myGameData == null) debug ("busy waiting while game data loads");
		com.players = myGameData.playerList;
		com.sendNEW_PLAYER(me);
		myGameData.playerList.add(me);
		gui.myGameData = myGameData;
		gui.boardChanged();
	}//Here is a game!

	
	private void debug(String string) {
		if (debug) System.out.println(string);
		
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
			boolean shouldDealMore = (myGameData.deck.boardCards.size() != 15 && myGameData.deck.unusedCards.size() > 0);
			//myGameData.deck.removeSet(c1, c2, c3);
			Card[] cards;
			if (shouldDealMore){
				//int[] indicesToReplace = {myGameData.deck.boardCards.indexOf(c1), myGameData.deck.boardCards.indexOf(c2), myGameData.deck.boardCards.indexOf(c3)};
				cards = new Card[6];
				Card[] newCards = myGameData.deck.dealThreeCardsNoRemove();
				for (int i = 0; i < 3; i++){
					cards[3 + i] = newCards[i];
				}
			}//if we should deal more cards
			else{
				cards = new Card[3];
			}
			cards[0] = c1;
			cards[1] = c2;
			cards[2] = c3;
			com.sendI_CLAIM_SET(cards, me);
		}//reverify the set
		releaseCS();
	}
	
	public void receiveClaimSet(Message m){
		Player scorer = myGameData.playerList.get(myGameData.playerList.indexOf(m.getObjects().get(1)));
		scorer.score ++;
		myGameData.numPlayersWantCards = 0;
		gui.reqMoreCards.setSelected(false);
		Card[] cards =  (Card[])m.getObjects().get(0);
		gui.gameLog.append(scorer.name + " scores with Set: " + cards[0].toString() + " " + cards[1].toString() + " " + cards[2].toString() + "\n");
		if (myGameData.deck.boardCards.size() == 15 || myGameData.deck.unusedCards.size() == 0){
			myGameData.deck.removeSet(cards[0], cards[1], cards[2]);
			gui.gameLog.append("No new cards added \n" );
		}
		else {
			for (int i = 0; i < 3; i ++){
				myGameData.deck.replaceCard(cards[i = 3], cards[i]);
				gui.gameLog.append("Adding Card: " + cards[i + 3] + "\n");
			}
		}
		gui.boardChanged();
	}
	
	public void sendDeductMe(){
		
	}
	
	public void receiveDeduction(Message m){
		
	}
	
	public void askMoreCards(){
		requestCS();
		if (myGameData.deck.boardCards.size() == 12 && myGameData.deck.unusedCards.size() > 0){
			myGameData.numPlayersWantCards ++;
			if (myGameData.numPlayersWantCards >= (myGameData.playerList.size() / 2)){
				ArrayList<Serializable> data = new ArrayList<Serializable>();
				Card[] cards = new Card[3];
				for (int i = 0; i < 3; i++){
					cards = myGameData.deck.dealThreeCardsNoRemove();
				}
				data.add(cards);
				com.sendADDED_MORE_CARDS(data);
			}//if at least half of players (rounded down) now want more cards
			else {
				com.sendWANT_MORE_CARDS(myGameData.numPlayersWantCards);
			}
		} //if nothing happened since the request to make it invalid
		releaseCS();
	}
	
	public void receiveMoreCardsRequest(Message m){
		myGameData.numPlayersWantCards = (Integer)m.getObjects().get(0);
	}
	
	public void receiveMoreCardsAdded(Message m){
		Card[] cards = (Card[])m.getObjects().get(0);
		for (int i = 0; i < cards.length; i++){
			myGameData.deck.dealCard((cards[i]));
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
		if (!tokenPassingStarted) {
			tokenPassingStarted = true;
			releaseCS();
		}
		gui.boardChanged();
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
