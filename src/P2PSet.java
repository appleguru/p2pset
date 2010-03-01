import java.awt.*;
import java.util.LinkedList;

import javax.swing.*;

public class P2PSet {

	private static int numCards = 9;
	private static final int defaultWindowWidth = 640;
	private static final int defaultWindowHeight = 480;
	private static final int iconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	protected static CardButton[] cards;
	protected static LinkedList<CardButton> selectedCards = new LinkedList<CardButton>();
	static GameData myGameData;
	static JPanel cardPanel;
	static JFrame frame;
	
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
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers

		cardPanel = new JPanel(new GridLayout(numRows,numCols));
		ButtonListener bl = new ButtonListener(myGameData);

		numCards = myGameData.deck.boardCards.size();
		cards = new CardButton[numCards];

		//Add card buttons to panel
		int i = 0;
		for (i=0; i < numCards; i++)
		{
			CardButton myCardButton = new CardButton();
			myCardButton.card = myGameData.deck.boardCards.get(i);

			ImageIcon icon = myCardButton.card.icon;
			int iconScaler = (icon.getIconWidth() / iconScaleWidth);
			int iconScaleHeight = icon.getIconHeight() / iconScaler;
			//set button's icon to scaled version of card's icon
			icon = new ImageIcon(icon.getImage().getScaledInstance(iconScaleWidth,iconScaleHeight,Image.SCALE_SMOOTH));
			myCardButton.setIcon(icon);
			myCardButton.addActionListener(bl);
			cardPanel.add(myCardButton);
		}//for
		frame.add(cardPanel);
	}//boardChanged
	
	public static void main(String[] args)
	{

		createNewGameAndShowGUI();

	}//main

}//class
