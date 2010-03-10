import java.io.Serializable;	//Import necessary classes
import java.util.*;

/**
 * This class represents a full deck of Set cards.  It contains various methods for manipulating the cards in the deck.
 * @author Ari
 *
 */
public class Deck implements Serializable
{
	private static final long serialVersionUID = 4441183230181133146L;
	LinkedList<Card> unusedCards;	//Stores cards that have not been dealt yet
	LinkedList<Card> boardCards;	//Stores cards that are on the board
	LinkedList<Card> usedCards;	//Stores cards that players have taken as sets
	Random generator;
	/**
	 * Constructor; used to create a new random number generator for dealing cards
	 */
	public Deck()
	{
		generator = new Random();
		unusedCards = new LinkedList<Card>();
		boardCards = new LinkedList<Card>();
		usedCards = new LinkedList<Card>();
		
		for (int i = 0; i < Card.NUM_ATTR_TYPES; i++){
			for (int j = 0; j < Card.NUM_ATTR_TYPES; j++){
				for (int k = 0; k < Card.NUM_ATTR_TYPES; k++){
					for (int l = 0; l < Card.NUM_ATTR_TYPES; l++){
						unusedCards.add(new Card(i, j, k, l));
					}
				}
			}
		}
		for (int i = 0; i < 12; i++){
			boardCards.add(dealCard());
		}//deal out the initial 12 cards
	}
	
	/**
	 * Deals a random card from unusedCards, and moves that card into boardCards.
	 * @return Returns a random card selected from unusedCards.
	 */
	public Card dealCard()
	{
		int size = unusedCards.size();	//Get the number of undealt cards
		if (size == 0){
			return null;
		}
		
		int cardNum = generator.nextInt(size);	//Get a random integer from 0 to this number
		Card dealtCard = unusedCards.get(cardNum);	//Get the card at this index
		unusedCards.remove(cardNum);	//Remove this card from the unused cards list
		//boardCards.add(dealtCard);	//Add this card to the dealt cards list
		return dealtCard;	//Return the card that was dealt
	}

	/**
	 * Picks out a card from the deck and returns it to the user without removing it from the undealt cards list. 
	 */
	public Card dealCardNoRemove(){
		int size = unusedCards.size();
		if (size == 0){
			return null;
		}
		
		int cardNum = generator.nextInt(size);
		return unusedCards.get(cardNum);
	}

	/**
	 * Returns an array of three distinct cards from the unused cards list.
	 * @return An array of three distinct cards from the unused cards list.
	 */
	public Card[] dealThreeCardsNoRemove(){
		Card[] result = new Card[3];
		for (int i = 0 ; i < 3; i++){
			result[i] = dealCard();
		}
		for (int i = 0; i < 3; i++){
			unusedCards.add(result[i]);
		}
		return result;
	}
	
	/**
	 * Deals the given card by moving it from undealt cards to the board.
	 * @param c Card to deal
	 */
	public void dealCard(Card c){
		if (unusedCards.contains(c)){
			unusedCards.remove(c);
			boardCards.add(c);
		}
	}
	
	/**
	 * Moves a card from the board to the used cards pile
	 * @param c
	 * @param i
	 */
	public void replaceCard(Card c, int i){
		usedCards.add(boardCards.get(i));
		unusedCards.remove(c);
		boardCards.set(i, c);
	}
	
	public void replaceCard(Card newCard, Card oldCard){
		usedCards.add(oldCard);
		unusedCards.remove(newCard);
		boardCards.set(boardCards.indexOf(oldCard), newCard);
	}
	
	/**
	 * Verifies whether the specified group of cards is a set.
	 * @param card1 First card
	 * @param card2 Second card
	 * @param card3 Third card
	 * @return True if the cards form a set and are on the board, false otherwise.
	 */
	public boolean verifySet(Card card1, Card card2, Card card3)
	{
		Card[] claim = {card1, card2, card3};
		
		for (int i = 0; i < claim.length; i++){
			if (!boardCards.contains(claim[i])){
				return false;
			}//return false if the card ain't there
		}//verify that each card is actually on the board
		
		boolean[] allSame = new boolean[Card.NUM_ATTRS];
		boolean[] allDiff = new boolean[Card.NUM_ATTRS];
		
		for (int i = 0; i < Card.NUM_ATTRS; i++){
			allSame[i] = true; //assume true until finding a contradiction
			allDiff[i] = true; //assume true until finding a contradiction
			for (int j = 0; j < claim.length; j++){
				for (int k = 1; k < claim.length; k++){
					if (claim[j].attributes[i] == claim[(j + k) % claim.length].attributes[i]){
						allDiff[i] = false; //then all Different must be false for this attribute
					}//if the cards share the given attribute
					
					else {
						allSame[i] = false; //otherwise, then allSame must be false for this attribute
					}
				}//for each card it can be compared to
			}//for each card
			
			if (!allSame[i] && !allDiff[i]){
				return false;
			}//if, for this attribute, the cards are not all the same, and not all different
		}//for each attribute check to see whether each card is the same, or all different
		
		return true; //if we never found any contradictions, return true
	}
	
	/**
	 * Moves the set from boardCards to usedCards.
	 * @param card1 First card
	 * @param card2 Second card
	 * @param card3 Third card
	 */
	public void removeSet(Card card1, Card card2, Card card3)
	{
		int card1Index = boardCards.indexOf(card1);	//Get the index in the list of board cards of each card to be removed
		int card2Index = boardCards.indexOf(card2);
		int card3Index = boardCards.indexOf(card3);
		usedCards.add(boardCards.get(card1Index));	//Add each card to the list of used cards
		usedCards.add(boardCards.get(card2Index));
		usedCards.add(boardCards.get(card3Index));
		
		if (boardCards.size() == 12 && unusedCards.size() > 0){
			boardCards.set(card1Index, dealCard());	//Remove each card from the list of cards on the board
			boardCards.set(card2Index, dealCard());
			boardCards.set(card3Index, dealCard());
		}//if we can deal more cards
		else {
			assert (boardCards.size() == 15 || unusedCards.size() == 0);
			boardCards.remove(card1);
			boardCards.remove(card2);
			boardCards.remove(card3);
		}//otherwise remove the cards without replacing them
	}

	/**
	 * Determines whether the game is over yet.
	 * @return A boolean representing the state of the game
	 */
	public boolean gameFinished(){
		if (unusedCards.size() > 0){	//Game isn't over if there are undealt cards
			return false;
		}
		else if (boardCards.size() == 15){	//Game isn't over if there are 15 cards on the board (since there must be a set)
			return false;
		}
		else{
			for (int i = 0; i < boardCards.size(); i++){
				for (int j = i + 1; j < boardCards.size(); j++){
					for (int k = j + 1; k < boardCards.size(); k++){
						if (verifySet(boardCards.get(i), boardCards.get(j), boardCards.get(k))){
							return false;
						}
					}
				}
			}//for each possible combination of cards
			return true;
		}
	}

	/**
	 * Removes all null cards from the board.
	 */
	public void removeNulls(){
		for (int i = boardCards.size(); i >= 0; i--){
			if (boardCards.get(i) == null){
				boardCards.remove();
			}
		}
	}//removes all the null Cards from the board
	
	/**
	 * Compares this deck to another one.  Returns true if they are equivalent.
	 */
	public boolean equals (Object o){
		Deck other = (Deck)o;
		
		return (unusedCards.equals(other.unusedCards) && boardCards.equals(other.boardCards) && usedCards.equals(other.usedCards));
	}
	
	/**
	 * Return a string representation of this deck.
	 */
	public String toString(){
		return ("Unused Cards: " + unusedCards.toString() + ". Board Cards: " + boardCards.toString() + ". Used Cards: " + usedCards.toString());
	}
}//class
