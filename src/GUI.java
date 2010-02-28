import java.awt.*;
import javax.swing.*;

public class GUI {

	private static final int numCards = 9;

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("P2PSet");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton[] cards = new JButton[numCards];
		
		//Add buttons to panel in as close to a square as we can
		int numCols = (int)Math.sqrt(numCards); //cast result as an int to throw away decimal
		int remainderCards = numCards % numCols;

		int numRows = numCols;
		if (remainderCards != 0) numRows++; //if we're not a perfect square, add a row for the leftovers

		JPanel cardPanel = new JPanel(new GridLayout(numRows,numCols));

		//Add buttons to panel
		for (JButton i:cards)
		{
			i = new JButton();
			cardPanel.add(i);
		}//for

		frame.add(cardPanel);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{

		createAndShowGUI();

	}//main
}
