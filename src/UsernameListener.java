import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class UsernameListener implements ActionListener {

	P2PSet p;
	GameData gd;
	String lastUsername;
	
	public UsernameListener(P2PSet _p){
		p = _p;
		gd = p.getGameData();
		lastUsername = null;
	}//constructor

	public void actionPerformed(ActionEvent e) {
		JTextField myUserNameField = (JTextField) e.getSource();
	    String myUserName = myUserNameField.getText();

		if (lastUsername == null)
		{
			gd.addPlayer(myUserName);
			lastUsername = myUserName;
			p.boardChanged(); //Redraw board so we see score now
		}//if we're a new player
		
		else
		{
			gd.changePlayerName(lastUsername, myUserName);
			lastUsername = myUserName;
		}//else we're changing names
	}//actionPerformed
	
	public String getUsername()
	{
		return lastUsername;
	}
	
}//class