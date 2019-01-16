package sonraycalendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class CalendarPanel extends JPanel {
	private final EventButton leftArrow, rightArrow, add, time, search, exitSearch;
	private final JLabel month;
	private final JTextField searchField;
	
	private boolean searchMode;
	
	private static class TimeDisplay extends JPanel {
		private EventButton current;
		private JLabel now;
		
		private TimeDisplay() {
			current = new EventButton();
			now = new JLabel();
		}
		
		private void init() {
			setLayout(null);
			setSize(190, 40);
			setBackground(Color.WHITE);
			
			now.setFont(new Font("Arial", Font.PLAIN, 10));
			now.setText(new SimpleDateFormat("EEE, MM/dd/yyyy, h:mm a zz")
					.format(new Date()));
			now.setBounds(10, 17, 160, 20);
			add(now);
			
			current.setBounds(170, 20, 14, 14);
			current.init(() -> {
				Manager manager = Manager.getInstance();
				Calendar calendar = Calendar.getInstance();
				manager.calendarDisplay.setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
				manager.calendarPanel.setSearchMode(false);
				manager.calendarPanel.stopShowingTime();
				manager.repaint();
			});
			add(current);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), 15);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			Color currentColor = current.hovering() ? Color.RED : Color.GRAY;
			g.drawImage(Icon.location(14, getBackground(), currentColor),
					170, 20, null);
		}
	}
	
	public CalendarPanel() {
		leftArrow = new EventButton();
		rightArrow = new EventButton();
		add = new EventButton();
		time = new EventButton();
		search = new EventButton();
		exitSearch = new EventButton();
		month = new JLabel();
		searchField = new JTextField();
	}
	
	public void init() {
		CalendarPanel panel = this;
		setLayout(null);
		setBackground(Color.WHITE);
		
		leftArrow.init(() -> {
			if (!leftArrow.isEnabled()) return;
			CalendarDisplay display = Manager.getInstance().calendarDisplay;
			int[] month = display.getMonth();
			
			if (month[0] == 1) {
				display.setMonth(12, month[1] - 1);
			} else {
				display.setMonth(month[0] - 1, month[1]);
			}
			display.repaint();
			setSearchMode(false);
		});
		
		rightArrow.init(() -> {
			if (!rightArrow.isEnabled()) return;
			CalendarDisplay display = Manager.getInstance().calendarDisplay;
			int[] month = display.getMonth();
			
			if (month[0] == 12) {
				display.setMonth(1, month[1] + 1);
			} else {
				display.setMonth(month[0] + 1, month[1]);
			}
			display.repaint();
			setSearchMode(false);
		});
		
		add.init(() -> {
			if (!add.isEnabled()) return;
			EventData event = new EventData();
			
			Manager manager = Manager.getInstance();
			
			//if (manager.calendarDisplay.dayClicked()) {
				//event.update("", manager.calendarDisplay.getSelectedDay().replaceAll(" ",  ""), "", "");
			//}
			
			//if (manager.eventDisplay != null) {
				manager.remove(manager.eventDisplay);
			//}
			
			stopShowingTime();
			//EventData.events.add(event);
			manager.eventDisplay = new EventDisplay(event);
			manager.eventDisplay.init();
			manager.eventDisplay.setLocation(add.getX() + panel.getX()
			- manager.eventDisplay.getWidth() / 2, panel.getY() + panel.getHeight());
			manager.add(manager.eventDisplay);
		});
		
		time.init(() -> {
			if (!time.isEnabled()) return;
			TimeDisplay timeDisplay = new TimeDisplay();
			timeDisplay.init();
			timeDisplay.setLocation(time.getX() + panel.getX() - timeDisplay.getWidth() / 2,
					panel.getY() + panel.getHeight());
			
			Manager manager = Manager.getInstance();
			manager.remove(manager.eventDisplay);
			manager.add(timeDisplay);
			manager.getContentPane().setComponentZOrder(timeDisplay, 0);
			manager.repaint();
			//System.out.println("time pressed");
		});
		
		search.init(() -> {
			setSearchMode(true);
			//repaint();
			//System.out.println("search pressed");
		});
		
		exitSearch.init(() -> {
			setSearchMode(false);
			searchField.setForeground(Color.GRAY);
			searchField.setText("Search");
			Manager manager = Manager.getInstance();
			if (manager.searchDisplay != null) {
				manager.remove(manager.searchDisplay);
				manager.searchDisplay = null;
				manager.add(manager.calendarDisplay);
			}
			manager.remove(manager.eventDisplay);
			manager.calendarDisplay.repaint();
		});
		
		month.setFont(new Font("Arial", Font.BOLD, 24));
		
		searchField.setFont(new Font("Arial", Font.PLAIN, 20));
		searchField.setBorder(null);
		searchField.setOpaque(false);
		searchField.setForeground(Color.GRAY);
		searchField.setText("Search");
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					Manager manager = Manager.getInstance();
					manager.remove(manager.calendarDisplay);
					if (manager.searchDisplay == null) {
						manager.searchDisplay = new SearchDisplay(searchField.getText());
						manager.searchDisplay.init();
						manager.add(manager.searchDisplay);
					} else {
						manager.searchDisplay.update(searchField.getText());
					}
					manager.repaint();
				}
			}
		});
		searchField.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if ("Search".equals(searchField.getText())) {
					searchField.setForeground(Color.BLACK);
					searchField.setText("");
				}
			}
		});
		
		for (JComponent button : new JComponent[] {leftArrow, rightArrow,
				add, time, search, exitSearch, month, searchField}) {
			add(button);
		}
		
		setSearchMode(false);
	}
	
	private void setSearchMode(boolean b) {
		for (JButton button : new JButton[] {leftArrow, rightArrow, add, time, search}) {
			button.setEnabled(!b);
			//button.setVisible(!searchMode);
		}
		//System.out.println(add.isEnabled());
		month.setEnabled(!b);
		month.setVisible(!b);
		exitSearch.setEnabled(b);
		searchField.setEnabled(b);
		searchField.setVisible(b);
		//exitSearch.setVisible(searchMode);
		
		if (b) {
			int calendarx = Manager.getInstance().calendarDisplay.getX();
			int calendarwidth = Manager.getInstance().calendarDisplay.getWidth();
			setBounds(calendarx + 4, 20, calendarwidth - 8, 40);
			exitSearch.setBounds(calendarwidth - 42, 10, 20, 20);
			searchField.setBounds(47, 7, calendarwidth - 100, 27);
		} else {
			leftArrow.setBounds(0, 7, 15, 27);
			rightArrow.setBounds(25, 7, 15, 27);
			String text = Manager.getInstance().calendarDisplay.getMonthName();
			month.setText(text);
			month.setBounds(60, 7, getFontMetrics(month.getFont()).stringWidth(text), 27);
			add.setBounds(month.getWidth() + 80, 7, 27, 27);
			time.setBounds(add.getX() + 37, 7, 27, 27);
			search.setBounds(time.getX() + 37, 7, 27, 27);
			
			int width = search.getX() + 27;
			int calendarx = Manager.getInstance().calendarDisplay.getX();
			int relativex = (Manager.getInstance().calendarDisplay.getWidth() - width) / 2;
			setBounds(calendarx + relativex, 20, width, 40);
		}
		
		searchMode = b;
	}
	
	public void stopShowingTime() {
		for (Component c : Manager.getInstance().getContentPane().getComponents()) {
			if (c instanceof TimeDisplay) {
				Manager.getInstance().remove(c);
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	
		if (searchMode) drawSearchMode(g2d);
		else drawRegular(g2d);
	}
	
	private void drawSearchMode(Graphics2D g2d) {
		Color bgcolor = getBackground();
		
		g2d.drawImage(Icon.search(27, bgcolor, Color.GRAY), 10, 7, null);
		Color excolor = exitSearch.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.ex(20, bgcolor, excolor), exitSearch.getX(),
				10, null);
		//System.out.println(exitSearch.getX());
		g2d.setColor(Color.GRAY);
		g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
	
	private void drawRegular(Graphics2D g2d) {
		Color bgcolor = getBackground();
		
		Color leftArrowColor = leftArrow.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.leftArrow(15, bgcolor, leftArrowColor),
				leftArrow.getX(), 7, null);
		Color rightArrowColor = rightArrow.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.rightArrow(15, bgcolor, rightArrowColor),
				rightArrow.getX(), 7, null);
		Color plusColor = add.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.plus(27, bgcolor, plusColor), add.getX(), 7, null);
		Color clockColor = time.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.clock(27, bgcolor, clockColor), time.getX(), 7, null);
		Color searchColor = search.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(Icon.search(27, bgcolor, searchColor), search.getX(), 7, null);
	}
}