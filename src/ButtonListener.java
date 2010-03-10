
import java.awt.event.ActionEvent;	//Import necessary classes
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * ActionListener which listens for GUI buttons that have been pressed.
 * @author Ari
 *
 */
public class ButtonListener implements ActionListener {

	GameData gd;
	P2PSet p;	//Keep a reference to the main class so we have access to its variables.

	/**
	 * Constructor for ButtonListener.
	 * @param _p Passed-in reference to a P2PSet object
	 */
	public ButtonListener(P2PSet _p){
		p = _p;
	}

	/**
	 * Run when a button is clicked.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(p.reqMoreCards)){
			JToggleButton moreCardsButton = (JToggleButton) e.getSource();
			if (moreCardsButton.isSelected())
			{ p.sp.askMoreCards(); }
			/*if the button is already selected, keep it selected.
			 * We don't allow users to withdraw their requests for more cards
			 * Once they get more cards, it will get set to deselected
			 */
			else
			{ p.reqMoreCards.setSelected(true); }

		}//if

		else if (e.getSource().equals(p.joinExistingGame)){
			p.joinExistingGame.setEnabled(false);
			String username = p.username.getText();
			p.sp = new SetPeer(p, username);
			p.sp.joinGame();
		}//Join Existing Game

		else
		{
			gd = p.getGameData();

			CardButton pressed = (CardButton)e.getSource();

			if (pressed.isSelected()){
				p.selectedCards.add(pressed);
				if (p.selectedCards.size() == Card.NUM_ATTR_TYPES){
					if (gd.deck.verifySet(p.selectedCards.get(0).card, p.selectedCards.get(1).card, p.selectedCards.get(2).card)){
						p.sp.claimSet(p.selectedCards.get(0).card, p.selectedCards.get(1).card, p.selectedCards.get(2).card);
					}//if the set is real
					else {
						p.sp.sendDeductMe();
						for (int i = 0; i < p.selectedCards.size(); i++){
							p.selectedCards.get(i).setSelected(false);
						}//deselect cards
					}//else

					p.selectedCards.clear();

				}//if 3 cards selected
			}//if pressed button is selected

			else {
				p.selectedCards.remove(pressed);
			}//else

		}//else game is in progress and a card button was pushed

	}//actionPerformed
}//class
