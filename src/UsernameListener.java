import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class UsernameListener implements ActionListener {

	GameData gd;
	String lastUsername;

	public UsernameListener(GameData gada){
		gd = gada;
		lastUsername = null;
	}

	public void actionPerformed(ActionEvent e) {
		JTextField myUserNameField = (JTextField) e.getSource();
	    String myUserName = myUserNameField.getText();

		if (lastUsername == null)
		{
			gd.addPlayer(myUserName);
		}//if we're a new player
		
		else
		{
			gd.changePlayerName(lastUsername, myUserName);
		}//else we're changing names
	}//actionPerformed
	
	public String getUsername()
	{
		return lastUsername;
	}
	
}//class