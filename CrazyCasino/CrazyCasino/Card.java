import javax.swing.*;

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
public class Card extends JButton
{
	/**
	 *The standard width of a card.
	 */
	public static int WIDTH = 120;
	/**
	 *The standard height of a card.
	 */
	public static int HEIGHT = 180;
	/**
	 *The suit of this card.
	 */
	private String suit;
	/**
	 *The face value of this card. 1 for ace, 2 for deuce, etc, all the way up to 13 for a king.
	 */
	private int faceValue;
	/**
	 *Stores whether the card is flipped or not. True if the cards image is back.png, false otherwise.
	 */
	private boolean faceShowing;

	/**
	 *Creates a card as a JButton with an icon of the same size. The icon is appropriate image for its suit and face value.
	 *@param s 	the suit of the card
	 *@param fv the face value of the card
	 */
	public Card(String s, int fv)
	{
		super();
		setIcon(new ImageIcon(getClass().getResource("img/cards/" + s + fv + ".png")));
		suit = s;
		faceValue = fv;
		setSize(WIDTH, HEIGHT);
		faceShowing = true;
	}
	/**
	 *Flips the card. If the card is face down, icon is set to appropriate image
	 *for its suit and face value. If the card is face up, icon is set to back.png.
	 */
	public void flip()
	{
		if (faceShowing)
			setIcon(new ImageIcon(getClass().getResource("img/cards/back.png")));
		else
			setIcon(new ImageIcon(getClass().getResource("img/cards/" + suit + faceValue + ".png")));
		faceShowing = !faceShowing;
	}
	/**
	 *Gets the suit of this card.
	 *@return the suit of this card (either "hearts", "clubs", "spades", or "diamonds") as a string.
	 */
	public String getSuit()
	{
		return suit;
	}
	/**
	 *Gets the face value of this card.
	 *@return The face value of this card as in integer. A two will return 2, a three will return three, etc. Aces are valued high and will return 14, not 1.
	 */
	public int getFaceValue()
	{
		if(faceValue == 1 && Deck.fiveCard == true)
			return 14;
		return faceValue;
	}
	/**
	 *Checks to see if the cards face is showing (if the card is face up).
	 *@return False if the card is face down, that is, its icon is back.png. True otherwise
	 */
	public boolean getFaceShowing()
	{
		return faceShowing;
	}
}