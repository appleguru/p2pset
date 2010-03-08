import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;

//Test change from netbeans...

public class P2PSet {
	protected GameData myGameData;

	private int numCards;
	private final int defaultWindowWidth = 640;
	private final int defaultWindowHeight = 480;
	private final int defaultIconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	private final int defaultIconScaleHeight = 96;

	private JPanel cardPanel, scorePanel, masterPanel;
	private JFrame frame;
	private JLabel score;
	private JTextField username;
	private JTextArea scores;
	private ButtonListener bl;
	private UsernameListener ul;
	private CardButton[] cards;
	protected LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	private final JLabel userNameLabel = new JLabel("Username: ");

	private final String messageDestination = "localhost";
	private final int messagePort = 6262;
	private SetServer myServer;

	private void createNewGameAndShowGUI() {
		//Make a new Game
		myGameData = new GameData();

		//myServer = new SetServer();
		//myServer.isServer = true;
		//myServer.gd = myGameData;

		//Create and set up the window.
		frame = new JFrame("P2P Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ul = new UsernameListener(this);
		bl = new ButtonListener(this);
		username = new JTextField(10); //TODO: Make size a constant var
		username.addActionListener(ul);

		//Add cards to the board
		boardChanged();

		//Display the window.
		frame.setSize(defaultWindowWidth,defaultWindowHeight);
		frame.setVisible(true);

	}

	public void boardChanged()
	{
		frame.getContentPane().removeAll();

		masterPanel = new JPanel();
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));
		scorePanel = new JPanel();

		//Add Username to scorePanel
		scorePanel.add(userNameLabel);
		scorePanel.add(username);

		if (ul.getUsername() != null)
		{
			score = new JLabel("Score: " + myGameData.playerList.get(ul.getUsername()).score);
			scorePanel.add(score);
			cardPanel = getCardPanel();
		}//if username

		masterPanel.add(scorePanel);
		masterPanel.add(cardPanel);

		frame.add(masterPanel);
		frame.validate();
	}//boardChanged

	public void sendMessage(Message m, String recipient)
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

	public void sendI_CLAIM_SET(Card c1, Card c2, Card c3)
	{
		ArrayList<Object> set = new ArrayList<Object>();
		set.add(c1);
		set.add(c2);
		set.add(c3);
		set.add(myGameData.playerList.get(ul.getUsername()));
		System.out.println("Sending I_CLAIM_SET to server...");
		Message m = new Message("I_CLAIM_SET", set);
		sendMessage(m, messageDestination);
	}

	public void sendSYNCH_TO_ME()
	{
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(myGameData);
		Message m = new Message("SYNCH_TO_ME", data);
		sendMessage(m, messageDestination);
	}

	public GameData getGameData()
	{
		return myGameData;
	}
	
	private JPanel getCardPanel()
	{
		numCards = myGameData.deck.boardCards.size();
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers
		
		JPanel myCardPanel = new JPanel(new GridLayout(numRows,numCols));
		
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
			myCardPanel.add(myCardButton);
		}//for
		return myCardPanel;
	}//getCardPanel

	public static void main(String[] args)
	{
		P2PSet myP2PSet = new P2PSet();
		myP2PSet.createNewGameAndShowGUI();
		//Make a new Peer Listener that will spawn off a connectionHandler thread
		//PeerListener myPeerListener = new PeerListener();
		//myPeerListener.start();

	}//main

}//class
