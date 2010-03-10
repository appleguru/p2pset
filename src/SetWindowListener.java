import java.awt.event.WindowEvent;	//Import necessary classes
import java.awt.event.WindowListener;

/**
 * Waits for the user to close the game, then sends a friendly message to fellow players so they don't freak out.
 * @author Ari
 *
 */
public class SetWindowListener implements WindowListener {
	SetPeer sp;
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {
		sp.Quit();
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
