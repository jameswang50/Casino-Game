import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 *(c) 2012 Bohde Productions Inc. All Rights Reserved.
 *
 *This class creates the main interface used to play the game.
 *It contains the menus and code structure to manage the players
 *money throughout the casino and dealing with switching games,
 *creating games, and quitting games.
 *
 *@author David Ingersoll
 *@author Tommy Bohde
 *@author Neil B. Patel
 *
 *@version 1.0 %G%
 *
 *TODO
 *	 Tommy
 *		-Fix repainting issues
 *		-Fancy up background
 */
public class Blackjack extends Container implements ActionListener
{
	/**
	 *Background image
	 */
	private final JLabel BACKGROUND = GameFrame.BACKGROUND_BJ;
	/**
	 *The pixel coordinate along the x-axis where the leftmost edge of the leftmost card will be placed
	 */
	private final int START_INDEX = 20;
	/**
	 *The "Hit" button. Live on the edge!
	 */
	private JButton hit;
	/**
	 *The "Stand" button
	 */
	private JButton stand;
	/**
	 *The player's hand
	 */
	private ArrayList<Card> hand;
	/**
	 *The dealer's hand
	 */
	private ArrayList<Card> dHand;
	/**
	 *The pixel to place the next player's card
	 */
	private int cardIndex;
	/**
	 *The pixel to place the next dealer's card
	 */
	private int dCardIndex;
	/**
	 *The deck of cards
	 */
	private Deck d;
	/**
	 *The value of the player's hand
	 */
	private int handValue;
	/**
	 *The value of the dealer's hand
	 */
	private int dHandValue;
	/**
	 *The user's bet
	 */
	private int bet;
	/**
	 *Label displaying how much money you have in your pocket
	 */
	private JLabel betPrint;
	/**
	 *Label displaying how much money you have on the table (wagered)
	 */
	private JLabel onTable;
	/**
	 *Label displaying the player's status, such as "Standing at " concatenated with {@link #handValue}
	 */
	private JLabel playerStatus;
	/**
	 *Label displaying the player's status, such as "Standing at " concatenated with {@link #dHandValue}
	 */
	private JLabel dealerStatus;
	/**
	 *Used to control the dealers first card being flipped initially, so it cannot be seen until after the user has played
	 */
	private boolean flipMe;

	/**
	 *Sets up a <code>Container</code> which will be placed into the existing
	 *<code>GameFrame</code> as its content pane. Represents a Blackjack table.
	 */
	public Blackjack()
	{
		super();
		setLayout(null);
		setSize(1024,640);
		BACKGROUND.setSize(1024, 640);
		
		hit = new JButton("Hit");
		stand = new JButton("Stand");
		hand = new ArrayList<Card>();
		dHand = new ArrayList<Card>();
		d = new Deck(false);
		cardIndex = START_INDEX;
		dCardIndex = START_INDEX;

		hit.setSize(100,30);
		stand.setSize(100,30);
		
		add(hit);
		hit.setLocation(this.getWidth()/2-(hit.getWidth()+5),0);
		add(stand);
		stand.setLocation(getWidth()/2,0);

		//"You have: " label
		betPrint = new JLabel("You have: $" + GameFrame.cash);
		betPrint.setFont(betPrint.getFont().deriveFont((float)24));
		betPrint.setForeground(Color.CYAN);
		betPrint.setSize(350, 25);
		betPrint.setLocation(5, 5);
		add(betPrint);
		//"On table: " label
		onTable = new JLabel("On table: $" + bet);
		onTable.setFont(onTable.getFont().deriveFont((float)24));
		onTable.setForeground(Color.CYAN);
		onTable.setSize(350, 25);
		onTable.setLocation(5, 30);
		add(onTable);
	
		validate();
		repaint();
		makeWager();
		
		flipMe = true;
		addCard(d.deal());
		dAddCard(d.deal());
		addCard(d.deal());
		dAddCard(d.deal());

		//Player status
		playerStatus = new JLabel("Total: " + getHandValue(hand));
		playerStatus.setFont(playerStatus.getFont().deriveFont((float)24));
		playerStatus.setForeground(Color.CYAN);
		playerStatus.setSize(200, 30);
		playerStatus.setLocation(824, 5);
		add(playerStatus);
		//Dealers status
		dealerStatus = new JLabel("Dealer waits");
		dealerStatus.setFont(dealerStatus.getFont().deriveFont((float)24));
		dealerStatus.setForeground(Color.CYAN);
		dealerStatus.setSize(200, 30);
		dealerStatus.setLocation(824, 35);
		add(dealerStatus);

		hit.addActionListener(this);
		stand.addActionListener(this);
		add(BACKGROUND);
	}
	/**
	 *Convenience method for adding a card to the players ArrayList of cards and their side of the table.
	 *@param c the <code>Card</code> to be added
	 */
	public void addCard(Card c)
	{
		hand.add(c);
		remove(BACKGROUND);
		add(hand.get(hand.size()-1));
		hand.get(hand.size()-1).setLocation(cardIndex, 100);
		add(BACKGROUND);
		cardIndex+=123;
		repaint();
	}
	/**
	 *Convenience method for adding a card to the dealers ArrayList of cards and their side of the table.
	 *@param c the <code>Card</code> to be added
	 */
	public void dAddCard(Card c)
	{
		dHand.add(c);
		remove(BACKGROUND);
		if (flipMe)
		{
			c.flip();
			flipMe = false;
		}
		add(dHand.get(dHand.size()-1));
		dHand.get(dHand.size()-1).setLocation(dCardIndex, 340);
		add(BACKGROUND);
		dCardIndex+=123;
		repaint();
	}
	/**
	 *Calculates the total value of a hand (passed in as a parameter) to calculate
	 *the round winner or determine the action for the dealer to take.
	 *@param list the ArrayList of cards representing a hand
	 *@return an integer value representing the total face value of all the cards in the hand
	 */
	private int getHandValue(ArrayList<Card> list)
	{
		int total = 0;
		int numAces = 0;

		for(Card c : list)
		{
			int temp = c.getFaceValue();

			if (temp > 10)
				temp = 10;
			if (temp == 1)
			{
				temp = 11;
				numAces++;
			}

			total += temp;
		}
		while (total > 21 && numAces > 0)
		{
			total -= 10;
			numAces -= 1;
		}
		return total;
	}
	/**
	 *Prompts the user to enter their bet in a dialog box. Input is taken as a <code>String</code> and
	 *parsed using <code>Integer.parseInt()</code>. If the input if not valid, an exception is thrown and
	 *caught and the user is prompted continously until appropriate input is provided. Invalid input includes
	 *text, a negative value, zero, or a value that is greater than the amount of money the user has.
	 */
	private void makeWager()
	{
		String w;
		int wager = 0;

		while (true)
		{
			//Gets wager from user
			try
			{
				w = JOptionPane.showInputDialog(
						 /*Parent component*/  this,
						 /*Message*/		   "Make your wager.\nYou have $" + GameFrame.cash,
						 /*Title message*/	   "Time to lose money! :D",
						 /*Dialog type*/	   JOptionPane.QUESTION_MESSAGE
											   );
				if (w == null)
				{
					JOptionPane.showMessageDialog(
												 this,
												 "",
												 "THUNDERDOME!",
												 JOptionPane.WARNING_MESSAGE,
												 GameFrame.FIGHTME
												 );
					continue;
				}
				wager = Integer.parseInt(w);
				if (wager > GameFrame.cash)	throw new RuntimeException("0");
				if (wager <= 0) throw new RuntimeException("1");
			}
			//Deals with input not being an integer
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(
												 this,
												 "",
												 "THUNDERDOME!",
												 JOptionPane.WARNING_MESSAGE,
												 GameFrame.FIGHTME
												 );
				continue;
			}
			break;
		}
		//Takes your money >:D
		bet = wager;
		GameFrame.cash -= bet;
		betPrint.setText("You have: $" + GameFrame.cash);
		onTable.setText("On Table: $" + bet);
		repaint();
		
	}
	/**
	 *Convenience method for displaying a <code>JOptionPane</code> message dialog.
	 *@param message		the message to be displayed in the dialog box
	 *@param titleMessage	the message to be displayed in the dialog's title bar
	 *@param type			the type of message being displayed; affects the icon which is shown in the dialog. It is preferable to use a field of <code>JOptionPane</code>.
	 *@see JOptionPane#ERROR_MESSAGE
	 *@see JOptionPane#INFORMATION_MESSAGE
	 *@see JOptionPane#WARNING_MESSAGE
	 *@see JOptionPane#QUESTION_MESSAGE
	 *@see JOptionPane#PLAIN_MESSAGE
	 */
	private void showMsgDlg(String message, String titleMessage, int type)
	{
		JOptionPane.showMessageDialog(this, message, titleMessage, type);
	}
	/**
	 *Checks to see if a hand is bust (has a value of greater than 21).
	 *@param list the hand to check if bust
	 *@return <code>true</code> if the hand has a value of greater than 21, otherwise <code>false</code>.
	 *@see #getHandValue(ArrayList list)
	 */
	private boolean isBust(ArrayList<Card> list)
	{
		return (getHandValue(list) > 21);
	}
	/**
	 *Gameplay of the dealer. Implements standard soft 17 casino rules: hit under 16, stand on or above 17.
	 *@see #dAddCard(Card)
	 *@see #getHandValue(ArrayList)
	 *@see #isBust(ArrayList)
	 */
	public void dealerTurn()
	{
		while(getHandValue(dHand) < 17)
		{
			dAddCard(d.deal());
			dealerStatus.setText("Total: " + getHandValue(dHand));
		}
		if (isBust(dHand))
			dealerStatus.setText("Bust!");
		dealerStatus.setText("Standing at " + getHandValue(dHand));
	}
	/**
	 *Determines the outcome of the hand and who won, and calls the appropriate method.
	 *@see #lost()
	 *@see #victory()
	 *@see #push()
	 *@see #dealerBJ()
	 */
	public void end()
	{
		if (getHandValue(dHand) == 21 && dHand.size() == 2 && (dHand.get(0).getFaceValue() > 10 || dHand.get(1).getFaceValue() > 10))
			dealerBJ();
		else if (isBust(hand))
			lost();
		else if (isBust(dHand))
			victory();
		else if (getHandValue(hand) < getHandValue(dHand))
			lost();
		else if (getHandValue(hand) > getHandValue(dHand))
			victory();
		else
			push();
	}
	/**
	 *Deals with player losing. Informs them in a dialog box, then kicks them out if
	 *they are bankrupt or prompts them to either play again or return to the main screen.
	 */
	private void lost()
	{
		GameFrame.boot();
		//Prompts user about new game in a dialog box
		Object[] options = {"Play Again :D", "Quit D:"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Play again.\nDO IT AND YOU'RE COOL!",
					/*Dialog title*/	    "You lost :(",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.SAD,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of blackjack
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			Blackjack bj = new Blackjack();
			removeAll();
			add(bj);
			repaint();
		}
		//Resets to original screen
		else
		{
			resetBackground();
		}
	}
	/**
	 *Deals with player winning. Informs them in a dialog box, then kicks them out if they have
	 *bankrupt the casino or prompts them to either play again or return to the main screen.
	 *@see GameFrame#bankrupt()
	 */
	private void victory()
	{
		//Ok....you won :(
		if (Integer.MAX_VALUE - bet*2 < GameFrame.cash)
			GameFrame.bankrupt();
		else
			GameFrame.cash += bet*2;
		//Prompts user about new game in a dialog box
		Object[] options = {"Play Again :D", "Quit D:"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Play again.\nDO IT AND YOU'RE COOL!",
					/*Dialog title*/	    "You won :D",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.SMILEY,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of blackjack
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			Blackjack bj = new Blackjack();
			removeAll();
			add(bj);
			repaint();
		}
		//Resets to original screen
		else
		{
			resetBackground();
		}
	}
	/**
	 *Deals with a push (tie). Informs them in a dialog box, then kicks them out if they have
	 *bankrupt the casino or prompts them to either play again or return to the main screen.
	 *@see GameFrame#bankrupt()
	 */
	private void push()
	{
		//Stalemate.
		if (Integer.MAX_VALUE - bet < GameFrame.cash)
			GameFrame.bankrupt();
		else
			GameFrame.cash += bet;
		//Prompts user about new game in a dialog box
		Object[] options = {"Play Again :D", "Quit D:"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Play again.\nDO IT AND YOU'RE COOL!",
					/*Dialog title*/	    "It's a tie :O",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.VOMIT,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of blackjack
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			Blackjack bj = new Blackjack();
			removeAll();
			add(bj);
			repaint();
		}
		//Resets to original screen
		else
		{
			resetBackground();
		}
	}
	/**
	 *Called when the dealer has blackjack. A dialog box is displayed informing the player. If the
	 *player is bankrupt, he is removed from the casino and the game is closed. The user is then
	 *prompted via a dialog box to either play another hand or quit, returning to the main screen.
	 */
	private void dealerBJ()
	{
		GameFrame.boot();
		//Prompts user about new game in a dialog box
		Object[] options = {"Play Again :D", "Quit D:"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Well,\n\nThat's unlucky.",
					/*Dialog title*/	    "Dealer has blackjack!",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.GRUMPY,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of blackjack
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			Blackjack bj = new Blackjack();
			removeAll();
			add(bj);
			repaint();
		}
		//Resets to original screen
		else
		{
			resetBackground();
		}
	}
	/**
 	 *Resets original main screen. Used when user chooses to leave table between hands.
 	 *@see GameFrame#cash
 	 *@see GameFrame#youHave
 	 *@see GameFrame#showMoney
 	 *@see GameFrame#BACKGROUND
 	 */
	private void resetBackground()
	{
		removeAll();
		add(GameFrame.youHave);
		GameFrame.showMoney.setText("$" + GameFrame.cash);
		add(GameFrame.showMoney);
		add(GameFrame.BACKGROUND);
		GameFrame.resetMenus();
		repaint();
	}
	/**
	 *Handles actions processed on objects using <code>this</code> as their ActionListener
	 *@param e An ActionEvent containing the action which has taken place
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == hit)
		{
			addCard(d.deal());
			playerStatus.setText("Total: " + getHandValue(hand));
			if(isBust(hand))
			{
				Card x = dHand.get(0);
				remove(x);
				x.flip();
				remove(BACKGROUND);
				x.setLocation(20, 340);
				add(x);
				add(BACKGROUND);
				repaint();
				playerStatus.setText("Bust!");
				remove(hit);
				remove(stand);
				end();
			}
		}
		else if (e.getSource() == stand)
		{
			playerStatus.setText("Standing at " + getHandValue(hand));
			remove(hit);
			remove(stand);
			remove(BACKGROUND);
			Card x = dHand.get(0);
			remove(x);
			x.flip();
			remove(BACKGROUND);
			x.setLocation(20, 340);
			add(x);
			add(BACKGROUND);
			repaint();
			dealerTurn();
			end();
		}
	}
}