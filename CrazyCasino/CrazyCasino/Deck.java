/**
 *(c) 2012 Bohde Productions Inc. All Rights Reserved.
 *
 *This class creates the main interface used to play the game.
 *It contains the menus and code structure to manage the players
 *money throughout the casino and dealing with switching games,
 *creating games, and quitting games.
 *
 *@author Mr. Segall
 *@author David Ingersoll
 *@author Tommy Bohde
 *@author Neil B. Patel
 *
 *@version 1.0 %G%
 */
public class Deck
{
	/**
	 *The standard 4 suits of a deck of cards: Hearts, Clubs, Spades, and Diamonds
	 */
	public static final String[] SUITS = {"hearts", "clubs", "spades", "diamonds"};
	/**
	 *The standard value of cards in a deck: 52.
	 */
	public static final int NUM_CARDS = 52;
	/**
	 *True if the deck was created by a Five Card Draw table. Enables the firing of ActionEvents on all cards in the deck.
	 */
	public static boolean fiveCard;
	/**
	 *The cards in this deck.
	 *@see #NUM_CARDS
	 */
	private Card[] cards;
	
	/**
	 *Creates a new standard shuffled deck of 52 cards.
	 *@param fiveCard should be <code>true</code> if this deck is being used at a Five Card Draw table. Enables the firing of ActionEvents on all cards in the deck.
	 */
	public Deck(boolean fiveCard)
	{
		this.fiveCard = fiveCard;
		cards = new Card[NUM_CARDS];
		for(int i=0;i<NUM_CARDS;i++)
		{
			cards[i]=new Card(SUITS[(int)(i/13)],(i%13)+1);
		}
		shuffle();
	}
	/**
	 *Simulates the dealing of a card, then shifts all remaining cards to the beginning of the array.
	 *@return A <code>Card</code> object simulating the dealt card. <code>null</code> will be returned if there are no cards remaining.
	 */
	public Card deal()
	{
		if(cards[0] == null)
			return null;
		Card temp = cards[0];
		for(int i=1; i<cards.length; i++)
		{
			cards[i-1] = cards[i];
			if(i == 51)
			{
				cards[i] = null;
			}
		}
		return temp;
	}
	/**
	 *Shuffles the deck. Contains an off-by-one error; the 52nd card will never be shuffled.
	 */
	public void shuffle()
	{
		for(int i=0;i<NUM_CARDS;i++)
		{
			Card temp;
			int num=(int)(Math.random()*52);
			temp=cards[i];
			cards[i]=cards[num];
			cards[num]=temp;
		}
	}
}