package sonraycalendar;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//import javax.swing.JButton;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Manager extends JFrame { //when panels repaint they must repaint whole manager instead
	//of just themselves so that panels always drawn in proper z order (e.g. event display in front)
	private static Manager manager;
	
	public static final int WIDTH = 800, HEIGHT = 500;
	public final SidePanel sidePanel; //never removed from jframe
	public final CalendarPanel calendarPanel; //sometimes removed from jframe but var never null
	public final TopPanel topPanel; //never removed from jframe
	
	public EventPanel eventPanel; //always removed and added in jframe but always only one
	public SearchPanel searchPanel; //always removed and added in jframe but always only one can't be
	//added at same time as calendarPanel
	
	private Manager() {
		sidePanel = new SidePanel();
		calendarPanel = new CalendarPanel();
		topPanel = new TopPanel();
	}
	
	public static Manager getInstance() {
		if (manager == null) manager = new Manager();
		return manager;
	}
	
	private void init() {
		setTitle("SONRAY Calendar");
		
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		setLayout(null);
		getContentPane().setBackground(Color.WHITE);
		
		sidePanel.init();
		add(sidePanel);
		calendarPanel.init();
		add(calendarPanel);
		topPanel.init();
		add(topPanel);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				calendarPanel.stopSelectingDay();
				topPanel.removeCurrentTimePanel();
				removeEventPanel();
				repaint();
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EventData.serialize();
			}
		});
		
		setVisible(true);
	}
	
	public void removeEventPanel() { //setting to null serves as reference for whether or not eventPanel
		//in manager (if not, trying to remove it would result in error)
		if (eventPanel != null) {
			remove(eventPanel);
			eventPanel = null;
		}
	}
	
	public void stopSelectingDay() {
		calendarPanel.stopSelectingDay();
		sidePanel.setEvents(null);
		
	}
	
	public static void main(String[] args) {
		EventData.deserialize();
		Manager.getInstance().init();
	}
}