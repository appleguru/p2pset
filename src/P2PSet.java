import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;



public class P2PSet {
	protected GameData myGameData;

	private int numCards;
	private final int defaultWindowWidth = 860;
	private final int defaultWindowHeight = 600;
	private final int defaultIconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	private final int defaultIconScaleHeight = 96;

	protected SetPeer sp;
	private JPanel cardPanel, rightPanel, rightBottomPanel, chatPanel, scorePanel, masterPanel;
	private JScrollPane rightScrollPane, chatScrollPane; 
	private JFrame frame;
	private JLabel cardsLeft, moreCardReqs;
	private JLabel[] scores;
	protected JButton startNewGame, joinExistingGame;
	protected JTextField username;
	protected JTextArea gameLog;
	//private JTextArea scores;
	private ButtonListener bl;
	private CardButton[] cards;
	protected LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	protected JToggleButton reqMoreCards;
	private final JLabel userNameLabel = new JLabel("Username: ");
	private final JLabel scoreLabel = new JLabel("<html><font size=\"+2\" color=\"000000\"><i>Scores:</i></font></html>");
	private final String CARDS_LEFT_STR = "Cards left in deck: ";
	private final String REQ_CARDS_STR = "Players desiring more cards: ";
	private final String HEX_RED = "ff0000";
	private final String HEX_BLACK = "000000";
	protected String myUsername;

	private void createAndShowGUI() {
		//Make a new Game
		//myGameData = new GameData();

		//Create and set up the window.
		frame = new JFrame("P2P Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		bl = new ButtonListener(this);
		username = new JTextField(10); //TODO: Make size a constant var
		//username.addActionListener(ul);

		startNewGame = new JButton("Start New Game"); //TODO: Get these from a constant var
		joinExistingGame = new JButton ("Join Exisiting Game");
		startNewGame.addActionListener(bl);
		joinExistingGame.addActionListener(bl);
		
		reqMoreCards = new JToggleButton("Request More Cards");
		reqMoreCards.addActionListener(bl);
		
		//Moving This here from GameData to fix a mac java bug
		gameLog = new JTextArea(7, 60);

		masterPanel = new JPanel();
		masterPanel.add(userNameLabel);
		masterPanel.add(username);
		masterPanel.add(startNewGame);
		masterPanel.add(joinExistingGame);
		frame.add(masterPanel);

		//Display the window.
		frame.setSize(defaultWindowWidth,defaultWindowHeight);
		frame.setVisible(true);

	}

	public void boardChanged()
	{
		selectedCards.clear();
		frame.getContentPane().removeAll();

		masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		rightPanel = new JPanel();
		rightPanel.setLayout (new BorderLayout());
		scorePanel = new JPanel();
		scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
		chatPanel = new JPanel();
		//chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
		rightBottomPanel = new JPanel();
		rightBottomPanel.setLayout(new BoxLayout(rightBottomPanel, BoxLayout.Y_AXIS));


		JLabel[] scores;
		rightPanel.add(scoreLabel, BorderLayout.PAGE_START);

		int numPlayers = myGameData.playerList.size();
		scores = new JLabel[numPlayers];
		for (int i = 0; i < numPlayers; i++)
		{
			String myColor;
			String tempName = myGameData.playerList.get(i).name;
			String tempScore = myGameData.playerList.get(i).score + "";

			if (tempName.equals(myUsername))
			{ myColor = HEX_RED; }
			else
			{ myColor = HEX_BLACK; }
			scores[i] = new JLabel("<html><font size=\"+1\" color=\"" + myColor + "\">" + tempName + ":     " + tempScore + "</font></html>");
		}//for

		for (JLabel i : scores)
		{ scorePanel.add(i); }

		rightScrollPane = new JScrollPane(scorePanel);
		rightPanel.add(rightScrollPane, BorderLayout.CENTER);

		cardsLeft = new JLabel(CARDS_LEFT_STR + myGameData.deck.unusedCards.size());
		rightBottomPanel.add(cardsLeft);

		rightBottomPanel.add(reqMoreCards);

		moreCardReqs = new JLabel(REQ_CARDS_STR + myGameData.numPlayersWantCards + "/" + myGameData.playerList.size());
		rightBottomPanel.add(moreCardReqs);
		
		rightPanel.add(rightBottomPanel, BorderLayout.PAGE_END);

		gameLog.setEditable(false);
		chatPanel.add(gameLog);
		
		chatScrollPane = new JScrollPane(chatPanel);
		chatScrollPane.setPreferredSize(new Dimension(600,150));
		
		cardPanel = getCardPanel();
		
		//Disable the reqMoreCards button if we already have more cards
		//Note numCards gets set in getCardPanel, so make sure this is after it
		if (numCards == 15)
		{ reqMoreCards.setEnabled(false); }
		else
		{ reqMoreCards.setEnabled(true); }
		
		masterPanel.add(cardPanel, BorderLayout.CENTER);
		masterPanel.add(rightPanel, BorderLayout.LINE_END);
		masterPanel.add(chatScrollPane, BorderLayout.PAGE_END);

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

			ImageIcon icon = myCardButton.card.getIcon();
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	P2PSet myP2PSet = new P2PSet();
                
            	myP2PSet.createAndShowGUI();
            }
        });

	}//main

}//class
