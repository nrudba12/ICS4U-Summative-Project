package sonraycalendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SearchDisplay extends JPanel {
	private String entry;
	private List<EventData> events;
	private int hoveringy; //clickedy;
	
	public SearchDisplay(String entry) {
		this.entry = entry;
		events = new ArrayList<>();
	}
	
	public void update(String entry) {
		this.entry = entry;
		events.clear();
		init();
	}
	
	public void init() {
		setBounds(Manager.WIDTH / 3 + 24, Manager.HEIGHT / 4 - 40,
				Manager.WIDTH * 2 / 3 - 48, Manager.HEIGHT * 3 / 4 - 40);
		setBackground(Color.WHITE);
		resetEvents();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				hoveringy = e.getY();
				Manager.getInstance().repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//clickedy = e.getY();
				clicked();
			}
		});
	}
	
	private void resetEvents() {
		for (EventData event : EventData.events) {
			if (event.getTitle().contains(entry)) this.events.add(event);
		}
		Manager.section(events, 0, events.size() - 1);
	}
	
	private void clicked() {
		String lastDate = null;
		int y = 20;
		for (EventData event : events) {
			String comparable = event.getComparableDate();
			if (!comparable.equals(lastDate)) {
				y += 20;
				lastDate = comparable;
			}
			
			if (hoveringy < y && hoveringy > y - 14) {
				int strwidth = getFontMetrics(new Font("Arial", Font.PLAIN, 14))
						.stringWidth(event.getTitle());
				
				Manager manager = Manager.getInstance();
				//if (manager.eventDisplay != null) {
					manager.remove(manager.eventDisplay); //}
				manager.eventDisplay = new EventDisplay(event);
				manager.eventDisplay.init();
				manager.eventDisplay.setLocation(getX() + 125 + strwidth, y + 70);
				manager.add(manager.eventDisplay);
			}
			y += 20;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		String lastDate = null;
		int y = 20;
		for (EventData event : events) {
			String comparable = event.getComparableDate();
			if (!comparable.equals(lastDate)) {
				g2d.setColor(Color.BLACK);
				g2d.drawLine(5, y - 5, getWidth() - 10, y - 5);
				y += 20;
				
				lastDate = comparable;
				int month = Integer.parseInt(comparable.substring(4, 6));
				String date = Month.getName(month).substring(0, 3) + " " + comparable.substring(6, 8);
				g2d.setFont(new Font("Arial", Font.BOLD, 14));
				g2d.drawString(date, 10, y);
			}
			
			g2d.setColor(event.getColor(true));
			g2d.fillOval(75, y - 10, 10, 10);
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.PLAIN, 14));
			g2d.drawString(event.getTitle(), 100, y);
			if (hoveringy < y && hoveringy > y - 14) {
				int strwidth = getFontMetrics(g2d.getFont()).stringWidth(event.getTitle());
				g2d.drawLine(100, y + 2, 100 + strwidth, y + 2);
			}
			y += 20;
		}
		g2d.setColor(Color.BLACK);
		g2d.drawLine(5, y - 5, getWidth() - 10, y - 5);
	}
}