import java.awt.BorderLayout;	//Import necessary classes
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;


/**
 * Main class which is run to start the peer and initialize the GUI and the threads. 
 * @author Ari
 *
 */
public class P2PSet {
	protected GameData myGameData;	//Stores this peer's local copy of all game information, such as where all of the cards are, who is winning, and everyone's name and IP

	private int numCards;
	private final int defaultWindowWidth = 860;	//Set the window parameters
	private final int defaultWindowHeight = 600;
	private final int defaultIconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	private final int defaultIconScaleHeight = 96;

	protected SetPeer sp;	//Stores a reference to a SetPeer
	private JPanel cardPanel, rightPanel, rightBottomPanel, chatPanel, scorePanel, masterPanel;	//Various GUI elements
	private JScrollPane rightScrollPane, chatScrollPane; 
	protected JFrame frame;
	private JLabel cardsLeft, moreCardReqs;
	private JLabel[] scores;
	protected JButton joinExistingGame;
	protected JTextField username;
	protected JTextArea gameLog;
	private ButtonListener bl;
	private SetWindowListener wl;
	protected LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	protected JToggleButton reqMoreCards;
	private final JLabel userNameLabel = new JLabel("Username: ");
	private final JLabel scoreLabel = new JLabel("<html><font size=\"+2\" color=\"000000\"><i>Scores:</i></font></html>");
	private final String CARDS_LEFT_STR = "Cards left in deck: ";
	private final String REQ_CARDS_STR = "Players desiring more cards: ";
	private final String HEX_RED = "ff0000";
	private final String HEX_BLACK = "000000";
	protected InetAddress myAddress;
	protected String strAddr;

	/**
	 * Creates and sets up the window.
	 */
	private void createAndShowGUI() {
		frame = new JFrame("P2P Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		bl = new ButtonListener(this);
		username = new JTextField(10); //TODO: Make size a constant var

		joinExistingGame = new JButton ("Play SET!");
		joinExistingGame.addActionListener(bl);
		
		reqMoreCards = new JToggleButton("Request More Cards");
		reqMoreCards.addActionListener(bl);
		
		gameLog = new JTextArea(7, 60);
		gameLog.setEditable(false);
		chatPanel = new JPanel();
		chatPanel.add(gameLog);
		
		chatScrollPane = new JScrollPane(chatPanel);
		chatScrollPane.setPreferredSize(new Dimension(600,150));

		masterPanel = new JPanel();
		masterPanel.add(userNameLabel);
		masterPanel.add(username);
		masterPanel.add(joinExistingGame);
		frame.add(masterPanel);

		//Display the window.
		frame.setSize(defaultWindowWidth,defaultWindowHeight);
		frame.setVisible(true);

	}

	/**
	 * Run when the board is first created and when cards are moved.  Updates the GUI to match changes to the game data.
	 */
	public void boardChanged()
	{
		if (wl == null){
			wl = new SetWindowListener();
			frame.addWindowListener(wl);
		}
		if (wl.sp == null){
			wl.sp = sp;
		}
		
		selectedCards.clear();
		frame.getContentPane().removeAll();

		masterPanel = new JPanel();
		masterPanel.setLayout(new BorderLayout());
		rightPanel = new JPanel();
		rightPanel.setLayout (new BorderLayout());
		scorePanel = new JPanel();
		scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
		rightBottomPanel = new JPanel();
		rightBottomPanel.setLayout(new BoxLayout(rightBottomPanel, BoxLayout.Y_AXIS));

		rightPanel.add(scoreLabel, BorderLayout.PAGE_START);

		int numPlayers = myGameData.playerList.size();
		scores = new JLabel[numPlayers];
		for (int i = 0; i < numPlayers; i++)
		{
			String myColor;
			String tempName = myGameData.playerList.get(i).name;
			String tempScore = myGameData.playerList.get(i).score + "";
			
			InetAddress tempAddr = myGameData.playerList.get(i).ip;
			String tempAddrStr = tempAddr.getHostAddress();

			if ((tempAddrStr.equals(strAddr)))
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

		cardPanel = getCardPanel();
		
		//Disable the reqMoreCards button if we already have more cards, or if there are no more cards to deal
		//Note numCards gets set in getCardPanel, so make sure this is after it
		if (numCards == 15 || myGameData.deck.unusedCards.size() == 0)
		{ reqMoreCards.setEnabled(false); }
		else
		{ reqMoreCards.setEnabled(true); }
		
		masterPanel.add(cardPanel, BorderLayout.CENTER);
		masterPanel.add(rightPanel, BorderLayout.LINE_END);
		masterPanel.add(chatScrollPane, BorderLayout.PAGE_END);

		frame.add(masterPanel);
		frame.validate();
	}//boardChanged

	/**
	 * Accessor method for the GameData object.
	 * @return GameData
	 */
	public GameData getGameData()
	{
		return myGameData;
	}

	/**
	 * Generates the board layout of buttons based on the cards that are supposed to be on the board.
	 * @return A JPanel containing the cards on the board
	 */
	private JPanel getCardPanel()
	{
		numCards = myGameData.deck.boardCards.size();
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers

		JPanel myCardPanel = new JPanel(new GridLayout(numRows,numCols));

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

	/**
	 * This method writes strings in a window below the game board.  All strings are appended to existing text.
	 * @param s The string to write
	 */
	public void log(String s){
		this.gameLog.append( "\n" + s);

		JScrollBar mySB = chatScrollPane.getVerticalScrollBar();
		int currentScrollMax = mySB.getMaximum();
		mySB.setValue(currentScrollMax);
	}
	
	/**
	 * Main method.
	 * @param args Not used by this method
	 */
	public static void main(String[] args)
	{
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	P2PSet myP2PSet = new P2PSet();
            	
				try {
					myP2PSet.myAddress = InetAddress.getLocalHost();
					myP2PSet.strAddr = myP2PSet.myAddress.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
                
            	myP2PSet.createAndShowGUI();
            }
        });

	}//main

}//class
