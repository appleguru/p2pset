import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;

public class P2PSet {

	private static int numCards = 9;
	private static final int defaultWindowWidth = 640;
	private static final int defaultWindowHeight = 480;
	private static final int defaultIconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	private static final int defaultIconScaleHeight = 96;
	protected static CardButton[] cards;
	protected static LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	static GameData myGameData;
	static JPanel cardPanel, scorePanel, masterPanel;
	static JFrame frame;
	static JLabel score;
	private static final String messageDestination = "localhost";
	private static final int messagePort = 62626;

	private static void createNewGameAndShowGUI() {
		//Make a new Game
		myGameData = new GameData();

		//Create and set up the window.
		frame = new JFrame("P2P Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add cards to the board
		boardChanged();

		//Display the window.
		frame.setSize(defaultWindowWidth,defaultWindowHeight);
		frame.setVisible(true);
	}

	private static void boardChanged()
	{
		numCards = myGameData.deck.boardCards.size();
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers

		masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		cardPanel = new JPanel(new GridLayout(numRows,numCols));
		scorePanel = new JPanel();
		
		//Add score to scorePanel
		score = new JLabel("Score: " + myGameData.playerList.get("Me!").score); //Yay for hacks!
		scorePanel.add(score);
		
		ButtonListener bl = new ButtonListener(myGameData);
		cards = new CardButton[numCards];

		//Add card buttons to boardPanel
		for (int i = 0; i < numCards; i++)
		{
			CardButton myCardButton = new CardButton();
			myCardButton.card = myGameData.deck.boardCards.get(i);

			ImageIcon icon = myCardButton.card.icon;
			int iconScaler, iconScaleHeight, iconScaleWidth;
			if (icon.getIconWidth() > icon.getIconHeight())
			{
				iconScaler = (icon.getIconWidth() / defaultIconScaleWidth);
				iconScaleHeight = icon.getIconHeight() / iconScaler;
				iconScaleWidth = defaultIconScaleWidth;
			}

			else
			{
				iconScaler = (icon.getIconHeight() / defaultIconScaleHeight);
				iconScaleWidth = icon.getIconWidth() / iconScaler;
				iconScaleHeight = defaultIconScaleHeight;
			}

			//set button's icon to scaled version of card's icon
			icon = new ImageIcon(icon.getImage().getScaledInstance(iconScaleWidth,iconScaleHeight,Image.SCALE_SMOOTH));
			myCardButton.setIcon(icon);
			myCardButton.addActionListener(bl);
			cardPanel.add(myCardButton);
		}//for
		masterPanel.add(scorePanel);
		masterPanel.add(cardPanel);
		frame.add(masterPanel);
	}//boardChanged

	public static void sendMessage(Message m, String recipient)
	{
		try
		{
			Socket sock = new Socket(recipient, messagePort);
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());	//Create an object output stream to send messages to the server
			oos.writeObject(m);	//Send a PUT message to the server with the given object
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	public static void sendI_CLAIM_SET(Card c1, Card c2, Card c3)
	{
		ArrayList<Object> set = new ArrayList<Object>();
		set.add(c1);
		set.add(c2);
		set.add(c3);
		Message m = new Message("I_CLAIM_SET", set);
		sendMessage(m, messageDestination);
	}

	public static void sendSYNCH_TO_ME()
	{
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(myGameData);
		Message m = new Message("SYNCH_TO_ME", data);
		sendMessage(m, messageDestination);
	}
	

	public static void main(String[] args)
	{

		createNewGameAndShowGUI();

	}//main

}//class
