package sonraycalendar;

import java.awt.BasicStroke;
import java.awt.Color;
//import java.awt.EventQueue;
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
public class CalendarDisplay extends JPanel {
	private final List<EventData> eventsInMonth;
	
	private int month, year;
	private int[] daysInDisplay;
	private int hoveringSquare, clickedSquare;
	private int squareWidth, squareHeight;
	private boolean hoverDay, clickDay;
	
	public CalendarDisplay() {
		eventsInMonth = new ArrayList<>();
	}
	
	public void init() {
		month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		year = Calendar.getInstance().get(Calendar.YEAR);
		daysInDisplay = new int[7 * 6];
		setBounds(Manager.WIDTH / 3 + 20, Manager.HEIGHT / 4 - 40,
				Manager.WIDTH * 2 / 3 - 40, Manager.HEIGHT * 3 / 4 - 40);
		setBackground(Color.WHITE);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int x = e.getX(), y = e.getY();
				hoverDay = x > 4 && x < 4 + squareWidth * 7 && y > 20 && y < 20 + squareHeight * 6;
				//if (!hoverDay) sidePanel.disableSelectedDay();
				hoveringSquare = (y - 20) / squareHeight * 7 + (x - 4) / squareWidth + 1;
				//repaint();
				Manager.getInstance().repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Manager manager = Manager.getInstance();
				
				int x = e.getX(), y = e.getY();
				clickDay = x > 4 && x < 4 + squareWidth * 7 && y > 20 && y < 20 + squareHeight * 6;
				if (!clickDay) manager.sidePanel.stopSelectingDay();
				clickedSquare = (y - 20) / squareHeight * 7 + (x - 4) / squareWidth + 1;
				
				String selectedDay = getSelectedDay();
				manager.sidePanel.setSelectedDayEvents(selectedDay);
				manager.calendarPanel.stopShowingTime();
				
				manager.remove(manager.eventDisplay);
				manager.repaint();
			}
		});
		squareWidth = (int) Math.round((getWidth() - 7) / 7.0);
		squareHeight = (int) Math.round((getHeight() - 20) / 6.0) - 1;
		resetDaysInDisplay();
		setMonth(month, year);
	}
	
	public void setMonth(int month, int year) {
		this.month = month;
		this.year = year;
		eventsInMonth.clear();
		for (EventData event : EventData.events) {
			if (event.getMonth() == month && event.getYear() == year) eventsInMonth.add(event);
		}
		Manager.section(eventsInMonth, 0, eventsInMonth.size() - 1);
		stopClickingDay();
		resetDaysInDisplay();
	}
	
	public int[] getMonth() {
		return new int[] {month, year};
	}
	
	public String getMonthName() {
		return Month.getName(month) + " " + year;
	}
	
	public void stopClickingDay() {
		clickDay = false;
		Manager.getInstance().sidePanel.stopSelectingDay();
	}
	
	public void stopHoveringDay() {
		hoverDay = false;
	}
	
	public boolean dayClicked() {
		return clickDay;
	}
	
	public void test() { //delete later
		int month = this.month == 12 ? 1 : this.month + 1;
		setMonth(month, year);
	}
	
	public String getSelectedDay() { //doesn't account for next month; fix later
		if (!hoverDay) return null;
		int startDay = Month.getWeekday(month, 1, year);
		
		String strmonth, strday, stryear;
		if (clickedSquare <= startDay) {
			if (month == 1) {
				strmonth = Integer.toString(12);
				stryear = Integer.toString(year - 1);
			} else {
				strmonth = Integer.toString(month - 1);
				stryear = Integer.toString(year);
			}
		} else if (clickedSquare <= startDay + Month.getDays(this.month)) {
			strmonth = Integer.toString(month);
			stryear = Integer.toString(year);
		} else {
			if (this.month == 12) {
				strmonth = Integer.toString(1);
				stryear = Integer.toString(year + 1);
			} else {
				strmonth = Integer.toString(month + 1);
				stryear = Integer.toString(year);
			}
		}
		strmonth = (strmonth.length() == 1 ? "0" : "") + strmonth;
		int day = daysInDisplay[clickedSquare - 1];
		strday = (day / 10 == 0 ? "0" : "") + day;
		//doesn't account for year that isn't four digits
		return strmonth + " / " + strday + " / " + stryear;
	}
	
	private void resetDaysInDisplay() {
		Month.leapYear(year);
		
		int startDay = Month.getWeekday(month, 1, year);
		int days = Month.getDays(month);
		int lastMonthDays = Month.getDays(month == 1 ? 12 : month - 1);
		
		for (int i = 0; i < startDay; i++) daysInDisplay[i] = lastMonthDays - startDay + 1 + i;
		for (int i = startDay; i < days + startDay; i++) daysInDisplay[i] = i - startDay + 1;
		for (int i = days + startDay; i < daysInDisplay.length; i++) daysInDisplay[i] = i - (days + startDay) + 1;
	}
	
	private int getCoordinate(int square, boolean xOrY) {
		square--;
		return xOrY ? square % 7 * squareWidth + 4 : square / 7 * squareHeight + 20;
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
	
	private void drawDays(Graphics2D g) {
		g.setFont(new Font("Arial", Font.BOLD, 11));
		
		for (int i = 4; i <= squareWidth * 7 + 4; i += squareWidth) g.drawLine(i, 20, i, squareHeight * 6 + 20);
		for (int i = 20; i <= squareHeight * 6 + 20; i += squareHeight) g.drawLine(4, i, squareWidth * 7 + 4, i);
		
		g.setColor(Color.GRAY);
		for (int i = 0; i < daysInDisplay.length; i++) {
			if (daysInDisplay[i] == 1) g.setColor(g.getColor()
					== Color.BLACK ? Color.GRAY : Color.BLACK);
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
	
	private void drawOutline(Graphics2D g2d) {
		Calendar today = Calendar.getInstance();
		g2d.setStroke(new BasicStroke(3));
		
		if (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.YEAR) == year) {
			int square = today.get(Calendar.DATE) + Month.getWeekday(month, 1, year);
			int x = getCoordinate(square, true);
			int y = getCoordinate(square, false);
			g2d.setColor(Color.RED);
			g2d.drawRect(x, y, squareWidth, squareHeight);
		}
		
		g2d.setColor(Color.BLACK);
		
		if (hoverDay) {
			g2d.drawRect(getCoordinate(hoveringSquare, true),
					getCoordinate(hoveringSquare, false), squareWidth,
					squareHeight);	
		}
		if (clickDay) {
			g2d.drawRect(getCoordinate(clickedSquare, true),
					getCoordinate(clickedSquare, false), squareWidth,
					squareHeight);	
		}
	}
	
	private void drawEvents(Graphics2D g2d) {
		int lastDay = 1;
		int extraEventsInDay = 0;
		for (EventData event : eventsInMonth) {
			//System.out.println(event.getDay());
			if (event.getDay() == lastDay) extraEventsInDay++;
			else extraEventsInDay = 0;
			lastDay = event.getDay();
			
			int square = event.getDay() + Month.getWeekday(event.getMonth(), 1, event.getYear());
			int x = getCoordinate(square, true);
			int y = getCoordinate(square, false);
			
			g2d.setColor(event.getColor(false));
			g2d.fillRoundRect(x, y + 22, squareWidth, 15, 15, 15);
			
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.PLAIN, 10));
			String title = "".equals(event.getTitle()) ? EventDisplay.defaultTitle : event.getTitle();
			String fittedTitle = Icon.getFittedString(g2d.getFontMetrics(), title, squareWidth);
			g2d.drawString(fittedTitle, x + 5, y + 33);
			
			//g2d.setStroke(new BasicStroke(1));
			if (extraEventsInDay == 0) continue;
			
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x + 4, y + 38, squareWidth - 8, 10);
			g2d.setColor(Color.GRAY);
			g2d.drawString(extraEventsInDay + " more...", x + 5, y + 48);
			//System.out.println(extraEventsInDay);
		}
	}
}