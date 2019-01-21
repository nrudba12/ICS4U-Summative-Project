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
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SearchPanel extends JPanel {
	private String entry;
	private List<EventData> eventsFound;
	private int hoveringY, clickedY;
	
	public SearchPanel(String entry) {
		this.entry = entry;
		eventsFound = new ArrayList<>();
	}
	
	public void init() {
		setBounds(Manager.WIDTH / 3 + 24, Manager.HEIGHT / 4 - 40, Manager.WIDTH * 2 / 3 - 48, Manager.HEIGHT * 3 / 4 - 40);
		setBackground(Color.WHITE);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				hoveringY = e.getY();
				Manager.getInstance().repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clickedY = e.getY();
				Manager.getInstance().removeEventPanel();
				resultPressed();
				Manager.getInstance().repaint();
			}
		});
		reset();
	}
	
	public void update(String entry) {
		this.entry = entry;
		reset();
	}
	
	public void reset() {
		eventsFound.clear();
		for (EventData event : EventData.events) {
			if (event.getTitle().contains(entry.toLowerCase())) this.eventsFound.add(event);
		}
		EventData.sortEvents(eventsFound);
	}
	
	private void resultPressed() {
		String lastDate = null;
		int y = 20;
		for (EventData event : eventsFound) {
			String currentDate = event.getDate();
			if (!currentDate.equals(lastDate)) {
				y += 20;
				lastDate = currentDate;
			}
			
			if (clickedY < y && clickedY > y - 14) { //creates event panel if result pressed
				String title = event.getTitle().replace(" ", "").equals("") ? EventPanel.defaultTitle : event.getTitle();
				int strwidth = getFontMetrics(new Font("Arial", Font.PLAIN, 14)).stringWidth(title);
				
				Manager manager = Manager.getInstance();
				manager.eventPanel = new EventPanel(event);
				manager.eventPanel.init();
				manager.eventPanel.setLocation(getX() + 125 + strwidth, y + 70);
			}
			y += 20;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		String lastDate = null;
		int y = 20;
		for (EventData event : eventsFound) {
			String comparable = event.getDate();
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
			String title;
			if ("".equals(event.getTitle())) {
				g2d.setFont(new Font("Arial", Font.ITALIC, 14));
				title = EventPanel.defaultTitle;
			} else {
				g2d.setFont(new Font("Arial", Font.PLAIN, 14));
				title = event.getTitle();
			}
			g2d.drawString(title, 100, y);
			if ((hoveringY < y && hoveringY > y - 14) || (clickedY < y && clickedY > y - 14)) {
				int strwidth = getFontMetrics(g2d.getFont()).stringWidth(title);
				g2d.drawLine(100, y + 2, 100 + strwidth, y + 2);
			}
			y += 20;
		}
		g2d.setColor(Color.BLACK);
		g2d.drawLine(5, y - 5, getWidth() - 10, y - 5);
	}
}