package sonraycalendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TopPanel extends JPanel {
	private final EventButton leftArrow, rightArrow, add, time, search, exitSearch;
	private final JLabel month;
	private final JTextField searchField;
	
	private CurrentTimePanel currentTimePanel;
	private boolean searchMode;
	
	private static class CurrentTimePanel extends JPanel { //panel to show current time and has button so can
		//return to current month (goToCurrentMonth)
		private EventButton goToCurrentMonth;
		private JLabel now;
		
		private CurrentTimePanel() {
			goToCurrentMonth = new EventButton();
			now = new JLabel();
		}
		
		private void init() {
			setLayout(null);
			setSize(190, 40);
			setBackground(Color.WHITE);
			
			now.setFont(new Font("Arial", Font.PLAIN, 10));
			now.setText(new SimpleDateFormat("EEE, MM/dd/yyyy, h:mm a z").format(new Date()));
			now.setBounds(10, 17, 160, 20);
			add(now);
			
			goToCurrentMonth.setBounds(165, 20, 14, 14);
			goToCurrentMonth.init(() -> {
				Manager manager = Manager.getInstance();
				Calendar calendar = Calendar.getInstance();
				manager.calendarPanel.setMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
				manager.topPanel.setSearchMode(false);
				manager.topPanel.removeCurrentTimePanel();
				manager.repaint();
			});
			add(goToCurrentMonth);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);
			g.fillRect(0, 0, getWidth(), 15);
			Color currentColor = goToCurrentMonth.hovering() ? Color.RED : Color.GRAY;
			g.drawImage(DrawingUtilities.pin(14, getBackground(), currentColor), 165, 20, null);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		}
	}
	
	public TopPanel() {
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
		setLayout(null);
		setBackground(Color.WHITE);
		
		leftArrow.init(() -> { //go back one month
			if (!leftArrow.isEnabled()) return;
			CalendarPanel calendarPanel = Manager.getInstance().calendarPanel;
			int[] month = calendarPanel.getMonth();
			
			if (month[0] == 1) calendarPanel.setMonth(12, month[1] - 1);
			else calendarPanel.setMonth(month[0] - 1, month[1]);
			
			calendarPanel.stopSelectingDay();
			calendarPanel.repaint();
			setSearchMode(false);
		});
		
		rightArrow.init(() -> { //go forward one month
			if (!rightArrow.isEnabled()) return;
			CalendarPanel panel = Manager.getInstance().calendarPanel;
			int[] month = panel.getMonth();
			
			if (month[0] == 12) panel.setMonth(1, month[1] + 1);
			else panel.setMonth(month[0] + 1, month[1]);
			
			panel.stopSelectingDay();
			panel.repaint();
			setSearchMode(false);
		});
		
		add.init(() -> { //add event (though only actually added when EventPanel update button pressed)
			if (!add.isEnabled()) return;
			EventData event = new EventData();
			
			Manager manager = Manager.getInstance();
			manager.removeEventPanel();
			
			try {
				String selected = manager.calendarPanel.getSelectedDay();
				if (selected != null) {
					Date date = new SimpleDateFormat("yyyyMMdd").parse(selected);
					String strdate = new SimpleDateFormat("MM/dd/yyyy").format(date);
					event.update("", strdate, "", "");
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			removeCurrentTimePanel();
			
			manager.eventPanel = new EventPanel(event);
			manager.eventPanel.init();
			int x = add.getX() + add.getWidth() / 2 + getX() - manager.eventPanel.getWidth() / 2;
			int y = getY() + getHeight();
			manager.eventPanel.setLocation(x, y);
			manager.repaint();
		});
		
		time.init(() -> { //creates current time panel
			if (!time.isEnabled()) return;
			removeCurrentTimePanel();
			
			currentTimePanel = new CurrentTimePanel();
			currentTimePanel.init();
			int x = time.getX() + time.getWidth() / 2 + getX() - currentTimePanel.getWidth() / 2;
			int y = getY() + getHeight();
			currentTimePanel.setLocation(x, y);
			
			Manager manager = Manager.getInstance();
			manager.removeEventPanel();
			manager.add(currentTimePanel);
			manager.getContentPane().setComponentZOrder(currentTimePanel, 0);
			manager.repaint();
		});
		
		search.init(() -> { //creates search bar (but not panel)
			searchField.setForeground(Color.GRAY);
			searchField.setText("Search");
			setSearchMode(true);
		});
		
		exitSearch.init(() -> { //craetes search panel (search bar already craeted)
			setSearchMode(false);
			Manager manager = Manager.getInstance();
			if (manager.searchPanel != null) {
				manager.remove(manager.searchPanel);
				manager.searchPanel = null;
				manager.add(manager.calendarPanel);
				manager.calendarPanel.repaint();
			}
		});
		
		month.setFont(new Font("Arial", Font.BOLD, 24));
		
		searchField.setFont(new Font("Arial", Font.PLAIN, 20));
		searchField.setBorder(null);
		searchField.setOpaque(false);
		searchField.setForeground(Color.GRAY);
		searchField.setText("Search");
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) { //creates search panel and removes calendar panel so not in the way
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					Manager manager = Manager.getInstance();
					manager.remove(manager.calendarPanel);
					if (manager.searchPanel == null) {
						manager.searchPanel = new SearchPanel(searchField.getText());
						manager.searchPanel.init();
						manager.add(manager.searchPanel);
						manager.repaint();
					} else {
						manager.searchPanel.update(searchField.getText());
						manager.repaint();
					}
				}
			}
		});
		
		searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if ("Search".equals(searchField.getText())) {
					searchField.setForeground(Color.BLACK);
					searchField.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {} //not used but interface requires implementing
		});
		
		for (JComponent button : new JComponent[] {leftArrow, rightArrow, add, time, search, exitSearch, month, searchField}) add(button);
		
		setSearchMode(false);
	}
	
	private void setSearchMode(boolean b) {
		for (JButton button : new JButton[] {leftArrow, rightArrow, add, time, search}) button.setEnabled(!b);
		
		month.setEnabled(!b);
		month.setVisible(!b);
		exitSearch.setEnabled(b);
		searchField.setEnabled(b);
		searchField.setVisible(b);
		
		if (b) { //resizes panel and sets bounds of search field and exit search button
			int calendarx = Manager.getInstance().calendarPanel.getX();
			int calendarwidth = Manager.getInstance().calendarPanel.getWidth();
			setBounds(calendarx + 4, 20, calendarwidth - 8, 40);
			exitSearch.setBounds(calendarwidth - 42, 10, 20, 20);
			searchField.setBounds(47, 7, calendarwidth - 100, 27);
		} else { //resizes panel and sets bounds of every button (expect exit search) and of jlabel showing month
			leftArrow.setBounds(0, 7, 15, 27);
			rightArrow.setBounds(25, 7, 15, 27);
			int[] calendarMonth = Manager.getInstance().calendarPanel.getMonth();
			month.setText(Month.getName(calendarMonth[0]) + " " + calendarMonth[1]); //month and year, e.g. January 2019
			month.setBounds(60, 7, getFontMetrics(month.getFont()).stringWidth(month.getText()), 27);
			add.setBounds(month.getWidth() + 80, 7, 27, 27);
			time.setBounds(add.getX() + 37, 7, 27, 27);
			search.setBounds(time.getX() + 37, 7, 27, 27);
			
			int width = search.getX() + 27;
			int calendarx = Manager.getInstance().calendarPanel.getX();
			int relativex = (Manager.getInstance().calendarPanel.getWidth() - width) / 2;
			setBounds(calendarx + relativex, 20, width, 40);
		}
		
		searchMode = b;
	}
	
	public void removeCurrentTimePanel() { //setting to null serves as reference for whether or not currentTimePanel
		//in topPanel (if not, trying to remove it would result in error)
		if (currentTimePanel != null) {
			Manager.getInstance().remove(currentTimePanel);
			currentTimePanel = null;
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
	
	private void drawSearchMode(Graphics2D g2d) { //draws search bar
		Color bgcolor = getBackground();
		
		g2d.drawImage(DrawingUtilities.search(27, bgcolor, Color.GRAY), 10, 7, null);
		Color excolor = exitSearch.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.ex(20, bgcolor, excolor), exitSearch.getX(), 10, null);
		g2d.setColor(Color.GRAY);
		g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
	
	private void drawRegular(Graphics2D g2d) { //draws buttons
		Color bgcolor = getBackground();
		
		Color leftArrowColor = leftArrow.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.leftArrow(15, bgcolor, leftArrowColor), leftArrow.getX(), 7, null);
		Color rightArrowColor = rightArrow.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.rightArrow(15, bgcolor, rightArrowColor), rightArrow.getX(), 7, null);
		Color plusColor = add.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.plus(27, bgcolor, plusColor), add.getX(), 7, null);
		Color clockColor = time.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.clock(27, bgcolor, clockColor), time.getX(), 7, null);
		Color searchColor = search.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.search(27, bgcolor, searchColor), search.getX(), 7, null);
	}
}