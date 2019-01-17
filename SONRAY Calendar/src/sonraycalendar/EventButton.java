package sonraycalendar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class EventButton extends JButton {
	private boolean hovering;
	public boolean pressed;
	
	public void init(Runnable runnable) {
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovering = true;
				Manager.getInstance().repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				hovering = false;
				Manager.getInstance().repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				runnable.run();
				Manager.getInstance().repaint();
			}
		});
	}
	
	public boolean hovering() {
		return hovering;
	}
}