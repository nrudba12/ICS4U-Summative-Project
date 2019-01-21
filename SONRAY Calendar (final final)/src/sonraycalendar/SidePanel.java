package sonraycalendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SidePanel extends JPanel {
	private final List<EventData> selectedDayEvents;
	private final List<EventData> todayEvents;
	private final List<EventData> tomorrowEvents;
	private final List<EventData> dayAfterEvents;
	private final String[] titles;
	
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
				Manager manager = Manager.getInstance();
				manager.removeEventPanel();
				manager.topPanel.removeCurrentTimePanel();
				if (!pressed()) manager.calendarPanel.stopSelectingDay();
				manager.repaint();
			}
		});
	}
	
	public void setEvents(String selectedDate) {
		if (selectedDate == null) { setDefaultEvents(); return; }
		
		selectedDayEvents.clear();
		for (EventData event : EventData.events) {
			if (event.getDate().equals(selectedDate)) selectedDayEvents.add(event);
		}
		EventData.sortEvents(selectedDayEvents);
		
		String displayableDate = null;
		try {
			Date date = new SimpleDateFormat("yyyyMMdd").parse(selectedDate);
			displayableDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		titles[3] = "SELECTED DAY " + displayableDate;
	}
	
	private void setDefaultEvents() { //i.e. today, tomorrow, day after (when day not selected)
		Calendar calendar = Calendar.getInstance();
		
		Format comparableFormat = new SimpleDateFormat("yyyyMMdd");
		Format displayableFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		String comparableToday = comparableFormat.format(calendar.getTime());
		String displayableToday = displayableFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		String comparableTomorrow = comparableFormat.format(calendar.getTime());
		String displayableTomorrow = displayableFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		String comparableDayAfter = comparableFormat.format(calendar.getTime());
		String displayableDayAfter = displayableFormat.format(calendar.getTime());
		
		todayEvents.clear();
		tomorrowEvents.clear();
		dayAfterEvents.clear();
		for (EventData event : EventData.events) {
			if (event.getDate().equals(comparableToday)) todayEvents.add(event);
			if (event.getDate().equals(comparableTomorrow)) tomorrowEvents.add(event);
			if (event.getDate().equals(comparableDayAfter)) dayAfterEvents.add(event);
		}
		EventData.sortEvents(todayEvents);
		EventData.sortEvents(tomorrowEvents);
		EventData.sortEvents(dayAfterEvents);
		
		titles[0] = "TODAY " + displayableToday;
		titles[1] = "TOMORROW " + displayableTomorrow;
		titles[2] = Month.getWeekdayAsString(comparableDayAfter).toUpperCase() + " " + displayableDayAfter;
	}
	
	private boolean pressed() {
		Manager manager = Manager.getInstance();
		
		List<List<EventData>> eventListList = new ArrayList<List<EventData>>();
		
		if (manager.calendarPanel.isDaySelected()) {
			eventListList.add(selectedDayEvents);
		} else {
			eventListList.add(todayEvents);
			eventListList.add(tomorrowEvents);
			eventListList.add(dayAfterEvents);
		}
		
		int y = 45;
		for (List<EventData> eventList : eventListList) { //y values based on how panel drawn; if clicked y
			//value also touches drawn event, create event panel for that event
			int eventCounter = 0;
			for (EventData event : eventList) {
				if (clickedY > y && clickedY < y + 15) {
					manager.eventPanel = new EventPanel(event);
					manager.eventPanel.init();
					manager.eventPanel.setLocation(getBounds().width + 24, y + 16);
					return true;
				};
				y += 16;
				eventCounter++;
			}
			if (eventCounter == 0) y += 12;
			y += 30;
		}
		
		return false; //returned if event not pressed, otherwise true
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		List<List<EventData>> eventListList = new ArrayList<List<EventData>>();
		if (Manager.getInstance().calendarPanel.isDaySelected()) {
			eventListList.add(selectedDayEvents);
		} else {
			eventListList.add(todayEvents);
			eventListList.add(tomorrowEvents);
			eventListList.add(dayAfterEvents);
		}
		
		//draws events and titles
		int i = 0;
		int y = 40;
		for (List<EventData> eventList : eventListList) {
			int eventCounter = 0;
			if (i == 0) g2d.setColor(Color.RED);
			else g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.BOLD, 14));
			g2d.drawString(titles[eventListList.size() == 1 ? 3 : i], 10, y);
			y += 5;
			for (EventData event : eventList) {
				boolean selected = hoveringY > y && hoveringY < y + 15 || clickedY > y && clickedY < y + 15;
				
				g2d.setColor(event.getColor(selected));
				g2d.setFont(new Font("Arial", Font.PLAIN, 10));
				String time = "".equals(event.getTime()) ? "all day" : event.getDisplayableTime();
				int x = getBounds().width - g2d.getFontMetrics().stringWidth(time);
				g2d.fillRoundRect(5, y, x - 15, 15, 20, 20);
				g2d.setColor(Color.GRAY);
				g2d.drawString(time, x, y + 11);
				
				g2d.setColor(selected ? Color.WHITE : Color.BLACK);
				String title;
				if ("".equals(event.getTitle())) {
					g2d.setFont(new Font("Arial", Font.ITALIC, 10));
					title = EventPanel.defaultTitle;
				} else {
					g2d.setFont(new Font("Arial", Font.PLAIN, 10));
					title = event.getTitle();
				}
				g2d.drawString(DrawingUtilities.getFittedString(g2d.getFontMetrics(), title, x - 15), 15, y + 11);
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
	}
}