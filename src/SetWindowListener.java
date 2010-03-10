import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


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
