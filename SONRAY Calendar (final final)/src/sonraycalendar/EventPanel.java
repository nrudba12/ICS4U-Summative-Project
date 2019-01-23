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
import java.awt.event.MouseAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class EventPanel extends JPanel {
	public static final String defaultTitle = "Add title";
	private static final int WIDTH = 200;
	
	private final EventData event;
	
	private final EventButton edit, update, delete, allDay, setTime;
	private final JTextField time, date, title;
	private final JTextArea notes;
	private final JLabel displayDate, displayTime;
	
	private boolean editMode;
	private int regHeight, editorHeight;
	
	public EventPanel(EventData event) {
		this.event = event;
		regHeight = 140;
		editorHeight = 200;
		
		edit = new EventButton();
		delete = new EventButton();
		update = new EventButton();
		allDay = new EventButton();
		setTime = new EventButton();
		
		time = new JTextField();
		date = new JTextField();
		notes = new JTextArea();
		title = new JTextField();
		
		displayDate = new JLabel();
		displayTime = new JLabel();
		
		editMode = false;
	}
	
	public void init() {
		setLayout(null); //no layout manager because want to use absolute position of buttons,
		//text boxes, etc. (had problem earlier where FlowLayout caused text boxes to be placed
		//side by side even after calling setLocation()
		setSize(WIDTH, regHeight);
		setBackground(Color.WHITE);
		initButtons();
		initTextFields();
		
		addMouseListener(new MouseAdapter() {}); //so that hover or click on this panel
		//won't affect panels behind it
		
		displayDate.setBounds(46, 61, WIDTH - 47, 15);
		displayTime.setBounds(46, 74, WIDTH - 47, 13);
		displayDate.setFont(new Font("Arial", Font.PLAIN, 10));
		displayTime.setFont(new Font("Arial", Font.PLAIN, 8));
		displayDate.setForeground(Color.BLACK);
		displayTime.setForeground(Color.GRAY);
		
		for (JComponent comp : new JComponent[] {edit, update, delete, allDay, setTime,
				time, date, notes, title, displayDate, displayTime}) add(comp);
		
		setEditMode(false);
		
		//makes sure event panel is always at front of screen; since event panel is created
		//in almost every other class, easier to do here instead of relegating to outside
		Manager.getInstance().add(this);
		Manager.getInstance().getContentPane().setComponentZOrder(this, 0);
	}
	
	private void initButtons() {
		edit.setBounds(175, regHeight - 25, 15, 15);
		update.setBounds(175, editorHeight - 25, 15, 15);
		delete.setBounds(152, regHeight - 25, 15, 15);
		//width is much wider than circles representing buttons since better user experience (more info
		//in github repo)
		allDay.setBounds(11, 63, WIDTH - 22, 15);
		setTime.setBounds(11, 83, WIDTH - 22, 15);
		
		edit.init(() -> setEditMode(true));
		update.init(() -> {
			if (updateEventData()) {
				setEditMode(false);
				if (!EventData.events.contains(event)) EventData.events.add(event);
				Manager manager = Manager.getInstance();
				//reset calendarPanel, sidePanel, and searchPanel since event updated
				String selectedDay = manager.calendarPanel.getSelectedDay();
				manager.sidePanel.setEvents(selectedDay);
				if (manager.searchPanel != null) manager.searchPanel.reset();
				else {
					int[] month = manager.calendarPanel.getMonth();
					manager.calendarPanel.setMonth(month[0], month[1]);
				}
				manager.repaint();
			}
		});
		
		delete.init(() -> {
			EventData.events.remove(event);
			Manager manager = Manager.getInstance();
			manager.remove(manager.eventPanel);
			//reset calendarPanel, sidePanel, and searchPanel since event deleted
			String selectedDay = manager.calendarPanel.getSelectedDay();
			manager.sidePanel.setEvents(selectedDay);
			if (manager.searchPanel != null) manager.searchPanel.reset();
			int[] month = manager.calendarPanel.getMonth();
			manager.calendarPanel.setMonth(month[0], month[1]);
			manager.repaint();
		});
		
		allDay.init(() -> {
			if (!editMode) return;
			
			allDay.pressed = true;
			setTime.pressed = false;
			setTime.setSize(WIDTH - 22, 15); //bounds reset when text box can no longer be pressed
			time.setEnabled(false);
			time.setVisible(false);
		});
		
		setTime.init(() -> {
			if (!editMode) return;
			
			setTime.pressed = true;
			allDay.pressed = false;
			setTime.setSize(70, 15); //bounds reset so doesn't get in way of time text box
			time.setText("12:00 AM - 12:00 PM");
			time.setEnabled(true);
			time.setVisible(true);
		});
		
		allDay.pressed = "".equals(event.getTime());
		setTime.pressed = !allDay.pressed;
		if (setTime.pressed) setTime.setSize(70, 15);
	}
	
	private void initTextFields() {
		class TextLimit extends KeyAdapter { //limits num of characters that can be typed in text component
			private JTextComponent textComponent;
			private int limit;
			
			TextLimit(JTextComponent textComponent, int limit) {
				this.textComponent = textComponent;
				this.limit = limit;
			}
			@Override
			public void keyTyped(KeyEvent e) {
				if (textComponent.getText().length() >= limit) e.consume();
			}
		}
		
		for (JTextComponent textComponent : new JTextComponent[] {time, date, title, notes}) {
			textComponent.setBorder(null);
			textComponent.setOpaque(false);
		}
		
		time.setFont(new Font("Arial", Font.PLAIN, 10));
		date.setFont(new Font("Arial", Font.PLAIN, 10));
		time.setBounds(82, 83, 105, 15);
		date.setBounds(42, 109, 145, 15);
		title.setBounds(10, 24, WIDTH - 20, 20);
		
		time.setText(event.getDisplayableTime());
		
		try { //reformats date to readable format (MM / dd / yyyy)
			Date dateob = new SimpleDateFormat("yyyyMMdd").parse(event.getDate());
			date.setText(new SimpleDateFormat("MM / dd / yyyy").format(dateob));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		time.addKeyListener(new TextLimit(time, 19));
		date.addKeyListener(new TextLimit(date, 14));
		title.addKeyListener(new TextLimit(title, 25));
		notes.addKeyListener(new TextLimit(notes, 80));
		
		//when only whitespace as title or notes, when clicked: text field empty, when not clicked: text field
		//shows "Add title" or "Add notes" respectively
		title.setForeground(getBackground());
		if ("".equals(event.getTitle())) {
			title.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 14));
			title.setText(defaultTitle);
		} else {
			title.setFont(new Font("Arial", Font.BOLD, 14));
			title.setText(event.getTitle());
		}
		title.addFocusListener(new FocusListener() { 
			public void focusGained(FocusEvent e) {
				if (defaultTitle.equals(title.getText()) && editMode) {
					title.setFont(new Font("Arial", Font.BOLD, 14));
					title.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if ("".equals(title.getText().replace(" ", ""))) {
					title.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 14));
					title.setText(defaultTitle);
				}
			}
		});
		
		notes.setRows(3);
		notes.setLineWrap(true);
		notes.setWrapStyleWord(true);
		if ("".equals(event.getNotes())) {
			notes.setFont(new Font("Arial", Font.ITALIC, 10));
			notes.setText("Add notes");
		} else {
			notes.setFont(new Font("Arial", Font.PLAIN, 10));
			notes.setText(event.getNotes());
		}
		notes.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if ("Add notes".equals(notes.getText()) && editMode) {
					notes.setFont(new Font("Arial", Font.PLAIN, 10));
					notes.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) {
				if ("".equals(notes.getText().replace(" ", ""))) {
					notes.setFont(new Font("Arial", Font.ITALIC, 10));
					notes.setText("Add notes");
				}
			}
		});
	}
	
	private void setEditMode(boolean b) {
		displayDate.setVisible(!b);
		displayTime.setVisible(!b);
		date.setVisible(b);
		title.setEditable(b);
		notes.setEditable(b);
		
		time.setEnabled(setTime.pressed && b);
		time.setVisible(setTime.pressed && b);
		
		if (b) {		
			notes.setBounds(45, 130, 143, 43);
			delete.setBounds(152, editorHeight - 25, 15, 15);
			setSize(WIDTH, editorHeight);
		} else {
			try {
				Date strdate = new SimpleDateFormat("yyyyMMdd").parse(event.getDate());
				displayDate.setText(new SimpleDateFormat("EEEE, MMMM d, yyyy").format(strdate));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			
			displayTime.setText("".equals(event.getTime()) ? "all day" : event.getDisplayableTime());
			
			try { //changes size depending on number of rows in notes
				int height = notes.modelToView(notes.getDocument().getEndPosition().getOffset() - 1).y;
				int lines = height / notes.getFont().getSize() + 1;
				regHeight = 115 + 15 * (lines);
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {} //when first called, throws NullPointerException
			
			notes.setBounds(45, 96, 143, 15 * (regHeight - 110));
			delete.setBounds(152, regHeight - 25, 15, 15);
			edit.setLocation(175, regHeight - 25);
			setSize(WIDTH, regHeight);
		}
		
		editMode = b;
	}
	
	private boolean updateEventData() {
		String title = defaultTitle.equals(this.title.getText()) ? "" : this.title.getText();
		String notes = "Add notes".equals(this.notes.getText()) ? "" : this.notes.getText();
		String time = allDay.pressed ? "" : this.time.getText();
		
		return event.update(title, date.getText(), time, notes);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		g2d.setColor(event.getColor(true));
		g2d.fillRect(0, 0, WIDTH, 50);
		
		if (editMode) drawEditMode(g2d);
		else drawRegularMode(g2d);
		
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, WIDTH - 1, getHeight() - 1);
	}
	
	private void drawRegularMode(Graphics2D g2d) {
		Color bgcolor = getBackground();
		
		g2d.drawImage(DrawingUtilities.clock(26, bgcolor, Color.GRAY), 10, 60, null);
		g2d.drawImage(DrawingUtilities.notes(26, bgcolor, Color.GRAY), 10, 100, null);
		
		Color pencilColor = edit.hovering() ? Color.RED : Color.GRAY;
		Color garbageColor = delete.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.pencil(15, bgcolor, pencilColor), 175, regHeight - 25, null);
		g2d.drawImage(DrawingUtilities.garbage(11, bgcolor, garbageColor), 154, regHeight - 25, null);
	}
	
	private void drawEditMode(Graphics2D g2d) {
		Color bgcolor = getBackground();
		
		String[] labels = {"all day", "set time"};
		EventButton[] buttons = {allDay, setTime};
		for (int i = 0; i < 2; i++) { //only does twice but didn't want to write twice
			Color filling;
			if (buttons[i].pressed) filling = Color.RED;
			else if (buttons[i].hovering()) filling = new Color(255, 153, 153);
			else filling = bgcolor;
			
			g2d.setColor(Color.BLACK);
			g2d.drawOval(11, 63 + i * 20, 15, 15);
			g2d.setColor(filling);
			g2d.fillOval(14, 66 + i * 20, 10, 10);
			
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("Arial", Font.PLAIN, 10));
			g2d.drawString(labels[i], 38, 74 + i * 20);
		}
		
		g2d.drawString("Date:", 10, 120);
		g2d.drawImage(DrawingUtilities.notes(20, bgcolor, Color.GRAY), 10, 135, null);
		
		Color checkColor = update.hovering() ? Color.RED : Color.GRAY;
		Color garbageColor = delete.hovering() ? Color.RED : Color.GRAY;
		g2d.drawImage(DrawingUtilities.check(15, bgcolor, checkColor), 175, editorHeight - 25, null);
		g2d.drawImage(DrawingUtilities.garbage(11, bgcolor, garbageColor), 154, editorHeight - 25, null);
	}
}