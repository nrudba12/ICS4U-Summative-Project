package sonraycalendar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class EventButton extends JButton { //used by every button in program
	private boolean hovering;
	public boolean pressed; //can be set and seen easily; much convenient than getter and setter
	//since need to continually get and set value of pressed and no point in getter and no logic
	//needed to set and/or see
	
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
				runnable.run(); //behaviour different for every button, unlike mouseEntered() and mouseExited(),
				//whose behaviours are same (just to change colour)
			}
		});
	}
	
	public boolean hovering() {
		return hovering;
	}
}