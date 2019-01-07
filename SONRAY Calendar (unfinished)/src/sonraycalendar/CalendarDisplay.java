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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class CalendarDisplay extends JPanel {
	private final List<EventData> eventsInMonth;
	
	private int month, year;
	private int[] daysInDisplay;
	private int currentSquare;
	private int squareWidth, squareHeight;
	private boolean outlineDay;
	
	public CalendarDisplay() {
		eventsInMonth = new LinkedList<>();
	}
	
	public void init(SidePanel sidePanel) {
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
				outlineDay = x > 4 && x < 4 + squareWidth * 7 && y > 20 && y < 20 + squareHeight * 6;
				if (!outlineDay) sidePanel.disableSelectedDay();
				currentSquare = (y - 20) / squareHeight * 7 + (x - 4) / squareWidth + 1;
				repaint();
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String selectedDay = getSelectedDay();
				sidePanel.setSelectedDayEvents(selectedDay);
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
		stopOutliningDay();
		resetDaysInDisplay();
	}
	
	public void stopOutliningDay() {
		outlineDay = false;
	}
	
	public boolean isOutliningDay() {
		return outlineDay;
	}
	
	public void test() { //delete later
		int month = this.month == 12 ? 1 : this.month + 1;
		setMonth(month, year);
	}
	
	public String getSelectedDay() { //doesn't account for next month; fix later
		if (!outlineDay) return null;
		int startDay = Month.getWeekday(month, 1, year);
		
		boolean inCurrentMonth = currentSquare >= startDay + 1;
		
		int previousMonth, previousMonthYear;
		if (month == 1) {
			previousMonth = 12;
			previousMonthYear = this.year - 1;
		} else {
			previousMonth = month - 1;
			previousMonthYear = this.year;
		}
		
		int month = inCurrentMonth ? this.month : previousMonth;
		int year = inCurrentMonth ? this.year : previousMonthYear;
		int day = daysInDisplay[currentSquare - 1];
		
		String strmonth, strday, stryear;
		strmonth = month / 10 == 0 ? "0" + Integer.toString(month) : Integer.toString(month);
		strday = day / 10 == 0 ? "0" + Integer.toString(day) : Integer.toString(day);
		stryear = year / 1000 == 0 ? "0" + Integer.toString(year) : Integer.toString(year);
		
		return strmonth + " / " + strday + " / " + stryear;
	}
	
	private void resetDaysInDisplay() {
		int startDay = Month.getWeekday(month, 1, year);
		int days = Month.getDays(month);
		int lastMonthDays = Month.getDays(month == 1 ? 12 : month - 1);
		
		for (int i = 0; i < startDay; i++) daysInDisplay[i] = lastMonthDays - startDay + 1 + i;
		for (int i = startDay; i < days + startDay; i++) daysInDisplay[i] = i - startDay + 1;
		for (int i = days + startDay; i < daysInDisplay.length; i++) daysInDisplay[i] = i - (days + startDay) + 1;
	}
	
	private int getCoordinate(int square, boolean xOrY) {
		square--;
		return xOrY ? (square) % 7 * squareWidth + 4 : (square) / 7 * squareHeight + 20;
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
		
		for (int i = 0; i < daysInDisplay.length; i++) {
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
		
		if (outlineDay) {
			g2d.setColor(Color.BLACK);
			g2d.drawRect(getCoordinate(currentSquare, true), getCoordinate(currentSquare, false), squareWidth, squareHeight);	
		}
	}
	
	private void drawEvents(Graphics2D g2d) {
		for (EventData event : eventsInMonth) {
			int month = event.getMonth();
			int year = event.getYear();
			int square = event.getDay() + Month.getWeekday(month, 1, year);
			int x = getCoordinate(square, true);
			int y = getCoordinate(square, false);
			
			g2d.setColor(event.getColor(false));
			g2d.fillRoundRect(x, y + 22, squareWidth, 15, 15, 15);
			
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.PLAIN, 10));
			String title = "".equals(event.getTitle()) ? EventDisplay.defaultTitle : event.getTitle();
			
			String fittedTitle = Icon.getFittedString(g2d.getFontMetrics(), title, squareWidth);
			
			g2d.drawString(fittedTitle, x + 5, y + 33);
		}
	}
}