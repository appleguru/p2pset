import java.awt.*;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;

public class P2PSet {
	protected GameData myGameData;

	private int numCards;
	private final int defaultWindowWidth = 860;
	private final int defaultWindowHeight = 600;
	private final int defaultIconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	private final int defaultIconScaleHeight = 96;

	private JPanel cardPanel, rightPanel, chatPanel, scorePanel, masterPanel;
	private JScrollPane rightScrollPane; 
	private JFrame frame;
	private JLabel cardsLeft, moreCardReqs;
	private JLabel[] scores;
	private JTextField username;
	private JTextArea gameLog;
	//private JTextArea scores;
	private ButtonListener bl;
	private UsernameListener ul;
	private CardButton[] cards;
	protected LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	private JToggleButton reqMoreCards = new JToggleButton("Request More Cards");
	private final JLabel userNameLabel = new JLabel("Username: ");
	private final JLabel scoreLabel = new JLabel("<html><font size=\"+2\" color=\"000000\"><i>Scores:</i></font></html>");
	private final String CARDS_LEFT_STR = "Cards left in deck: ";
	private final String REQ_CARDS_STR = "Players want more cards: ";
	private final String HEX_RED = "ff0000";
	private final String HEX_BLACK = "000000";


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
		//masterPanel.setLayout(new BorderLayout()); //Defaults to BorderLayout..
		rightPanel = new JPanel();
		scorePanel = new JPanel();
		chatPanel = new JPanel();
		
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

		if (ul.getUsername() != null)
		{
			//score = new JLabel("Score: " + myGameData.playerList.get(ul.getUsername()).score);
			//rightPanel.add(score);

			JLabel[] scores;

			rightPanel.add(scoreLabel);

			int numPlayers = myGameData.playerList.size();
			scores = new JLabel[numPlayers];
			for (int i = 0; i < numPlayers; i++)
			{
				String myColor;
				String myName = myGameData.playerList.get(i).name;
				String myScore = myGameData.playerList.get(i).score + "";

				if (myName == ul.getUsername())
				{ myColor = HEX_RED; }
				else
				{ myColor = HEX_BLACK; }
				scores[i] = new JLabel("<html><font size=\"+1\" color=\"" + myColor + "\">" + myName + ":     " + myScore + "</font></html>");
			}//for

			for (JLabel i : scores)
			{ scorePanel.add(i); }
			
			//TODO: Need to set the viewPort of the rightScrollPane...
			rightScrollPane = new JScrollPane(scorePanel);
			rightPanel.add(rightScrollPane);

			cardsLeft = new JLabel(CARDS_LEFT_STR + myGameData.deck.unusedCards.size());
			rightPanel.add(cardsLeft);

			rightPanel.add(reqMoreCards);

			moreCardReqs = new JLabel(REQ_CARDS_STR + myGameData.numPlayersWantCards);
			rightPanel.add(moreCardReqs);
			
			gameLog = new JTextArea("Some Event Log!", 10, 60); //TODO: Use constants for width/height, get actual events...
			chatPanel.add(gameLog);
			
			cardPanel = getCardPanel();
			masterPanel.add(cardPanel, BorderLayout.CENTER);
			masterPanel.add(rightPanel, BorderLayout.LINE_END);
			masterPanel.add(chatPanel, BorderLayout.PAGE_END);

		}//if username

		else
		{
			//Add Username to rightPanel
			rightPanel.add(userNameLabel);
			rightPanel.add(username);
			masterPanel.add(rightPanel, BorderLayout.CENTER);
		}//else we have no username, get it.

		frame.add(masterPanel);
		frame.validate();
	}//boardChanged

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
