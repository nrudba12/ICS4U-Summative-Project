package sonraycalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

//import javax.swing.JButton;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Manager extends JFrame {
	private static Manager manager;
	
	public static final int WIDTH = 800, HEIGHT = 500;
	public SidePanel sidePanel;
	public CalendarDisplay calendarDisplay;
	public CalendarPanel calendarPanel;
	public EventDisplay eventDisplay;
	public SearchDisplay searchDisplay;
	
	private Manager() {}
	
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
		
		sidePanel = new SidePanel();
		sidePanel.init();
		add(sidePanel);
		
		calendarDisplay = new CalendarDisplay();
		calendarDisplay.init();
		add(calendarDisplay);
		
		calendarPanel = new CalendarPanel();
		calendarPanel.init();
		add(calendarPanel);
		
		eventDisplay = new EventDisplay(null);
		add(eventDisplay);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clicked();
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
	
	public static int partition(List<EventData> events, int low, int high) {
		String pivot = events.get(high).getComparableDate();
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (events.get(j).getComparableDate().compareTo(pivot) <= 0) {
				i++;
				Collections.swap(events, i, j);
			}
		}
		Collections.swap(events, i + 1, high);
		return i + 1;
	}
	
	public static void section(List<EventData> events, int low, int high) {
		if (low < high) {
			int ind = partition(events, low, high);
			section(events, low, ind - 1);
			section(events, ind + 1, high);
		}
	}
	
	public void clicked() {
		calendarDisplay.stopClickingDay();
		calendarPanel.stopShowingTime();
		remove(eventDisplay);
		repaint();
	}
	
	/*@Override
	public Component add(Component c) {
		if (c instanceof EventDisplay) {
			remove(eventDisplay);
			eventDisplay = (EventDisplay) c;
			super.add(eventDisplay);
		} else {
			super.add(c);
		}
		return c;
	}*/
	
	public static void main(String[] args) {
		EventData.deserialize();
		//EventData.addTestEvents();
		Manager.getInstance().init();
	}
}