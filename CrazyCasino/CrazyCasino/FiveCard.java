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
 *@author Neil B. Patel
 *@author Tommy Bohde
 *@author David Ingersoll
 *
 *@version 1.0 %G%
 */
public class FiveCard extends Container implements ActionListener
{
	/**
	 *Background image.
	 */
	private final JLabel BACKGROUND = GameFrame.BACKGROUND_FC;
	/**
	 *Button to call the dealer.
	 */
	private JButton call;
	/**
	 *Button to fold the hand.
	 */
	private JButton fold;
	/**
	 *Button to display results.
	 */
	private JButton results;
	/**
	 *Button to raise the wager.
	 */
	private JButton raise;
	/**
	 *Button to replace the cards in a hand..
	 */
	private JButton replacer;
	/**
	 *Label displaying the amount of cash the player has.
	 */
	private JLabel betPrint;
	/**
	 *Label displaying the amount of the bet on the table for the user.
	 */
	private JLabel onTable;
	/**
	 *Integer value for the Player's bet.
	 */
	private int bet;
	/**
	 *Integer value for the computer's bet.
	 */
	private int cBet;
	/**
	 *Array of Cards that is the Player's hand.
	 */
	private Card[] hand1;
	/**
	 *Array of Cards that is the Computer's hand.
	 */
	private Card[] comp;
	/**
	 *ArrayList of Cards that are going to be replaced from <code>hand1</code>
	 *@see #hand1
	 */
	private ArrayList<Card> replace;
	/**
	 *ArrayList of Integers that stores the indexes of the Cards to be replaced from <code>hand1</code>
	 *@see #hand1
	 */
	private ArrayList<Integer> indexes;
	/**
	 *Deck for the Game.
	 */
	private Deck deck;
	/**
	 *Represents whether or not the user has replaced their cards for the current hand yet
	 */
	private boolean notReplaced;
	/**
	 *Represents whether or not the user has the ability to bet or not
	 */
	private int justBet;

	/**
	 *Sets up a <code>Container</code> which will be placed into the existing
	 *<code>GameFrame</code> as its content pane. Represents a Five Card Draw table.
	 */
	public FiveCard()
	{
		//Creates standard empty container using no layout manager
		super();
		setLayout(null);
		//Sets the window size and the background size
		setSize(1024,640);
		BACKGROUND.setSize(1024, 640);
		//Initializes all buttons
		call = new JButton("Call");
		fold = new JButton("Fold");
		results = new JButton("Results?");
		raise = new JButton("Bet");
		replacer = new JButton("Replace");
		hand1 = new Card[5]; //Player One
		comp = new Card[5]; //Player Two
		replace = new ArrayList<Card>();
		indexes = new ArrayList<Integer>();
		deck = new Deck(true);
		betPrint = new JLabel();
		onTable = new JLabel();
		//Initializes instance variables
		bet = 0;
		cBet = 0;
		justBet = 0;
		notReplaced = true;
		//Deals hands
		dealHands();
		//Configures button sizes
		call.setSize(100,30);
		results.setSize(100,30);
		fold.setSize(100,30);
		raise.setSize(100,30);
		replacer.setSize(100,30);
		//Configures button locations (BACKGROUND SPECIFIC COORDINATES)
		call.setLocation(50, 110);
		raise.setLocation(50, 160);
		fold.setLocation(50, 210);
		replacer.setLocation(50, 260);
		results.setLocation(868, 110);
		//Adds all buttons
		add(call);
		add(raise);
		add(fold);
		add(replacer);
		add(results);
		//Points event handling to appropriate listener (<code>this</code>).
		call.addActionListener(this);
		results.addActionListener(this);
		fold.addActionListener(this);
		raise.addActionListener(this);
		replacer.addActionListener(this);
		//Disables buttons that cannot be initially used
		call.setVisible(false);
		results.setVisible(false);
		replacer.setVisible(false);
		//Checks to see if user is bankrupt (<code>GameFrame.cash == 0</code>), and kicks them out of the casino (closes game) if they are.
		GameFrame.boot();
		//Displays cards and points event handling to appropriate listener (<code>this</code>).
		for(int i = 0; i < 5; i++)
		{
			add(hand1[i]);
			hand1[i].setLocation(123*i+203, 110);
			hand1[i].addActionListener(this);

			comp[i].flip();
			add(comp[i]);
			comp[i].setLocation(123*i+203, 308);
		}
		//Final housekeeping
		displayCash();
		displayOnTable();
		repaint();
	}
	/**
	 *Adds label displaying "You have: " and the amount of money you have
	 */
	private void displayCash()
	{
		remove(BACKGROUND);
		betPrint.setText("You have: $" + GameFrame.cash);
		betPrint.setFont(betPrint.getFont().deriveFont((float)24));
		betPrint.setForeground(Color.CYAN);
		betPrint.setSize(350, 25);
		betPrint.setLocation(5, 5);
		add(betPrint);
		add(BACKGROUND);
		repaint();
	}
	/*
	 **Adds label displaying "On table: " and the amount of money you have on the table
	 */
	private void displayOnTable()
	{
		remove(BACKGROUND);
		onTable.setText("On table: $" + bet);
		onTable.setFont(onTable.getFont().deriveFont((float)24));
		onTable.setForeground(Color.CYAN);
		onTable.setSize(350, 25);
		onTable.setLocation(5, 30);
		add(onTable);
		add(BACKGROUND);
		repaint();
	}
	/**
	 *Deals 5 cards to each hand
	 */
	private void dealHands()
	{
		for (int i=0; i<5; i++)
		{
			hand1[i] = (deck.deal());
			comp[i] = (deck.deal());
		}
	}
	/**
	 *Asks user for a wager and obnoxiously demands an appropriate one until it is supplied by the user.
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
				if (wager < cBet) throw new RuntimeException("2");
			}
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
			break;
		}
		//Takes your money >:D
		if(!(cBet > GameFrame.cash))
		{
			bet += wager;
			GameFrame.cash -= wager;
		}

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
		if (e.getSource() == hand1[0])
		{
			if(isIn(hand1[0]))
			{
				for(int a = 0; a < replace.size(); a++)
				{
					if(hand1[0].equals(replace.get(a)))
						replace.remove(a);
					if(indexes.get(a) == 0)
						indexes.remove(a);
				}
				hand1[0].setLocation(203,110);

			}
			else if(replace.size() == 3)
			{

				JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "You can only replace three cards",
					/*Window Title*/		"Sorry...",
					/*Window type*/		    JOptionPane.WARNING_MESSAGE);
			}
			else if(notReplaced)
			{
				replace.add(hand1[0]);
				indexes.add(0);
				hand1[0].setLocation(203,60);
			}
		}
		else if (e.getSource() == hand1[1])
		{
			if(isIn(hand1[1]))
			{
				for(int a = 0; a < replace.size(); a++)
				{
					if(hand1[1].equals(replace.get(a)))
						replace.remove(a);
					if(indexes.get(a) == 1)
						indexes.remove(a);
				}

				hand1[1].setLocation(326,110);

			}
			else if(replace.size() == 3)
			{

				JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "You can only replace three cards",
					/*Window Title*/		"Sorry...",
					/*Window type*/		    JOptionPane.WARNING_MESSAGE);
			}
			else if(notReplaced)
			{
				replace.add(hand1[1]);
				indexes.add(1);
				hand1[1].setLocation(326,60);
			}
		}
		else if (e.getSource() == hand1[2])
		{
			if(isIn(hand1[2]))
			{
				for(int a = 0; a < replace.size(); a++)
				{
					if(hand1[2].equals(replace.get(a)))
						replace.remove(a);
					if(indexes.get(a) == 2)
						indexes.remove(a);
				}

				hand1[2].setLocation(449,110);

			}
			else if(replace.size() == 3)
			{

				JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "You can only replace three cards",
					/*Window Title*/		"Sorry...",
					/*Window type*/		    JOptionPane.WARNING_MESSAGE);
			}
			else if(notReplaced)
			{
				replace.add(hand1[2]);
				indexes.add(2);
				hand1[2].setLocation(449, 60);
			}

		}
		else if (e.getSource() == hand1[3])
		{
			if(isIn(hand1[3]))
			{
				for(int a = 0; a < replace.size(); a++)
				{
					if(hand1[3].equals(replace.get(a)))
						replace.remove(a);
					if(indexes.get(a) == 3)
						indexes.remove(a);
				}

				hand1[3].setLocation(572,110);

			}
			else if(replace.size() == 3)
			{

				JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "You can only replace three cards",
					/*Window Title*/		"Sorry...",
					/*Window type*/		    JOptionPane.WARNING_MESSAGE);
			}
			else if(notReplaced)
			{
				replace.add(hand1[3]);
				indexes.add(3);
				hand1[3].setLocation(572,60);
			}

		}
		else if (e.getSource() == hand1[4])
		{
			if(isIn(hand1[4]))
			{
				for(int a = 0; a < replace.size(); a++)
				{
					if(hand1[4].equals(replace.get(a)))
						replace.remove(a);
					if(indexes.get(a) == 4)
						indexes.remove(a);
				}
				hand1[4].setLocation(695,110);

			}
			else if(replace.size() == 3)
			{

				JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "You can only replace three cards",
					/*Window Title*/		"Sorry...",
					/*Window type*/		    JOptionPane.WARNING_MESSAGE);
			}
			else if(notReplaced)
			{
				replace.add(hand1[4]);
				indexes.add(4);
				hand1[4].setLocation(695,60);
			}

		}
		else if(e.getSource() == replacer)
		{
			remove(BACKGROUND);
			replace = null;
			replace = new ArrayList<Card>();
			for(Integer a : indexes)
			{
				remove(hand1[a]);
				hand1[a] = deck.deal();
				add(hand1[a]);
			}
			indexes = null;
			indexes = new ArrayList<Integer>();
			for(int i = 0; i < 5; i++)
			{
				remove(hand1[i]);
				add(hand1[i]);
				hand1[i].setLocation(123*i+203, 110);
			}
			notReplaced = false;
			remove(replacer);
			raise.setVisible(true);
			compTurn();
			add(BACKGROUND);
			repaint();

		}
		else if(e.getSource() == fold)
		{
			JOptionPane.showMessageDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "There are only two people and the money is virtual, way to go.",
					/*Window Title*/		"You're a champ",
					/*Window type*/		    JOptionPane.INFORMATION_MESSAGE);
			removeAll();
			add(new FiveCard());
			repaint();

		}
		else if(e.getSource() == raise)
		{
			makeWager();
			displayCash();
			displayOnTable();
			if (raise.getText().equals("Bet"))
				raise.setText("Raise");
			justBet++;
			if(justBet == 1 || justBet > 1)
				raise.setVisible(false);
			else
				raise.setVisible(true);
			replacer.setVisible(true);
			if(justBet > 1)
			{
				displayCompCards();
			}
		}
		else if(e.getSource() == call)
		{
			if(cBet > GameFrame.cash)
			{
				showMsgDlg(":OOOOO\nALL IN!!!!!!","That cray", JOptionPane.INFORMATION_MESSAGE);
				bet += GameFrame.cash;
				GameFrame.cash = 0;
			}
			else
			{
				bet += cBet;
				GameFrame.cash -= cBet;
			}
			displayCash();
			displayOnTable();
			displayCompCards();
		}
		else if(e.getSource() == results)
		{
			displayCompCards();
			getWin();
		}

	}
	/**
	 *Checks to see if <code>Card b</code> is clicked and within <code>replace</code>
	 *@param b The card to check whether it has been clicked and is in the ArrayList of cards to be replaced
	 *@return true if the card has been clicked and is in the ArrayList of cards to be replaced, false otherwise
	 */
	private boolean isIn(Card b)
	{
		for(Card a : replace)
		{
			if(b.equals(a))
				return true;
		}
		return false;
	}
	/**
	 *Displays the computers cards. Until this method is called, the cards <code>faceShowing</code>
	 *instance variable is false, and the image showing on screen is the back of the card. This
	 *method flips the cards by calling <code>flip()</code> and repaints them on screen. Finally,
	 *all buttons which control the users gameplay are removed from the screen except for the one
	 *to calculate results so that the user cannot continue to bet after having seen the dealers cards.
	 */
	private void displayCompCards()
	{
		remove(BACKGROUND);
		for(int k = 0; k < 5; k++)
		{
			if (!comp[k].getFaceShowing())
			{
				remove(comp[k]);
				comp[k].flip();
				add(comp[k]);
			}
		}
		add(BACKGROUND);
		call.setVisible(false);
		raise.setVisible(false);
		fold.setVisible(false);
		replacer.setVisible(false);
		results.setVisible(true);
		repaint();

	}
	/**
	 *Computers gameplay. Replaces up to three random cards, then creates a random bet for the computer.
	 *If the random bet for the computer is less than the amount of <code>GameFrame.cash</code>, the computer's bet is set to the amound of use cash.
	 *Next, if the computer's bet is greater than 0, <code>call</code> is then set visible.
	 *If <code>GameFrame.cash</code> equals 0, <code>raise</code>, <code>call</code>, and  <code>fold</code>, are set to not visible, and <code>results</code> is set to visible.
	 *@see #replaceCompCards()
	 *@see #raise
	 *@see #call
	 *@see #fold
	 *@see #results
	 */
	private void compTurn()
	{
		replaceCompCards();
		cBet =(int) (Math.random() * GameFrame.cash) + (int) (Math.random() * 100);
		if(cBet > GameFrame.cash)
		{
				cBet = GameFrame.cash;
				raise.setVisible(false);
		}
		if (cBet != 0)
			call.setVisible(true);
		if (GameFrame.cash == 0)
		{
			JOptionPane.showMessageDialog(this, "Computer calls!", "Computer Betting :O", JOptionPane.INFORMATION_MESSAGE);
			raise.setVisible(false);
			results.setVisible(true);
			call.setVisible(false);
			fold.setVisible(false);
		}
		else
			JOptionPane.showMessageDialog(this, "Computer bets " + cBet, "Computer Betting :O", JOptionPane.INFORMATION_MESSAGE);

	}
	/**
	 *Replaces randomly selected cards in the computers hand as part of the computers gameplay in both
	 *the array of cards representing the computers hand and on screen, and then repaints the screen.
	 *@see #compTurn()
	 */
	private void replaceCompCards()
	{
		for(int i = 0; i < 3; i++)
		{
			remove(BACKGROUND);
			int j = (int) (Math.random() * 5);
			remove(comp[j]);
			comp[j] = deck.deal();
			comp[j].flip();
			add(comp[j]);
		}
		for(int k = 0; k < 5; k++)
		{
			remove(BACKGROUND);
			remove(comp[k]);
			add(comp[k]);
			comp[k].setLocation(123*k+203, 308);
			add(BACKGROUND);
			repaint();
		}

	}
	/**
	 *Finds the card with the least face value at or after index <code>first</code> in the array of cards <code>a</code>.
	 *
	 *@param a 		the <code>Card</code> array being organized by <code>organize(Card[] hand)</code>
	 *@param first	the index to start at i
	 *@return 		the index of the <code>Card</code> with the least face value.
	 *@see #organize(Card[] hand)
	 */
	private int findMinimum(Card[] a, int first)
 	{
 		int minIndex = first;
 		for(int i = first + 1; i < a.length; i++)
 		{
 			if(a[i].getFaceValue() < a[minIndex].getFaceValue())
 				minIndex = i;
 		}
 		return minIndex;
 	}
 	/**
 	 *Organizes a hand to greatly simplify evaluating the hand and calculating the winner.
 	 *@see #findMinimum
 	 *@see #getWin
 	 */
 	private void organize(Card[] hand)
 	{
 		Card temp;
 		for(int i =0; i<hand.length-1; i++)
 		{
 			int minIndex = findMinimum(hand,i);
 			if(minIndex != i)
 			{
 				//Swap the cards
 				temp = hand[i];
 				hand[i] = hand[minIndex];
 				hand[minIndex] = temp;
 			}
 		}
 	}
 	/**
 	 *Displays dialog with the results of the hand, stating the winner, loser, and both their hands
 	 *Then displays dialog asking user if they'd like to play another hand, or leave the table and
 	 *return to the main screen.
 	 *@param playerHand the hand that the player has as determined by <code>getWin()</code>
 	 *@param compHand 	the hand that the computer has as determined by <code>getWin()</code>
 	 *@param winner 	will be either "Player" or "Computer", assigned based on winner as determined by <code>getWin()</code>
 	 *@see #getWin()
 	 */
 	private void showWinDlg(String playerHand, String compHand, String winner)
 	{
 		if(winner.equals("Player"))
 			JOptionPane.showMessageDialog(this, "Player has a " + playerHand + " and beats the Computer's " + compHand, winner + " wins!!!", JOptionPane.INFORMATION_MESSAGE);
 		else if(winner.equals("Computer"))
 			JOptionPane.showMessageDialog(this, "Computer has a " + compHand + " and beats the Player's " + playerHand, winner + " wins!!!", JOptionPane.INFORMATION_MESSAGE);
 		else if(winner.startsWith("Player"))
 			JOptionPane.showMessageDialog(this, "Player has a higher " + playerHand + " than the Computer's " + compHand, winner, JOptionPane.INFORMATION_MESSAGE);
 		else
 			JOptionPane.showMessageDialog(this, "Computer has a higher " + compHand + " than the Player's " + playerHand, winner, JOptionPane.INFORMATION_MESSAGE);

 		GameFrame.boot();

 		Object[] options = {"Play Again :D", "Quit D:"};
		//Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Play again.\nDO IT AND YOU'RE COOL!",
					/*Dialog title*/	    "Choose wisely young padawan...",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    GameFrame.SMILEY,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);

		//Resets game of fivecard
		//********** INCEPTION WARNING ************
		if (n == 0)
		{
			removeAll();
			add(new FiveCard());
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
	 *Evaluates both the computer's and player's hands and determines the winner.
	 *@see #checkFlush()
	 *@see #checkStraight()
	 *@see #check3()
	 *@see #check2Pair()
	 *@see #checkPair()
	 */
 	private void getWin()
	{
		/*STATUS TABLE:
		 *8 = Straight Flush
		 *7 = Four of a Kind
		 *6 = Full House
		 *5 = Flush
		 *4 = Straight
		 *3 = 3 of a kind
		 *2 = Two Pair
		 *1 = Pair
		 *0 = Nothing
		 */
		int hand, hand2;
		hand = hand2 = -1;
		boolean[] p1, p2;
		String pHand = "";
		String cHand = "";
		p1 = p2 = new boolean[7];
		organize(hand1);
		organize(comp);

		//Checks to see what hand player 1 has
		p1[0] = checkFour()[0];
		p1[1] = checkFull()[0];
		p1[2] = checkFlush()[0];
		p1[3] = checkStraight()[0];
		p1[4] = check3()[0];
		p1[5] = check2Pair()[0];
		p1[6] = checkPair()[0];

		for (int i=0; i<7; i++)
		{
			if (p1[2] && p1[3])
			{
				hand = 8;
				pHand = ("Straight Flush");
				break;
			}
			else if (p1[i])
			{
				switch (i)
				{
					case 0: hand = 7;	pHand = ("Four of a Kind"); break;
					case 1: hand = 6;	pHand = ("Full House"); 	break;
					case 2: hand = 5;	pHand = ("Flush"); 			break;
					case 3: hand = 4;	pHand = ("Straight"); 		break;
					case 4: hand = 3;	pHand = ("Three of a Kind");break;
					case 5: hand = 2;	pHand = ("Two Pair"); 		break;
					case 6: hand = 1;	pHand = ("Pair"); 			break;
				}
				break;
			}
			else
				pHand = "Nothing";
		}
		//Checks to see what hand player 2 has
		p2[0] = checkFour()[1];
		p2[1] = checkFull()[1];
		p2[2] = checkFlush()[1];
		p2[3] = checkStraight()[1];
		p2[4] = check3()[1];
		p2[5] = check2Pair()[1];
		p2[6] = checkPair()[1];
		for (int i=0; i<7; i++)
		{
			if (p2[2] && p2[3])
			{
				hand2 = 8;
				cHand = ("Straight Flush");
				break;
			}
			else if (p2[i])
			{
				switch (i)
				{
					case 0: hand2 = 7;	cHand = ("Four of a Kind"); break;
					case 1: hand2 = 6;	cHand = ("Full House"); 	break;
					case 2: hand2 = 5;	cHand = ("Flush"); 			break;
					case 3: hand2 = 4;	cHand = ("Straight"); 		break;
					case 4: hand2 = 3;	cHand = ("Three of a Kind");break;
					case 5: hand2 = 2;	cHand = ("Two Pair"); 		break;
					case 6: hand2 = 1;	cHand = ("Pair"); 			break;
				}
				break;
			}
			else
				cHand = "Nothing";
		}
		if (hand > hand2)
		{
			GameFrame.cash += bet*2;
			if (GameFrame.cash < 0)
				GameFrame.bankrupt();
			else
				showWinDlg(pHand, cHand, "Player");
		}
		else if (hand < hand2)
			showWinDlg(pHand, cHand, "Computer");
		//Finds Higher Card
		else
		{
			if(hand == 5 || hand == 4 || hand == 8 || hand == -1)
			{
				if(comp[4].getFaceValue() > hand1[4].getFaceValue())
					showWinDlg(pHand, cHand, "Computer has the higher " + pHand + "!");
				else if(comp[4].getFaceValue() < hand1[4].getFaceValue())
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher " + cHand + "!");
				}
				else
					tie(pHand, cHand);
			}
			else if(hand == 7)
			{
				if(comp[3].getFaceValue() > hand1[3].getFaceValue())
					showWinDlg(pHand, cHand, "Computer has the higher " + pHand + "!");
				else if(comp[3].getFaceValue() < hand1[3].getFaceValue())
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher " + cHand + "!");
				}
				else
					tie(pHand, cHand);
			}
			else if(hand == 6)
			{
				int player = 0;
				int computer = 0;
				if(hand1[0].getFaceValue() > hand1[4].getFaceValue())
					player = hand1[0].getFaceValue();
				else
					player = hand1[4].getFaceValue();
				if(comp[0].getFaceValue() > comp[4].getFaceValue())
					computer = comp[0].getFaceValue();
				else
					computer = comp[4].getFaceValue();
				if(computer > player)
					showWinDlg(pHand, cHand, "Computer has the higher " + pHand + "!");
				else if(computer < player)
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher " + cHand + "!");
				}
				else
					tie(pHand, cHand);
			}
			else if(hand == 3)
			{
				Card p1Three[] = new Card[3];
				Card p2Three[] = new Card[3];

				for(int i = 0; i < hand1.length-2; i++)
		 		{
		 			if(p1[4])
		 			{
		 				if((hand1[i].getFaceValue() == hand1[i+1].getFaceValue()) && (hand1[i+1].getFaceValue() == hand1[i+2].getFaceValue()))
		 				{
		 					p1Three[0] = hand1[i];
		 					p1Three[1] = hand1[i+1];
		 					p1Three[2] = hand1[i+2];
		 				}
		 			}
		 		}
		 		for(int i = 0; i < comp.length-2; i++)
		 		{
		 			if(p2[4])
		 			{

		 				if((comp[i].getFaceValue() == comp[i+1].getFaceValue()) && (comp[i+1].getFaceValue() == comp[i+2].getFaceValue()))
		 				{
		 					p2Three[0] = comp[i];
		 					p2Three[1] = comp[i+1];
		 					p2Three[2] = comp[i+2];
		 				}
		 			}
		 		}
		 		if(p2Three[4].getFaceValue() > p1Three[4].getFaceValue())
					showWinDlg(pHand, cHand, "Computer has the higher three of a kind!");
				else if(p2Three[4].getFaceValue() < p1Three[4].getFaceValue())
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher three of a kind!");
				}
				else
					tie(pHand, cHand);
			}
			else if(hand == 2)
			{
				if(comp[3].getFaceValue() > hand1[3].getFaceValue())
					showWinDlg(pHand, cHand, "Computer has the higher two pair!");
				else if(comp[3].getFaceValue() < hand1[3].getFaceValue())
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher two pair!");
				}
				else
					tie(pHand, cHand);
			}
			else if(hand == 1)
			{
				Card p1Pair[] = new Card[2];
				Card p2Pair[] = new Card[2];
				for(int i = 0; i < hand1.length-1; i++)
		 		{
		 			if((hand1[i].getFaceValue() == hand1[i+1].getFaceValue()))
		 			{
		 				p1Pair[0] = hand1[i];
		 				p1Pair[1] = hand1[i+1];

		 			}
		 		}
		 		for(int i = 0; i < comp.length-1; i++)
		 		{
		 			if((comp[i].getFaceValue() == comp[i+1].getFaceValue()))
		 			{
		 				p2Pair[0] = comp[i];
		 				p2Pair[1] = comp[i+1];
		 			}

		 		}
		 		if(p2Pair[1].getFaceValue() > p1Pair[1].getFaceValue())
					showWinDlg(pHand, cHand, "Computer has the higher pair!");
				else if(p2Pair[1].getFaceValue() < p1Pair[1].getFaceValue())
				{
					GameFrame.cash += bet*2;
					if (GameFrame.cash < 0)
						GameFrame.bankrupt();
					else
						showWinDlg(pHand, cHand, "Player has the higher pair!");
				}
				else
					tie(pHand, cHand);
			}
			else
				tie(pHand, cHand);
		}
	}
	private void tie(String pHand, String cHand)
	{
		GameFrame.cash += bet;
		if (GameFrame.cash < 0)
			GameFrame.bankrupt();
		else
			showWinDlg(pHand, cHand, "No winner!");
	}
	/**
	 *Looks for a four of a kind in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have four of the same card, respectively.
	 */
	private boolean[] checkFour()
	{
		boolean[] x = new boolean[2];
		organize(hand1);
		organize(comp);

		if(hand1[0].getFaceValue() == hand1[1].getFaceValue() && hand1[1].getFaceValue() == hand1[2].getFaceValue() && hand1[2].getFaceValue() == hand1[3].getFaceValue())
				x[0] = true;
		else if(hand1[1].getFaceValue() == hand1[2].getFaceValue() && hand1[2].getFaceValue() == hand1[3].getFaceValue() && hand1[3].getFaceValue() == hand1[4].getFaceValue())
				x[0] = true;
		else
			x[0] = false;

		if(comp[0].getFaceValue() == comp[1].getFaceValue() && comp[1].getFaceValue() == comp[2].getFaceValue() && comp[2].getFaceValue() == comp[3].getFaceValue())
				x[1] = true;
		else if(comp[1].getFaceValue() == comp[2].getFaceValue() && comp[2].getFaceValue() == comp[3].getFaceValue() && comp[3].getFaceValue() == comp[4].getFaceValue())
				x[1] = true;
		else
			x[1] = false;
		return x;
	}
	/**
	 *Looks for a full house in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have full houses, respectively.
	 */
	private boolean[] checkFull()
	{
		boolean[] x = new boolean[2];
		organize(hand1);
		organize(comp);

		if(hand1[0].getFaceValue() == hand1[1].getFaceValue() && hand1[3].getFaceValue() == hand1[4].getFaceValue())
		{
			if(hand1[1].getFaceValue() == hand1[2].getFaceValue() || hand1[2].getFaceValue() == hand1[3].getFaceValue())
			{
				x[0] = true;
			}
		}
		else
			x[0] = false;

		if(comp[0].getFaceValue() == comp[1].getFaceValue() && comp[3].getFaceValue() == comp[4].getFaceValue())
		{
			if(comp[1].getFaceValue() == comp[2].getFaceValue() || comp[2].getFaceValue() == comp[3].getFaceValue())
			{
				x[1] = true;
			}
		}
		else
			x[1] = false;
		return x;
	}
	/**
	 *Looks for a flush in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have flushes, respectively.
	 */
	private boolean[] checkFlush()
	{
		boolean[] x = new boolean[2];
		String initSuit1 = hand1[0].getSuit();
		for (int i=0; i<5; i++)
		{
			if (!(initSuit1.equals(hand1[i].getSuit())))
				break;
			if (i == 4)
				x[0] = true;
		}
		String initSuit2 = comp[0].getSuit();
		for (int i=0; i<5; i++)
		{
			if (!(initSuit2.equals(comp[i].getSuit())))
				break;
			if (i == 4)
				x[1] = true;
		}
		return x;
	}
	/**
	 *Looks for a straight in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have straights, respectively.
	 */
	private boolean[] checkStraight()
	{
		boolean[] x = new boolean[2];
		int first;
		first = hand1[0].getFaceValue();
		for (int i=0; i<5; i++)
		{
			if (hand1[i].getFaceValue() != first+i)
				break;
			if (i == 4)
				x[0] = true;
		}
		first = comp[0].getFaceValue();
		for (int i=0; i<5; i++)
		{
			if (comp[i].getFaceValue() != first+i)
				break;
			if (i == 4)
				x[1] = true;
		}
		return x;
	}
	/**
	 *Looks for three of a kind in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have threes of a kind, respectively.
	 */
	private boolean[] check3()
	{
		boolean[] x = new boolean[2];
		int[]     c = new int[5];
		//Puts values of hand1 into c and checks
		for (int i=0; i<5; i++)
			c[i] = hand1[i].getFaceValue();
		x[0] = ((c[0] == c[1] && c[0] == c[2]) || (c[1] == c[2] && c[1] == c[3]) || (c[2] == c[3] && c[2] == c[4]));
		//Puts values of comp into c and checks
		for (int i=0; i<5; i++)
			c[i] = comp[i].getFaceValue();
		x[1] = ((c[0] == c[1] && c[0] == c[2]) || (c[1] == c[2] && c[1] == c[3]) || (c[2] == c[3] && c[2] == c[4]));
		return x;
	}
	/**
	 *Looks for two pairs in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have two pairs, respectively.
	 */
	private boolean[] check2Pair()
	{
		boolean[] x = new boolean[2];
		int[]     c = new int[5];
		//Puts values of hand1 into c and checks
		for (int i=0; i<5; i++)
			c[i] = hand1[i].getFaceValue();
		x[0] = ((c[0] == c[1] && c[2] == c[3]) || (c[0] == c[1] && c[3] == c[4]) || (c[1] == c[2] && c[3] == c[4]));
		//Puts values of comp into c and checks
		for (int i=0; i<5; i++)
			c[i] = comp[i].getFaceValue();
		x[1] = ((c[0] == c[1] && c[2] == c[3]) || (c[0] == c[1] && c[3] == c[4]) || (c[1] == c[2] && c[3] == c[4]));
		return x;
	}
	/**
	 *Looks for a pair in both hands
	 *@return A boolean array. <code>array[0]</code> and <code>array[1]</code>
	 *represent whether the player and computer have pairs, respectively.
	 */
	private boolean[] checkPair()
	{
		boolean[] x = new boolean[2];
		int[]     c = new int[5];
		//Puts values of hand1 into c and checks
		for (int i=0; i<5; i++)
			c[i] = hand1[i].getFaceValue();
		x[0] = ((c[0] == c[1]) || (c[1] == c[2]) || (c[2] == c[3]) || (c[3] == c[4]));
		//Puts values of comp into c and checks
		for (int i=0; i<5; i++)
			c[i] = comp[i].getFaceValue();
		x[1] = ((c[0] == c[1]) || (c[1] == c[2]) || (c[2] == c[3]) || (c[3] == c[4]));
		return x;
	}
}