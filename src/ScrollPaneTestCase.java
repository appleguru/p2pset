import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ScrollPaneTestCase {

	JTextArea testCaseTextArea;
	JScrollPane testCaseScrollPane;
	JFrame mainWindow;
	JButton appendMoreTextButton;
	
	public void createGUI ()
	{
		mainWindow = new JFrame("Test Case");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		TestButtonListener bl = new TestButtonListener(this);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		appendMoreTextButton = new JButton ("Append More Text!");
		appendMoreTextButton.addActionListener(bl);
		mainPanel.add(appendMoreTextButton);

		testCaseTextArea = new JTextArea();
		testCaseScrollPane = new JScrollPane(testCaseTextArea);
		
		mainPanel.add(testCaseScrollPane);
		
		mainWindow.add(mainPanel);
		
		mainWindow.setSize(640,480);
		mainWindow.setVisible(true);	
	}
	
	public static void main(String[] args) {
		ScrollPaneTestCase mytc = new ScrollPaneTestCase();
		mytc.createGUI();
	}//main
	
	public class TestButtonListener implements ActionListener
	{	
		ScrollPaneTestCase sp;
	public TestButtonListener(ScrollPaneTestCase _sp)
	{
		sp = _sp;
	}//Constructor
		
		public void actionPerformed(ActionEvent e)
		{			
			String textToAppend = "Lorem ipsum dolor sit amet, consectetur adipisicing elit,\n sed do eiusmod tempor incididunt ut labore et dolore magna\n aliqua. Ut enim ad minim veniam, quis nostrud exercitation \nullamco laboris nisi ut aliquip ex ea commodo consequat. \nDuis aute irure dolor in reprehenderit in voluptate velit esse \ncillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat \ncupidatat non proident, sunt in culpa qui officia deserunt mollit anim\n id est laborum.\n";
			sp.testCaseTextArea.append(textToAppend);
			
			//Do this to simulate what my app is doing on refresh...
			sp.mainWindow.getContentPane().removeAll();
			sp.mainWindow.add(sp.testCaseScrollPane);
			sp.mainWindow.add(sp.appendMoreTextButton);
			
			//sp.testCaseScrollPane.getVerticalScrollBar().setValue(sp.testCaseScrollPane.getVerticalScrollBar().getMaximum());

			//sp.testCaseScrollPane.getVerticalScrollBar().setValue(sp.testCaseScrollPane.getVerticalScrollBar().getHeight());
			
			//sp.testCaseTextArea.setCaretPosition(sp.testCaseTextArea.getCaretPosition() + textToAppend.length());			
		}
	}//TestButtonListener
	
}//class