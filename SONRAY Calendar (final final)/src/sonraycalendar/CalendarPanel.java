package sonraycalendar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CalendarPanel extends JPanel {
	private static final int xoffset = 4, yoffset = 20;
	private final List<EventData> eventsInMonth;
	private final int[] daysInDisplay;
	
	private int month, year;
	private int hoveringSquare, selectedSquare;
	private int squareWidth, squareHeight;
	private boolean hoverDay, selectDay;
	
	public CalendarPanel() {
		eventsInMonth = new ArrayList<>();
		daysInDisplay = new int[7 * 6];
	}
	
	public void init() {
		setBounds(Manager.WIDTH / 3 + 20, Manager.HEIGHT / 4 - 40, Manager.WIDTH * 2 / 3 - 40, Manager.HEIGHT * 3 / 4 - 40);
		setBackground(Color.WHITE);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				//checks if mouse hovering within bounds of calendar (e.g. mouse hovering weekday label = false)
				hoverDay = x > xoffset && x < xoffset + squareWidth * 7 && y > yoffset && y < yoffset + squareHeight * 6;
				//gets square corresponding to where mouse hovering
				hoveringSquare = (y - yoffset) / squareHeight * 7 + (x - xoffset) / squareWidth + 1;
				Manager.getInstance().repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				hoverDay = false;
				Manager.getInstance().repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				//checks if mouse pressed within bounds of calendar
				selectDay = x > xoffset && x < xoffset + squareWidth * 7 && y > yoffset && y < yoffset + squareHeight * 6;
				//gets square corresponding to where mouse pressed
				selectedSquare = (y - yoffset) / squareHeight * 7 + (x - xoffset) / squareWidth + 1;
				
				Manager manager = Manager.getInstance();
				manager.removeEventPanel();
				manager.topPanel.removeCurrentTimePanel();
				manager.sidePanel.setEvents(getSelectedDay());
				manager.repaint();
			}
		});
		//dimensions of each square in calendar
		squareWidth = (int) Math.round((getWidth() - xoffset * 2) / 7.0);
		squareHeight = (int) Math.round((getHeight() - yoffset) / 6.0) - 1;
		
		Calendar calendar = Calendar.getInstance();
		setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
	}
	
	public void setMonth(int month, int year) {
		this.month = month;
		this.year = year;
		//string of month (year + month written with two digits, e.g. 07)
		String strmonth = year + (month / 10 == 0 ? "0" : "") + month;
		eventsInMonth.clear();
		for (EventData event : EventData.events) {
			//checks if event date yyyyMM (year and month) portion same as current year and month
			//if so, adds to list of events in current month
			if (event.getDate().substring(0, 6).equals(strmonth)) eventsInMonth.add(event);
		}
		EventData.sortEvents(eventsInMonth);
		resetDaysInDisplay();
	}
	
	public int[] getMonth() {
		return new int[] {month, year};
	}
	
	public void stopSelectingDay() {
		selectDay = false;
	}
	
	public boolean isDaySelected() {
		return selectDay;
	}
	
	public String getSelectedDay() {
		if (!selectDay) return null;
		int startDay = Month.getWeekday(EventData.dateToString(month, 1, year));
		
		int intmonth = 0, intyear = 0;
		if (selectedSquare <= startDay) { //if selectedSquare in previous month
			if (month == 1) { //if month is jan
				intmonth = 12;
				intyear = year - 1;
			} else {
				intmonth = month - 1;
				intyear = year;
			}
		} else if (selectedSquare <= startDay + Month.getDays(this.month)) { //if selectedSquare in current month
			intmonth = month;
			intyear = year;
		} else { //if selectedSquare in next month
			if (this.month == 12) { //if month is dec
				intmonth = 1;
				intyear = year + 1;
			} else {
				intmonth = month + 1;
				intyear = year;
			}
		}
		//string of month, day, year, accounting for lack of zeros (e.g. day 7 = "07")
		String strmonth = (intmonth / 10 == 0 ? "0" : "") + intmonth;
		
		int day = daysInDisplay[selectedSquare - 1];
		String strday = (day / 10 == 0 ? "0" : "") + day;
		
		StringBuilder stryear = new StringBuilder(Integer.toString(intyear));
		for (int i = 0; i < 3 - year / 10; i++) stryear.insert(0, "0");
		
		return stryear.toString() + strmonth + strday;
	}
	
	private void resetDaysInDisplay() { //resets how days in calendar are numbered
		Month.leapYear(year);
		
		int startDay = Month.getWeekday(EventData.dateToString(month, 1, year));
		int days = Month.getDays(month);
		int lastMonthDays = Month.getDays(month == 1 ? 12 : month - 1);
		
		for (int i = 0; i < startDay; i++) daysInDisplay[i] = lastMonthDays - startDay + 1 + i;
		for (int i = startDay; i < days + startDay; i++) daysInDisplay[i] = i - startDay + 1;
		for (int i = days + startDay; i < daysInDisplay.length; i++) daysInDisplay[i] = i - (days + startDay) + 1;
	}
	
	private int getCoordinate(int square, boolean xOrY) { //returns coordinate (boolean decides x or y) of specified square in calendar
		if (xOrY) return (square - 1) % 7 * squareWidth + xoffset;
		else return (square - 1) / 7 * squareHeight + yoffset;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		drawDays(g2d);
		drawOutline(g2d);
		drawEvents(g2d);
	}
	
	private void drawDays(Graphics2D g) { //draws squares in calendar and numbers them
		g.setFont(new Font("Arial", Font.BOLD, 11));
		
		for (int i = xoffset; i <= squareWidth * 7 + xoffset; i += squareWidth) g.drawLine(i, yoffset, i, squareHeight * 6 + yoffset);
		for (int i = yoffset; i <= squareHeight * 6 + yoffset; i += squareHeight) g.drawLine(xoffset, i, squareWidth * 7 + xoffset, i);
		
		g.setColor(Color.GRAY);
		for (int i = 0; i < daysInDisplay.length; i++) {
			if (daysInDisplay[i] == 1) g.setColor(g.getColor() == Color.BLACK ? Color.GRAY : Color.BLACK);
			String day = Integer.toString(daysInDisplay[i]);
			int len = g.getFontMetrics().stringWidth(day);
			int x = getCoordinate(i + 1, true) + squareWidth - len - 10;
			int y = getCoordinate(i + 1, false) + 20;
			g.drawString(day, x, y);
		}
		
		for (int i = 0; i < 7; i++) {
			String weekday = Month.weekdays[i].substring(0, 3).toUpperCase();
			int len = g.getFontMetrics().stringWidth(weekday);
			int x = getCoordinate(i + 1, true) + squareWidth - len - 5;
			g.drawString(weekday, x, 15);
		}
	}
	
	private void drawOutline(Graphics2D g2d) { //draws outlines (red outline of current day, day hovering, and day selected)
		Calendar today = Calendar.getInstance();
		String firstDay = EventData.dateToString(today.get(Calendar.MONTH) + 1, 1, today.get(Calendar.YEAR));
		
		g2d.setStroke(new BasicStroke(3));
		
		if (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.YEAR) == year) {
			int square = today.get(Calendar.DATE) + Month.getWeekday(firstDay);
			int x = getCoordinate(square, true);
			int y = getCoordinate(square, false);
			g2d.setColor(Color.RED);
			g2d.drawRect(x, y, squareWidth, squareHeight);
		}
		
		g2d.setColor(Color.BLACK);
		if (hoverDay) {
			int x = getCoordinate(hoveringSquare, true);
			int y = getCoordinate(hoveringSquare, false);
			g2d.drawRect(x, y, squareWidth, squareHeight);
		}
		if (selectDay) {
			int x = getCoordinate(selectedSquare, true);
			int y = getCoordinate(selectedSquare, false);
			g2d.drawRect(x, y, squareWidth, squareHeight);
		}
	}
	
	private void drawEvents(Graphics2D g2d) {
		int lastDay = 1;
		int extraEventsInDay = 0;
		int startDay = Month.getWeekday(EventData.dateToString(month, 1, year));
		for (EventData event : eventsInMonth) {
			//keeps track of how many events in certain day
			int currentDay = Integer.parseInt(event.getDate().substring(6, 8));
			if (currentDay == lastDay) extraEventsInDay++;
			else { extraEventsInDay = 0; lastDay = currentDay; }
			
			int square = currentDay + startDay;
			int x = getCoordinate(square, true);
			int y = getCoordinate(square, false);
			
			if (extraEventsInDay == 0 ) { //if only one event in day, draws event
				g2d.setColor(event.getColor(false));
				g2d.fillRoundRect(x, y + 22, squareWidth, 15, 15, 15);
				
				g2d.setColor(Color.BLACK);
				String title;
				if ("".equals(event.getTitle())) {
					g2d.setFont(new Font("Arial", Font.ITALIC, 10));
					title = EventPanel.defaultTitle;
				} else {
					g2d.setFont(new Font("Arial", Font.PLAIN, 10));
					title = event.getTitle();
				}
				String fittedTitle = DrawingUtilities.getFittedString(g2d.getFontMetrics(), title, squareWidth);
				g2d.drawString(fittedTitle, x + 5, y + 33);
			} else { //if more than one event in day, draws string with number of extra events
				g2d.setColor(getBackground());
				g2d.fillRect(x + 4, y + 38, squareWidth - 8, 10);
				g2d.setColor(Color.GRAY);
				g2d.drawString(extraEventsInDay + " more...", x + 5, y + 48);
			}
		}
	}
}