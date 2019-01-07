package sonraycalendar;

import java.awt.Color;
//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import javax.swing.JButton;

import javax.swing.JFrame;

public class Manager {
	public static final int WIDTH = 800, HEIGHT = 500;
	
	public static void main(String[] args) {
		EventData.deserialize();
		//EventQueue.invokeLater(() -> {
			JFrame frame = new JFrame("SONRAY Calendar");
			
			frame.setSize(WIDTH, HEIGHT);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			//EventData.events.clear(); //for testing
			//EventData.addTestEvents();
			frame.setLayout(null);
			frame.getContentPane().setBackground(Color.WHITE);
			
			SidePanel sidePanel = new SidePanel();
			sidePanel.init();
			frame.add(sidePanel);
			
			CalendarDisplay calendarDisplay = new CalendarDisplay();
			calendarDisplay.init(sidePanel);
			frame.add(calendarDisplay);
			
			//EventDisplay event = new EventDisplay(new EventData());
			//event.init();
			//event.setLocation(50, 50);
			//frame.add(event);
			
			//JButton button = new JButton();
			//button.setBounds(200, 10, 30, 30);
			//button.addActionListener((ActionEvent e) -> {
				//calendarDisplay.test();
				//calendarDisplay.stopOutliningDay();
				//calendarDisplay.repaint();
			//});
			//frame.add(button);
			
			frame.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					calendarDisplay.stopOutliningDay();
					//if (!event.isDisposed()) event.disposeDisplay();
					sidePanel.disableSelectedDay();
					calendarDisplay.repaint();
				}
			});
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					EventData.serialize();
				}
			});
			frame.setVisible(true);
		//});
	}
}