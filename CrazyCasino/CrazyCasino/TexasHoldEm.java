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
 *@author Tommy Bohde
 *@author David Ingersoll
 *@author Neil B. Patel
 *
 *@version 1.0 %G%
 *
 *TODO
 *	 DAVID
 *	 	-evaluate hands
 *	 	-fold functionality (BUGGY)
 *	 	-intelligent random computer betting
 *	 Tommy
 *		-finish commenting
 */
public class TexasHoldEm extends Container implements ActionListener
{
	/**
	 *Location of small blind
	 */
	private static int blindSpot = 1;
	/**
	 *Value of small blind
	 */
	private final int SMALL_BLIND = 25;
	/**
	 *Background image
	 */
	private final JLabel BACKGROUND = GameFrame.BACKGROUND_TX;
	/**
	 *
	 */
	private final JButton takeTurn = new JButton("Take Turn");
	/**
	 *An array holding the amount of money each player has. The user's money is <code>monies[0]</code>. The computer's
	 *monies (<code>monies[1], monies[2], monies[3]</code>) are all initialized each hand to <code>Integer.MAX_VALUE</code>
	 *to prevent them running out of money during a hand.
	 */
	private int[] monies = {GameFrame.cash, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	/**
	 *2D Array of cards to hold each players hand
	 */
	private Card[][] hand = new Card[4][2];
	/**
	 *The river. Cards will be added to this in {@link #deal()}.
	 */
	private ArrayList<Card> river;
	/**
	 *The bets of each player, flushed to the pot after the round.
	 */
	private ArrayList<Integer> bets;
	/**
	 *Label displaying how much money you have
	 */
	private JLabel betPrint;
	/**
	 *Label displaying how much money you have on the table (wagered)
	 */
	private JLabel onTable;
	/**
	 *The pot: the total wagered money to be won at the end of the hand
	 */
	private int pot;
	/**
	 *The location of the player who is currently betting
	 */
	private int betIndex;
	/**
	 *The number of players who have bet; used to ensure that every round of betting provides each player a chance to bet a minimum of once
	 */
	private int haveBet;
	/**
	 *The deck used in this game of Texas Hold Em
	 */
	private Deck deck;
	/**
	 *The button that will either make the user call, or allow them to check, depending on which is legal
	 */
	private JButton moveOn = new JButton();
	/**
	 *Used to prevent computers from continuing playing after user has folded
	 */
	private boolean hasFolded;

	/**
	 *Creates a new game of Texas Hold Em, calling the appropriate methods to begin the game.
	 */
	public TexasHoldEm()
	{
		super();
		setLayout(null);
		setSize(1024,640);
		takeTurn.setSize(100, 30);
		takeTurn.setLocation(5, 5);
		bets = new ArrayList<Integer>();
		for (int i=0; i<4; i++)
			bets.add(new Integer(0));
		if(blindSpot<3) blindSpot++;
		else blindSpot=0;
		betIndex=blindSpot+2;
		haveBet=0;
		deck = new Deck(false);
		river = new ArrayList<Card>();
		BACKGROUND.setSize(1024, 640);
		dealHands();
		takeTurn.addActionListener(this);
		takeTurn.setSize(100,30);
		takeTurn.setLocation(915, 2);
		add(takeTurn);
		//"You have: " label
		betPrint = new JLabel("You have: $" + GameFrame.cash);
		betPrint.setFont(betPrint.getFont().deriveFont((float)24));
		betPrint.setForeground(Color.CYAN);
		betPrint.setSize(350, 25);
		betPrint.setLocation(5, 5);
		add(betPrint);
		//"On table: " label
		onTable = new JLabel("Pot: $" + pot);
		onTable.setFont(onTable.getFont().deriveFont((float)24));
		onTable.setForeground(Color.CYAN);
		onTable.setSize(350, 25);
		onTable.setLocation(5, 30);
		add(onTable);
		add(BACKGROUND);
		takeBlinds();
		repaint();
		computerBet();
		hasFolded = false;
	}
	/**
	 *Deals each player 2 cards, and flips the computer's cards face down.
	 *@see Card#flip()
	 */
	private void dealHands()
	{
		for (int i=0; i<4; i++)
		{
			for (int j=0; j<2; j++)
			{
				hand[i][j] = deck.deal();
				hand[i][j].setSize(120, 180);
			}
		}

		//North
		hand[0][0].setLocation(389, 5);
		hand[0][1].setLocation(512, 5);
		//East
		hand[1][0].setLocation(856, 107);
		hand[1][1].setLocation(856, 290);
		//South
		hand[2][0].setLocation(389, 397);
		hand[2][1].setLocation(512, 397);
		//West
		hand[3][0].setLocation(50, 107);
		hand[3][1].setLocation(50, 290);

		//Hides computer cards and then adds all cards
		for (int i=0; i<4; i++)
		{
			for (int j=0; j<2; j++)
			{
				if (i > 0)
					hand[i][j].flip();
				add(hand[i][j]);
			}
		}
	}
	/**
	 *Takes the big and small blinds from the appropriate players money and adds it to the pot.
	 */
	private void takeBlinds()
	{
		monies[blindSpot]-=SMALL_BLIND;
		if(blindSpot==3)
		{
			monies[0]-=SMALL_BLIND*2;
			blindSpot=0;
		}
		else
		{
			monies[blindSpot+1]-=SMALL_BLIND*2;
			blindSpot++;
		}
		pot += 75;
		betPrint.setText("You have: $" + GameFrame.cash);
		onTable.setText("Pot: $" + pot);
	}
	/**
	 *Checks to see if gameplay can continue.
	 *@return true if everyone has bet at least once and all bets are the same (nobody must call). false otherwise.
	 */
	private boolean canMoveOn()
	{
		for (int i=0; i<bets.size()-2; i++)
		{
			if (bets.get(i).equals(bets.get(i+1)))
				continue;
			else
				return false;
		}
		return true;
	}
	/**
	 *Deals the flop (the first three cards)
	 */
	private void dealFlop()
	{
		remove(BACKGROUND);
		for (int i=0; i<3; i++)
		{
			Card c = deck.deal();
			c.setSize(120, 180);
			c.setLocation(207 + 123*i, 201);
			river.add(c);
			add(c);
		}
		add(BACKGROUND);
		repaint();
	}
	/**
	 *Deals the 4th street (the 4th card)
	 */
	private void dealStreet()
	{
		remove(BACKGROUND);
		Card c = deck.deal();
		c.setSize(120, 180);
		c.setLocation(207 + 123*3, 201);
		river.add(c);
		add(c);
		add(BACKGROUND);
		repaint();
	}
	/**
	 *Deals the river (the 5th card)
	 */
	private void dealRiver()
	{
		remove(BACKGROUND);
		Card c = deck.deal();
		c.setSize(120, 180);
		c.setLocation(207 + 123*4, 201);
		river.add(c);
		add(c);
		add(BACKGROUND);
		repaint();
	}
	/**
	 *
	 */
	//Runs through all betting for all computers
	private void computerBet()
	{
		if (!hasFolded)
		{
			while(betIndex!=0 && (haveBet<4 || !canMoveOn()) && betIndex<4)
			{
				moveOn(betIndex);
				haveBet++;
				betIndex++;
				//Thomas needs to add a switch statement
				showMsgDlg("Computer Checked/Called","RAWR",JOptionPane.WARNING_MESSAGE);
			}
			betIndex=0;
	
			if(haveBet>=4 && canMoveOn())
			{
				deal();
			}
		}
	}
	/**
	 *Deals the appropriate amount of cards based on gameplay
	 *@see #dealFlop()
	 *@see #dealStreet()
	 *@see #dealRiver()
	 */
	private void deal()
	{
		switch(river.size())
		{
			case 0: dealFlop();   break;
			case 3: dealStreet(); break;
			case 4: dealRiver();  break;
			case 5: end();        break;
			default: showMsgDlg("YOU BROKEDEDED MY TOY YOU JERK", "JERKFACE!", 4);
		}

		haveBet=0;
		computerBet();
	}
	/**
	 *Deals with finding winner(s) and quitting/restarting the game
	 */
	private void end()
	{
		remove(BACKGROUND);
		remove(takeTurn);
		for (int i=1; i<4; i++)
			for (int j=0; j<2; j++)
			{
				remove(hand[i][j]);
				hand[i][j].flip();
				add(hand[i][j]);
			}
		add(BACKGROUND);
		repaint();

		int myHand = HandEvaluator.evaluate(river, hand[0][0], hand[0][1]);
		int eastHand = HandEvaluator.evaluate(river, hand[1][0], hand[1][1]);
		int southHand = HandEvaluator.evaluate(river, hand[2][0], hand[2][1]);
		int westHand = HandEvaluator.evaluate(river, hand[3][0], hand[3][1]);
		if(myHand>=eastHand && myHand >= southHand && myHand >= westHand)
		{
			int numWinners = 1;
			String winners="You";
			if(myHand == eastHand || myHand == southHand || myHand == westHand)
			{
				int []vals={eastHand,southHand,westHand};
				String []comps={"East", "South", "West"};
				for(int i=0; i<comps.length; i++)
				{
					if(vals[i] == myHand)
					{
						winners += ", " + comps[i];
						numWinners++;
					}
				}
			}
			GameFrame.cash += pot / numWinners;
			monies[0]+=pot;
			//you win play again box
			//with getHand(myHand) to display hand
			//must use "winners" variable in case of multiple winners
			JOptionPane.showMessageDialog(
										  this,
										  winners + " have won with a " + getHand(myHand) + "!",
										  "Hand over!",
										  JOptionPane.INFORMATION_MESSAGE
										 );
		}
		else
		{
			int numWinners = 0;
			String winners = "";
			String []comps={"East", "South", "West"};
			int []vals={eastHand,southHand,westHand};
			int i = 0;
			for(i=0; i<comps.length; i++)
			{
				if(Math.max(eastHand,Math.max(westHand,southHand)) == vals[i])
				{
					//computer wins play again box
					//use the string comps[i] to say which computer won
					//use getHand(vals[i]) to get the string for the hand
					winners += ((numWinners > 0) ? ", " : "") + comps[i];
					numWinners++;
					break;
				}
			}
			JOptionPane.showMessageDialog(
										  this,
										  winners + ((numWinners > 1) ? " have " : " has ") + "won with a " + getHand(vals[i]) + "!",
										  "Hand over!",
										  JOptionPane.INFORMATION_MESSAGE
										 );
		}
		resetGame();
	}
	/**
	 *Accessor for the hand that a player has
	 *@param hand the value of a hand to be translated
	 *@return a string representation for the hand.
	 *	<ul>
	 *		<li>&lt;20   "Nothing"</li>
	 *		<li>&lt;40   "Pair"</li>
	 *		<li>&lt;60   "Two Pair"</li>
	 *		<li>&lt;80   "Three of a Kind"</li>
	 *		<li>&lt;10   "Straight"</li>
	 *		<li>&lt;120  "Flush"</li>
	 *		<li>&lt;140  "Full House"</li>
	 *		<li>&lt;160  "Four of a Kind"</li>
	 *		<li>&gt;=160 "Straight Flush"</li>
	 *	</ul>
	 *
	 */
	private String getHand(int hand)
	{
		if		(hand<20)  return "Nothing";
		else if (hand<40)  return "Pair";
		else if (hand<60)  return "Two Pair";
		else if (hand<80)  return "Three of a Kind";
		else if (hand<100) return "Straight";
		else if (hand<120) return "Flush";
		else if (hand<140) return "Full House";
		else if (hand<160) return "Four of a Kind";
		else			   return "Straight Flush";

	}
	/**
	 *Allows a player to check, if they can, or call
	 *@param index the player who is checking/calling (0-north/user, 1-east, 2-south, 3-west)
	 */
	private void moveOn(int index)
	{
		int previousIndex = (index == 0) ? 3 : index-1;
		if (!canCheck(index))
		{
			//Takes your money >:D
			int wager =  bets.get(previousIndex) - bets.get(index);
			bets.set(index, bets.get(index) + wager);
			GameFrame.cash -= (index == 0) ? wager : 0;
			monies[index] -= wager;
			pot += wager;
			betPrint.setText("You have: $" + GameFrame.cash);
			onTable.setText("Pot: $" + pot);
		}
	}
	/**
	 *Raises the bet of a hand
	 *@param index the player who's raising (0-north/user, 1-east, 2-south, 3-west)
	 */
	private void raise(int index)
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
			//Deals with invalid input
			catch (Exception ex)
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
			haveBet = 0;
			break;
		}
		//Takes your money >:D
		bets.set(0, bets.get(0)+wager);
		GameFrame.cash -= wager;
		monies[0] -= wager;
		pot += wager;
		betPrint.setText("You have: $" + GameFrame.cash);
		onTable.setText("Pot: $" + pot);
		repaint();
	}
	/**
	 *Folds a hand
	 *@param index the player who folded (0-north/user, 1-east, 2-south, 3-west)
	 */
	private void fold(int index)
	{
		remove(hand[index][0]);
		remove(hand[index][1]);
		repaint();
		if (index == 0)
		{
			resetGame();
			hasFolded = true;
		}
	}
	/**
	 *Kicks the user out of the casino if either are bankrupt; if not, prompts the user to either play again or quit to the home screen.
	 */
	private void resetGame()
	{
		GameFrame.boot();
		if (GameFrame.cash < 0)
			GameFrame.bankrupt();
		//Prompts user about new game in a dialog box
		Object[] options = {"Play Again :D", "Quit D:"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent component*/	this,
					/*Dialog message*/	    "Play again.\nDO IT AND YOU'RE COOL!",
					/*Dialog title*/	    "Do you really want to leave?",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.SMILEY,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of Texas Hold Em
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			removeAll();
			add(new TexasHoldEm());
			repaint();
			hasFolded = false;
		}
		//Resets to original screen
		else if (n == 1 || n == JOptionPane.CLOSED_OPTION)
		{
			resetBackground();
		}
	}
	/**
 	 *Resets original main screen. Used when user chooses to leave table between hands.
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
	 *Checks to see if a specific player can check or must call, raise, or fold.
	 *@param index the player to check if they can check or must call
	 *@return true if player at index <code>index</code> can check (their current bet is equal to previous
	 *index's bet), false if they must call (their current bet is greater than previous index's bet)
	 */
	private boolean canCheck(int index)
	{
		int previousIndex = (index == 0) ? 3 : index-1;
		return !(bets.get(previousIndex) > bets.get(index));
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
	 *Handles actions processed on objects using <code>this</code> as their ActionListener
	 *@param e An ActionEvent containing the action which has taken place
	 */
	public void actionPerformed(ActionEvent e)
	{
		while (true)
		{
			if (e.getSource() == takeTurn)
			{
				Object[] options = {(canCheck(0) ? "Check" : "Call"), "Raise", "Fold"};
				int n = JOptionPane.showOptionDialog(
						/*Parent frame*/		this,
						/*Dialog message*/	    "What would you like to do?",
						/*Dialog title*/	    "Decision time!",
						/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
						/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
						/*Icon*/			    GameFrame.POKERCHIP,
						/*Custom button texts*/	options,
						/*Default button*/		options[0]);

				try
				{
					switch(n)
					{
						case JOptionPane.CLOSED_OPTION: throw new RuntimeException();
						case 0: moveOn(0); break;
						case 1: if(GameFrame.cash == 0) throw new RuntimeException(); else raise(0); break;
						case 2: fold(0);   break;
					}
				}
				catch (RuntimeException ex)
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
				betIndex++;
				haveBet++;
				computerBet();
				break;
			}
		}
	}
	/**
	 *Convenience method to revalidate the layout and repaint the content pane
	 */
	public void repaint()
	{
		validate();
		super.repaint();
	}
}