import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;

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
 */
public class GameFrame extends JFrame implements ActionListener
{
	private final String APPDATA = System.getenv("APPDATA");
	/**
	 *"You have" text on main screen
	 */
	public static JLabel youHave;
	/**
	 *Label on main screen displaying the amount of money you have
	 **/
	public static JLabel showMoney;
	/**
	 *Background image
	 */
	public static JLabel BACKGROUND;
	/**
	 *Blackjack background
	 */
	public static JLabel BACKGROUND_BJ;
	/**
	 *Five Card Draw background
	 */
	public static JLabel BACKGROUND_FC;
	/**
	 *Texas Hold Em background
	 */
	public static JLabel BACKGROUND_TX;
	/**
	 *Sad emoticon icon for dialog boxes
	 */
	public static ImageIcon SAD;
	/**
	 *Smiley emoticon icon for dialog boxes
	 */
	public static ImageIcon SMILEY;
	/**
	 *Vomiting emoticon icon for dialog boxes
	 */
	public static ImageIcon VOMIT;
	/**
	 *Grumpy emoticon icon for dialog boxes
	 */
	public static ImageIcon GRUMPY;
	/**
	 *Poker chip icon for dialog boxes
	 */
	public static ImageIcon POKERCHIP;
	/**
	 *Bankrupt icon for dialog boxes
	 */
	public static ImageIcon BANKRUPT;
	/**
	 *Ron Burgandy will kill your family
	 */
	public static ImageIcon FIGHTME;
	/**
	 *Dead emoticon icon for dialog boxes
	 */
	public static ImageIcon FATAL;
	/**
	 *Your current money. Initialized to 1000, reset to 1000 every time a new game is created via the menu bar.
	 */
	public static int cash = 1000;
	/**
	 *States whether you are currently playing a game. Used to prevent users from clicking Game --> Sit At A Table in the middle of a hand.
	 */
	public static boolean inGame = false;
	/**
	 *The menu bar of the game.
	 */
	private static JMenuBar bar;
	/**
	 *The "File" menu of the game 
	 */
	private static JMenu file;
	/**
	 *The "Game" menu of the game
	 */
	private static JMenu game;
	/**
	 *The "Save" item in the file menu
	 */
	private static JMenuItem fileSave;
	/**
	 *The "Load" item in the file menu
	 */
	private static JMenuItem fileLoad;
	/**
	 *The "Exit" item in the file menu
	 */
	private static JMenuItem fileExit;
	/**
	 *The "New Game" item in the file menu
	 */
	private static JMenuItem fileNew;
	/**
	 *The "Sit at a Table" item in the game menu
	 */
	private static JMenuItem gameSit;
	/**
	 *Holder for the content pane
	 */
	private Container c;

	/**
	 *Sets up the main screen.
	 */
	public GameFrame()
	{
		//Constructs standard empty JFrame
		super();
		//Assigns correct image to static icons
		BACKGROUND    = new JLabel(new ImageIcon(getClass().getResource("img/backgrounds/splash-600.png")));
		BACKGROUND_BJ = new JLabel(new ImageIcon(getClass().getResource("img/backgrounds/BlackjackBG.png")));
		BACKGROUND_FC = new JLabel(new ImageIcon(getClass().getResource("img/backgrounds/5-Card-Draw-Background.png")));
		BACKGROUND_TX = new JLabel(new ImageIcon(getClass().getResource("img/backgrounds/Texas-Hold-Em-Background.png")));
		SAD		  = new ImageIcon(getClass().getResource("img/dialogs/sad-100.png"));
		SMILEY	  = new ImageIcon(getClass().getResource("img/dialogs/smiley-100.png"));
		VOMIT	  = new ImageIcon(getClass().getResource("img/dialogs/vomit-100.png"));
		GRUMPY	  = new ImageIcon(getClass().getResource("img/dialogs/grumpy-100.png"));
		POKERCHIP = new ImageIcon(getClass().getResource("img/dialogs/PokerChip-100.png"));
		BANKRUPT  = new ImageIcon(getClass().getResource("img/dialogs/bankrupt.png"));
		FIGHTME	  = new ImageIcon(getClass().getResource("img/dialogs/IWillFightYou.png"));
		FATAL	  = new ImageIcon(getClass().getResource("img/dialogs/fatal.png"));
		//Configures window
		setLayout(null);
		setSize(1024,640);
		//Sets up menu structure
		bar = new JMenuBar();
		setJMenuBar(bar);
		file = new JMenu("File");
		game = new JMenu("Game");
		fileNew = new JMenuItem("New Game");
		gameSit = new JMenuItem("Sit At A Table");
		fileSave = new JMenuItem("Save");
		fileLoad = new JMenuItem("Load");
		fileExit = new JMenuItem("Exit");
		fileSave.addActionListener(this);
		fileLoad.addActionListener(this);
		fileExit.addActionListener(this);
		fileNew.addActionListener(this);
		gameSit.addActionListener(this);
		file.add(fileNew);
		file.add(fileSave);
		file.add(fileLoad);
		file.add(fileExit);
		game.add(gameSit);
		bar.add(file);
		bar.add(game);
		//This is needed A LOT
		c = getContentPane();
		//Configures "You have:" label and adds it
		youHave = new JLabel("You have:");
		youHave.setSize(200, 50);
		youHave.setLocation(5, 5);
		youHave.setVerticalAlignment(JLabel.TOP);
		youHave.setFont(youHave.getFont().deriveFont((float)32));
		youHave.setForeground(Color.GREEN);
		c.add(youHave);
		//Configures "$ " + cash label and adds it
		showMoney = new JLabel("$" + cash);
		showMoney.setSize(1024, 100);
		showMoney.setLocation(5, 50);
		showMoney.setVerticalAlignment(JLabel.TOP);
		showMoney.setFont(youHave.getFont().deriveFont((float)72));
		showMoney.setForeground(Color.GREEN);
		c.add(showMoney);
		//Adds image as background
		BACKGROUND.setSize(1024, 600);
		c.add(BACKGROUND);
		//Housekeeping
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		boolean exists = new File(APPDATA + "\\CrazyCasino\\save").exists();
		if (!exists)
		{
			try { new File(APPDATA + "\\CrazyCasino\\save").createNewFile(); }
			catch (Exception exc) { fatal(); }
		}
	}
	/**
	 *Used to reset menu items disabled during gameplay
	 */
	public static void resetMenus()
	{
		fileNew.setEnabled(true);
		fileLoad.setEnabled(true);
		gameSit.setEnabled(true);
	}
	/**
	 *Kicks the user out of the casino if they are bankrupt.
	 */
	public static void boot()
	{
		if (cash == 0)
		{
			Object[] options = {"OK :("};
			//**Assigned 0, 1, or 2
			int n = JOptionPane.showOptionDialog(
					/*Parent component*/	BACKGROUND,
					/*Dialog message*/	    "You're out of money.\nGoodbye.",
					/*Dialog title*/	    "Uh oh...",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.WARNING_MESSAGE,
					/*Icon*/			    BANKRUPT,
					/*Custom button texts*/	options,
					/*Default button*/		options[0]);
			if (n == 0)
				System.exit(0);
		}
	}
	/**
	 *Kicks the user out of the casino if they bankrupt the casino. <code>cash</code> will overflow to a negative value.
	 */
	public static void bankrupt()
	{
		Object[] options = {"$$$$$"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
				/*Parent component*/	BACKGROUND,
				/*Dialog message*/	    "You've bankrupt the casino.\nIt's time for you to leave.\n\nGoodbye.",
				/*Dialog title*/	    "Uh oh...",
				/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
				/*Text icon type*/	    JOptionPane.WARNING_MESSAGE,
				/*Icon*/			    BANKRUPT,
				/*Custom button texts*/	options,
				/*Default button*/		options[0]);
		if (n == 0)
			System.exit(0);
	}
	/**
	 *Handles actions processed on objects using <code>this</code> as their ActionListener
	 *@param e An ActionEvent containing the action which has taken place
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == fileSave)
		{
			saveGame();
		}
		else if (e.getSource() == fileLoad)
		{
			try
			{
				loadGame();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
				fatal();
			}
		}
		else if (e.getSource() == fileExit)
		{
			System.exit(0);
		}
		else if (e.getSource() == fileNew)
		{
			cash = 1000;
			Container cont = getContentPane();
			cont.removeAll();
			add(youHave);
			showMoney.setText("$" + cash);
			add(showMoney);
			add(BACKGROUND);
			cont.repaint();
		}
		else if (e.getSource() == gameSit)
		{
			createNewGame();
		}
	}
	/**
	 *Prompts user to choose the game they'd like to play, and calls respective methods to create that game.
	 */
	private void createNewGame()
	{
		//Prompts user about new game in a dialog box
		Object[] options = {"Texas Hold 'Em", "5 Card Draw", "Blackjack"};
		//**Assigned 0, 1, or 2
		int n = JOptionPane.showOptionDialog(
					/*Parent frame*/		this,
					/*Dialog message*/	    "Choose the game you would like to play.",
					/*Dialog title*/	    "Welcome :D",
					/*Window type*/		    JOptionPane.YES_NO_CANCEL_OPTION,
					/*Text icon type*/	    JOptionPane.QUESTION_MESSAGE,
					/*Icon*/			    POKERCHIP,
					/*Custom button texts*/	options,
					/*Default button*/		null);

		//Calls respective methods and constructor to mess with content pane
		if (n == 0)
		{
			TexasHoldEm t = new TexasHoldEm();
			c.removeAll();
			c.add(t);
		}
		else if (n == 1)
		{
			FiveCard f = new FiveCard();
			c.removeAll();
			c.add(f);
		}
		else if (n == 2)
		{
			Blackjack bj = new Blackjack();
			c.removeAll();
			c.add(bj);
		}
		fileNew.setEnabled(false);
		fileLoad.setEnabled(false);
		gameSit.setEnabled(false);
		c.repaint();
	}
	/**
	 *Saves the users current amount of money into a file.
	 *@throws InterruptedException
	 *@throws IOException
	 *@see #fatal()
	 */
	private void saveGame()
	{
		try
		{
			Scanner in = new Scanner(APPDATA + "/CrazyCasino/save");
			int n = JOptionPane.showConfirmDialog(this, "If there is an existing game saved,\nit will be overwritten. Continue?", null, JOptionPane.YES_NO_OPTION);
			if (n != 0)
			{
				return;
			}
			PrintWriter out = new PrintWriter(APPDATA + "/CrazyCasino/save");
			out.print(cash);
			out.close();
		}
		catch (FileNotFoundException ex)
		{
			JOptionPane.showMessageDialog(this, "Game could not be saved.", "Error", JOptionPane.WARNING_MESSAGE);
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			fatal();
		}
	}
	/**
	 *Loads the users saved amount of money from file, and sets <code>cash</code> to that amount.
	 *Also resets the main screen, exiting whatever table the user is sitting at, for safety.
	 *@throws InterruptedException
	 *@throws IOException
	 *@see #fatal()
	 */
	private void loadGame() throws InterruptedException, IOException
	{
		while (true)
		{
			try
			{
				FileReader reader;
				Scanner in;
				try
				{
					reader = new FileReader(APPDATA + "/CrazyCasino/save");
					in = new Scanner(reader);
				}
				catch (FileNotFoundException ex)
				{
					System.out.println(ex.getMessage());
					PrintWriter out = new PrintWriter("save");
					out.print(ex.getMessage());
					out.close();
					throw new FileNotFoundException("No existing saved game to load.");
				}
				if (in.hasNextLine())
				{
					int n = JOptionPane.showConfirmDialog(this, "Are you sure you want to load your\nsaved game? Progress will be lost.", null, JOptionPane.YES_NO_OPTION);
					if (n != 0)
					{
						return;
					}
					cash = Integer.parseInt(in.nextLine());
					showMoney.setText("$" + cash);
					c.removeAll();
					c.add(youHave);
					c.add(showMoney);
					c.add(BACKGROUND);
				}
				else throw new FileNotFoundException("No existing saved game to load.");
			}
			catch (FileNotFoundException ex)
			{
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			}
			break;
		}
		c.repaint();
	}
	/**
	 *In case the Java Gods attack. Displays message to user and then terminates program instantly.
	 */
	private void fatal()
	{
		JOptionPane.showMessageDialog(this, "Well...this really stinks.", "Fatal error", JOptionPane.ERROR_MESSAGE, FATAL);
		System.exit(1);
	}
}