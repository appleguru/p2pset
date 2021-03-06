import java.io.Serializable;	//Import necessary classes
import java.util.ArrayList;

/**
 * This class contains control methods for what the peer is doing.  The very core of our program.
 * @author Ari
 *
 */
public class SetPeer {
	private boolean debug = false;
	public GameData myGameData;
	public Player me;
	public P2PSet gui;
	public Communicator com;
	public boolean token;
	public boolean wantCS;
	private boolean tokenPassingStarted;

	/**
	 * Constructor for SetPeer.
	 * @param _gui Reference to the GUI object
	 * @param playerName Name of this player
	 */
	public SetPeer(P2PSet _gui, String playerName){
		gui = _gui;
		me = new Player(playerName);
		com = new Communicator(this);
	}
	
	/**
	 * Creates a new game of Set.
	 */
	public void createNewGame(){
		myGameData = new GameData(me);
		com.players = myGameData.playerList;
		gui.myGameData = myGameData;
		token = true;
		tokenPassingStarted = false;
		gui.boardChanged();		
	}
	
	/**
	 * Joins an existing game on the network.  If no games are found after 6 seconds, start a new game.
	 */
	public void joinGame(){
		com.sendLOOKING_FOR_GAMES();
		try {
			Thread.sleep(6000);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
		if (myGameData == null){
			createNewGame();
		}
	}
	
	/**
	 * Called when the peer receives a "LOOKING!" message.  Tries to start a conversation with the new player.
	 * @param m
	 */
	public void receiveLooking(Message m){
		if (myGameData.playerList.indexOf(me) == 0){
			requestCS();
			String noob = (String)m.getObjects().get(0);
			com.sendWHO_ARE_YOU(noob, me);
		}
	}
	
	/**
	 * Respond to a WhoAreYou message by sending information about this player.
	 * @param m
	 */
	public void receiveWhoAreYou(Message m){
		tokenPassingStarted = true;
		Player liason = (Player)m.getObjects().get(0);
		com.sendI_AM_ME(liason, me);
	}
	
	/**
	 * Respond to an IAmMe message.
	 * @param m
	 */
	public void receiveIAmMe(Message m){
		Player noob = (Player )m.getObjects().get(0);
		com.sendNEW_PLAYER(noob, me);
		myGameData.playerList.add(noob);
		com.sendHERE_IS_A_GAME(noob.ip.getHostAddress(), myGameData);
		gui.log(noob.name + " has joined the game.");
		gui.boardChanged();
		releaseCS();
	}

	/**
	 * Respond to a HereIsAGame message.
	 * @param m
	 */
	public void receiveHereIsAGame(Message m){
		myGameData = (GameData) m.getObjects().get(0);
		com.players = myGameData.playerList;
		gui.myGameData = myGameData;
		gui.boardChanged();
	}//Here is a game!

	/**
	 * Prints a line of debug text, but only if the debug flag is set to true.
	 * @param string Debug text to print
	 */
	private void debug(String string) {
		if (debug) System.out.println(string);
		
	}
	
	public void claimSet(Card c1, Card c2, Card c3){
		debug(new Boolean(token).toString());
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
		else {
			gui.log("Someone else found that set first. ");
		}
		releaseCS();
	}
	
	public void receiveClaimSet(Message m){
		Player scorer = myGameData.playerList.get(myGameData.playerList.indexOf(m.getObjects().get(1)));
		scorer.score ++;
		myGameData.numPlayersWantCards = 0;
		gui.reqMoreCards.setSelected(false);
		Card[] cards =  (Card[])m.getObjects().get(0);
		gui.log(scorer.name + " scores with Set: " + cards[0].toString() + " " + cards[1].toString() + " " + cards[2].toString());
		if (myGameData.deck.boardCards.size() == 15 || myGameData.deck.unusedCards.size() == 0){
			myGameData.deck.removeSet(cards[0], cards[1], cards[2]);
			gui.log("No new cards added" );
		}
		else {
			for (int i = 0; i < 3; i ++){
				myGameData.deck.replaceCard(cards[i + 3], cards[i]);
				gui.log("Adding Card: " + cards[i + 3]);
			}
		}
		if (myGameData.deck.gameFinished()){
			//TODO
			String winner = "";
			int win = -1;
			for (Player p : myGameData.playerList){
				if (p.score > win){
					win = p.score;
					winner = p.name;
				}
				else if (p.score == win){
					winner = (winner + " ties with " + p.name);
				}
			}
			gui.log("Game Over.  Winner(s): " + winner + " with score: " + win);
		}//if there are no more sets to find
		gui.boardChanged();
	}
	
	public void sendDeductMe(){
		requestCS();
		com.sendDEDUCTION(me);
		releaseCS();
	}
	
	public void receiveDeduction(Message m){
		Player dunce = (Player)m.getObjects().get(0);
		dunce = myGameData.playerList.get(myGameData.playerList.indexOf(dunce));
		gui.log(dunce.name + " was deducted a point for making an invalid claim.");
		dunce.score --;
		gui.boardChanged();
	}
	
	public void askMoreCards(){
		requestCS();
		if (myGameData.deck.boardCards.size() == 12 && myGameData.deck.unusedCards.size() > 0){
			myGameData.numPlayersWantCards ++;
			debug (myGameData.numPlayersWantCards + "players now want more cards, out of " + myGameData.playerList.size());
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
		gui.log(myGameData.numPlayersWantCards + " players now want to add more cards.");
		gui.boardChanged();
	}
	
	public void receiveMoreCardsAdded(Message m){
		Card[] cards = (Card[])m.getObjects().get(0);
		
		gui.log("At least half of players now want more cards.  ");
		for (int i = 0; i < cards.length; i++){
			myGameData.deck.dealCard((cards[i]));
			gui.log("Adding card " + cards[i].toString());
		}
		gui.reqMoreCards.setSelected(false);
		myGameData.numPlayersWantCards = 0;
		gui.boardChanged();
	}
	
	public void Quit(){
		requestCS();
		
		com.sendQUIT(me);
		
		releaseCS();
		System.exit(0);
	}
	
	public void recieveQuit(Message m){
		Player quitter = (Player)m.getObjects().get(0);
		
		myGameData.playerList.remove(quitter);
		gui.log(quitter.name + " has left the game.");
		gui.boardChanged();
	}
	
	public void receiveNewPlayer(Message m){
		Player noob = (Player)m.getObjects().get(0);
		myGameData.playerList.add(noob);
		if (!tokenPassingStarted) {
			tokenPassingStarted = true;
			releaseCS();
		}
		gui.log(noob.name + " has joined the game.");
		gui.boardChanged();
	}
	
	public Message readMessage(){
		return com.receiveMsg();
	}
	
	public synchronized void requestCS()
	{
		wantCS=true;
		if(!token)
			myWait(this);
	}
	
	public synchronized void releaseCS()
	{
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wantCS = false;
		if(myGameData.playerList.size() > 1)
		{
			token = false;
			int idx = myGameData.playerList.indexOf(me);
			int i = (idx+1)%myGameData.playerList.size();
			debug ("passing the token to " + myGameData.playerList.get(i).name);
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
		debug("token was " + Boolean.toString(token) + " at the beginning of recieve token");
		
		token = true;
		if(!wantCS){
			releaseCS();
		}
	}
}
