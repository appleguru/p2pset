import java.util.*;

public class Deck {

	LinkedList<Card> unusedCards = new LinkedList<Card>();
	LinkedList<Card> boardCards = new LinkedList<Card>();
	LinkedList<Card> usedCards = new LinkedList<Card>();

	/**
	 * Deals a random card from unusedCards, and moves that card into boardCards.
	 * @return Returns a random card selected from unusedCards.
	 */
	public Card dealCard()
	{
		return new Card();
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
		return false;
	}
	
	/**
	 * Moves the set from boardCards to usedCards.
	 * @param card1 First card
	 * @param card2 Second card
	 * @param card3 Third card
	 */
	public void removeSet(Card card1, Card card2, Card card3)
	{
		
	}
}//class
