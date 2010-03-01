import java.awt.*;
import java.util.LinkedList;

import javax.swing.*;

public class P2PSet {

	private static final int numCards = 9;
	private static final int defaultWindowWidth = 640;
	private static final int defaultWindowHeight = 480;
	private static final int iconScaleWidth = 128; //TODO: Get this from current window size on resize... 
	protected static JToggleButton[] cards;
	protected static LinkedList<JToggleButton> selectedCards = new LinkedList<JToggleButton>();

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("P2P Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		cards = new JToggleButton[numCards];
		
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers

		JPanel cardPanel = new JPanel(new GridLayout(numRows,numCols));
		
		ImageIcon icon = new ImageIcon ("images/Green_Hatched_Diamond_3.png");
		
		int iconScaler = (icon.getIconWidth() / iconScaleWidth);
		int iconScaleHeight = icon.getIconHeight() / iconScaler;
		
		ButtonListener bl = new ButtonListener();
		
		icon = new ImageIcon(icon.getImage().getScaledInstance(iconScaleWidth,iconScaleHeight,Image.SCALE_SMOOTH));

		//Add buttons to panel
		for (JToggleButton i:cards)
		{
			i = new JToggleButton(icon);
			i.addActionListener(bl);
			cardPanel.add(i);
		}//for

		frame.add(cardPanel);

		//Display the window.
		frame.setSize(defaultWindowWidth,defaultWindowHeight);
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{

		createAndShowGUI();

	}//main

}//class
