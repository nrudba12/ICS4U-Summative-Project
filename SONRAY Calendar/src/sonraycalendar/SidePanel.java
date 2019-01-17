package sonraycalendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	private final List<EventData> selectedDayEvents;
	private final List<EventData> todayEvents;
	private final List<EventData> tomorrowEvents;
	private final List<EventData> dayAfterEvents;
	private final String[] titles;
	
	private boolean daySelected;
	private int hoveringY, clickedY;
	
	public SidePanel() {
		selectedDayEvents = new ArrayList<>();
		todayEvents = new ArrayList<>();
		tomorrowEvents = new ArrayList<>();
		dayAfterEvents = new ArrayList<>();
		titles = new String[4];
	}
	
	public void init() {
		setBounds(20, 20, Manager.WIDTH / 3 - 20, Manager.HEIGHT * 3 / 4 + 20);
		setBackground(Color.WHITE);
		setDefaultEvents();
		daySelected = false;
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				hoveringY = e.getY();
				Manager manager = Manager.getInstance();
				manager.calendarDisplay.stopHoveringDay();
				manager.repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clickedY = e.getY();
				Manager manager = Manager.getInstance();
				manager.removeEventDisplay();
				if (!pressed()) manager.calendarDisplay.stopSelectingDay();
				manager.calendarPanel.stopShowingTime();
				manager.repaint();
			}
		});
	}
	
	public void setDefaultEvents() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat comparableFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat displayableDate = new SimpleDateFormat("MM/dd/yyyy");
		
		String compareToday = comparableFormat.format(calendar.getTime());
		String displayToday = displayableDate.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String compareTomorrow = comparableFormat.format(calendar.getTime());
		String displayTomorrow = displayableDate.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String compareDayAfter = comparableFormat.format(calendar.getTime());
		String displayDayAfter = displayableDate.format(calendar.getTime());
		
		todayEvents.clear();
		tomorrowEvents.clear();
		dayAfterEvents.clear();
		for (EventData event : EventData.events) {
			if (event.getComparableDate().equals(compareToday)) todayEvents.add(event);
			if (event.getComparableDate().equals(compareTomorrow)) tomorrowEvents.add(event);
			if (event.getComparableDate().equals(compareDayAfter)) dayAfterEvents.add(event);
		}
		
		titles[0] = "TODAY " + displayToday;
		titles[1] = "TOMORROW " + displayTomorrow;
		int month = Integer.parseInt(displayDayAfter.substring(0, 2));
		int day = Integer.parseInt(displayDayAfter.substring(3, 5));
		int year = Integer.parseInt(displayDayAfter.substring(7, 9));
		String weekday = Month.getWeekdayAsString(month, day, year).toUpperCase();
		titles[2] = weekday + " " + displayDayAfter;
	}
	
	public void setSelectedDayEvents(String selected) {
		if (selected == null) return;
		
		selectedDayEvents.clear();
		for (EventData event : EventData.events) {
			if (event.getComparableDate().equals(selected)) selectedDayEvents.add(event);
		}
		daySelected = true;
		String display = null;
		try {
			Date date = new SimpleDateFormat("yyyyMMdd").parse(selected);
			display = new SimpleDateFormat("MM/dd/yyyy").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		titles[3] = "SELECTED DAY " + display;
	}
	
	public void stopSelectingDay() {
		daySelected = false;
		clickedY = 0;
	}
	
	private boolean pressed() {
		Manager manager = Manager.getInstance();
		if (manager.eventDisplay != null) {
			manager.remove(manager.eventDisplay);
		}
		
		int y = 45;
		
		if (daySelected) {
			for (EventData event : selectedDayEvents) {
				if (clickedY > y && clickedY < y + 15) {
					manager.removeEventDisplay();
					manager.eventDisplay = new EventDisplay(event);
					manager.eventDisplay.init();
					manager.eventDisplay.setLocation(getBounds().width + 24, y + 16);
					return true;
				};
				y += 16;
			}
		} else {
			List<List<EventData>> eventListList = new ArrayList<List<EventData>>();
			eventListList.add(todayEvents);
			eventListList.add(tomorrowEvents);
			eventListList.add(dayAfterEvents);
			
			for (List<EventData> eventList : eventListList) {
				int eventCounter = 0;
				for (EventData event : eventList) {
					if (clickedY > y && clickedY < y + 15) {
						manager.eventDisplay = new EventDisplay(event);
						manager.eventDisplay.init();
						manager.eventDisplay.setLocation(getBounds().width + 24, y + 16);
						return true;
					};
					y += 16;
					eventCounter++;
				}
				if (eventCounter == 0) y += 12;
				y += 30;
			}
		}
		SwingUtilities.getAncestorOfClass(JFrame.class, this).revalidate();
		SwingUtilities.getAncestorOfClass(JFrame.class, this).repaint();
		
		return false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		List<List<EventData>> eventListList = new ArrayList<List<EventData>>();
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
			for (EventData event : eventList) {
				boolean selected = hoveringY > y && hoveringY < y + 15 || clickedY > y && clickedY < y + 15;
				g2d.setColor(event.getColor(selected));
				String time = event.getTime() == "" ? "all day" : event.getTime();
				int x = getBounds().width - g2d.getFontMetrics().stringWidth(time);
				g2d.fillRoundRect(5, y, x - 15, 15, 20, 20);
				g2d.setColor(Color.GRAY);
				g2d.drawString(time, x, y + 11);
				g2d.setColor(selected ? Color.WHITE : Color.BLACK);
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