package sonraycalendar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class EventButton extends JButton {
	private boolean hovering;
	public boolean pressed;
	
	public void init(Runnable runnable) {
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		
		JButton button = this;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovering = true;
				SwingUtilities.getAncestorOfClass(JPanel.class, button).repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
				SwingUtilities.getAncestorOfClass(JPanel.class, button).repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				runnable.run();
				SwingUtilities.getAncestorOfClass(JPanel.class, button).repaint();
			}
		});
	}
	
	public boolean hovering() {
		return hovering;
	}
}