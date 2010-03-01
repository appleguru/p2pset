import java.awt.event.*;
import javax.swing.JToggleButton;

public class ButtonListener implements ActionListener {

	public void actionPerformed(ActionEvent e) {

		JToggleButton pressed = (JToggleButton)e.getSource();

		if (pressed.isSelected()){
			P2PSet.selectedCards.add(pressed);
			if (P2PSet.selectedCards.size() == 3){
				//todo claim the set
			}
		}
		
		else {
			P2PSet.selectedCards.remove(pressed);
		}
		
	}

}
