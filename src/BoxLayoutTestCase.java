import java.awt.*;
import javax.swing.*;

public class BoxLayoutTestCase {

	public static void main(String[] args) {
		JFrame mainWindow = new JFrame("Test Case");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		JLabel test1 = new JLabel("Test Label Top");
		test1.setAlignmentY(Component.TOP_ALIGNMENT);
		rightPanel.add(test1);

		JLabel test2 = new JLabel("Test Label Bottom");
		test2.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		rightPanel.add(test2);

		mainWindow.add(rightPanel);
		
		mainWindow.setSize(640,480);
		mainWindow.setVisible(true);
	}//main
}//class
