import java.awt.event.*;

public class ButtonListener implements ActionListener {

	GameData gd;
	
	public ButtonListener(GameData gada){
		gd = gada;
	}
	
	public void actionPerformed(ActionEvent e) {

		CardButton pressed = (CardButton)e.getSource();

		if (pressed.isSelected()){
			P2PSet.selectedCards.add(pressed);
			if (P2PSet.selectedCards.size() == Card.NUM_ATTR_TYPES){
				if (gd.deck.verifySet(P2PSet.selectedCards.get(0).card, P2PSet.selectedCards.get(1).card, P2PSet.selectedCards.get(2).card)){
					P2PSet.sendI_CLAIM_SET(P2PSet.selectedCards.get(0).card, P2PSet.selectedCards.get(1).card, P2PSet.selectedCards.get(2).card);
				}//if the set is real
				else {
					//todo, check if we punish false claims, punish if necessary
					
					//deselect all cards
					for (int i = 0; i < P2PSet.selectedCards.size(); i++){
						P2PSet.selectedCards.get(i).setSelected(false);
					}
					P2PSet.selectedCards.clear();
				}//if the set wasn't good
			}
		}
		
		else {
			P2PSet.selectedCards.remove(pressed);
		}
		
	}

}
