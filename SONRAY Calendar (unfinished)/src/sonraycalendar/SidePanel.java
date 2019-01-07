package sonraycalendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	private final List<EventData> selectedDayEvents;
	private final List<EventData> todayEvents;
	private final List<EventData> tomorrowEvents;
	private final List<EventData> dayAfterEvents;
	private final String[] titles;
	
	private boolean daySelected;
	private int hoveringY;
	
	public SidePanel() {
		selectedDayEvents = new LinkedList<>();
		todayEvents = new LinkedList<>();
		tomorrowEvents = new LinkedList<>();
		dayAfterEvents = new LinkedList<>();
		daySelected = false;
		titles = new String[4];
	}
	
	public void init() {
		setBounds(20, 20, Manager.WIDTH / 3 - 20, Manager.HEIGHT * 3 / 4 + 20);
		setBackground(Color.WHITE);
		setDefaultEvents();
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				hoveringY = e.getY();
				repaint();
			}
		});
	}
	
	private void setDefaultEvents() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM / dd / yyyy");
		
		String today = dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String tomorrow = dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String dayAfter = dateFormat.format(calendar.getTime());
		
		for (EventData event : EventData.events) {
			if (event.getShortDate().equals(today)) todayEvents.add(event);
			if (event.getShortDate().equals(tomorrow)) tomorrowEvents.add(event);
			if (event.getShortDate().equals(dayAfter)) dayAfterEvents.add(event);
		}
		
		titles[0] = "TODAY " + today.replace(" ", "");
		titles[1] = "TOMORROW " + tomorrow.replace(" ",  "");
		int month = Integer.parseInt(dayAfter.substring(0, 2));
		int day = Integer.parseInt(dayAfter.substring(5, 7));
		int year = Integer.parseInt(dayAfter.substring(10, 12));
		String weekday = Month.getWeekdayAsString(month, day, year).toUpperCase();
		titles[2] = weekday + " " + dayAfter.replace(" ", "");
	}
	
	public void setSelectedDayEvents(String selected) {
		if (selected == null) return;
		
		selectedDayEvents.clear();
		for (EventData event : EventData.events) {
			if (event.getShortDate().equals(selected)) selectedDayEvents.add(event);
		}
		daySelected = true;
		
		titles[3] = "SELECTED DAY" + " " + selected.replace(" ", "");
		repaint();
	}
	
	public void disableSelectedDay() {
		daySelected = false;
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		List<List<EventData>> eventListList = new LinkedList<List<EventData>>();
		int i;
		if (daySelected) {
			eventListList.add(selectedDayEvents);
			i = 3;
		} else {
			eventListList.add(todayEvents);
			eventListList.add(tomorrowEvents);
			eventListList.add(dayAfterEvents);
			i = 0;
		}
		
		paintEvents(g2d, eventListList, i);
	}
	
	private void paintEvents(Graphics2D g2d, List<List<EventData>> eventListList, int i) {
		int y = 40;
		for (List<EventData> eventList : eventListList) {
			int eventCounter = 0;
			if (i == 0 || i == 3) g2d.setColor(Color.RED);
			else g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.BOLD, 14));
			g2d.drawString(titles[i], 10, y);
			y += 5;
			g2d.setFont(new Font("Arial", Font.PLAIN, 10));
			for (EventData event : eventList/*int j = 0; j < 3; j++*/) {
				//EventData event = new EventData(); //for testing
				boolean hovering = hoveringY > y && hoveringY < y + 15;
				g2d.setColor(event.getColor(hovering));
				String time = event.getTime() == "" ? "all day" : event.getTime();
				int x = getBounds().width - g2d.getFontMetrics().stringWidth(time);
				g2d.fillRoundRect(5, y, x - 15, 15, 20, 20);
				g2d.setColor(Color.GRAY);
				g2d.drawString(time, x, y + 11);
				g2d.setColor(hovering ? Color.WHITE : Color.BLACK);
				String title = "".equals(event.getTitle()) ? EventDisplay.defaultTitle : event.getTitle();
				g2d.drawString(Icon.getFittedString(g2d.getFontMetrics(), title, x - 15), 15, y + 11);
				y += 16;
				eventCounter++;
			}
			if (eventCounter == 0) {
				g2d.setColor(Color.GRAY);
				g2d.setFont(new Font("Arial", Font.ITALIC, 12));
				g2d.drawString("none", 10, y += 12);
			}
			y += 25; i++;
		}
		
		g2d.setColor(Color.BLACK);
	}
}