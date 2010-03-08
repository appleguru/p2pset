import java.awt.event.*;

public class ButtonListener implements ActionListener {

	GameData gd;
	P2PSet p;

	public ButtonListener(P2PSet _p){
		p = _p;
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(p.startNewGame)){
			p.sp = new SetPeer(p, p.username.getText());
			p.myGameData = p.sp.createNewGame();
		}//start new Game

		if (e.getSource().equals(p.joinExistingGame)){
			p.sp = new SetPeer(p, p.username.getText());
			p.myGameData = p.sp.joinGame();
		}//Join Existing Game

		else
		{
			gd = p.getGameData();

			CardButton pressed = (CardButton)e.getSource();

			if (pressed.isSelected()){
				p.selectedCards.add(pressed);
				if (p.selectedCards.size() == Card.NUM_ATTR_TYPES){
					if (gd.deck.verifySet(p.selectedCards.get(0).card, p.selectedCards.get(1).card, p.selectedCards.get(2).card)){
						p.sendI_CLAIM_SET(p.selectedCards.get(0).card, p.selectedCards.get(1).card, p.selectedCards.get(2).card);
					}//if the set is real
					else {
						//TODO: check if we punish false claims, punish if necessary

						//deselect all cards
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
