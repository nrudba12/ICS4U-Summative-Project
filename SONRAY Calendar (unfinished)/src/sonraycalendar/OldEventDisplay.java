package sonraycalendar;
/*
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;*/
import javax.swing.JPanel;
/*import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
*/
@SuppressWarnings("serial")
public class OldEventDisplay extends JPanel {/*
	private static final int WIDTH = 200;
	private static final String defaultNotes = "Add notes";
	public static final String defaultTitle = "Add title";
	private static final String defaultTime = "12:00 AM - 12:00 PM";
	
	private static class EventButton extends JButton {
		private boolean hovering, pressed;
		
		private void initMousePressed(Runnable runnable) {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					hovering = true;
					getRootPane().repaint();
				}
				@Override
				public void mouseExited(MouseEvent e) {
					hovering = false;
					getRootPane().repaint();
				}
				@Override
				public void mousePressed(MouseEvent e) {
					runnable.run();
				}
			});
		}
	}
	private final EventData event;
	//private int repeatOptionSelected;
	
	private final EventButton edit, update, delete, allDay, setTime, repeat;
	private final JTextField time, startDate, endDate, title;
	private final JTextArea notes;
	private final JComboBox<String> repeatComboBox;
	private final JLabel showDate, showTime;
	
	private boolean editMode;
	private int regHeight, editorHeight;
	
	public OldEventDisplay(EventData event) {
		this.event = event;
		regHeight = 140;
		editorHeight = 230;
		
		edit = new EventButton();
		delete = new EventButton();
		update = new EventButton();
		allDay = new EventButton();
		setTime = new EventButton();
		repeat = new EventButton();
		
		time = new JTextField();
		startDate = new JTextField();
		endDate = new JTextField();
		notes = new JTextArea();
		title = new JTextField();
		
		showDate = new JLabel();
		showTime = new JLabel();
		
		repeatComboBox = new JComboBox<String>(EventData.repeatOptions);
		
		editMode = false;
	}
	
	public void init() {
		setLayout(null);
		setSize(WIDTH, regHeight);
		setBackground(Color.WHITE);
		initButtons();
		initTextFields();
		
		int repeatInd = event.getRepeat();
		if (repeatInd < 0) {
			allDay.pressed = true;
			repeatComboBox.setSelectedIndex(0);
		} else {
			repeat.pressed = true;
			repeatComboBox.setSelectedIndex(repeatInd);
		}
		//repeatOptions.addActionListener((ActionEvent e) -> {
			//repeatOptionSelected = repeatOptions.getSelectedIndex();
		//});
		repeatComboBox.setFont(new Font("Arial", Font.PLAIN, 10));
		repeatComboBox.setBounds(76, 104, 65, 15);
		
		showDate.setText(event.getLongDate(true));
		showDate.setBounds(46, 61, WIDTH - 47, 15);
		showTime.setBounds(46, 74, WIDTH - 47, 13);
		showDate.setFont(new Font("Arial", Font.PLAIN, 10));
		showTime.setFont(new Font("Arial", Font.PLAIN, 8));
		showDate.setForeground(Color.BLACK);
		showTime.setForeground(Color.GRAY);
		
		//EventQueue.invokeLater(() -> {
			for (JComponent comp : new JComponent[] {edit, update, delete,
					allDay, setTime, repeat, time, startDate, endDate,
					repeatComboBox, notes, title, showDate, showTime}) {
				add(comp);
			}
		//});
		setEditMode(false);
		EventQueue.invokeLater(() -> repaint());
	}
	
	private void initButtons() {
		for (EventButton button : new EventButton[] {edit, update, delete,
				allDay, setTime, repeat}) {
			button.setOpaque(false);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
		}
		
		edit.setBounds(175, regHeight - 25, 15, 15);
		update.setBounds(175, editorHeight - 25, 15, 15);
		delete.setBounds(152, editorHeight - 25, 15, 15);
		allDay.setBounds(11, 63, 15, 15);
		setTime.setBounds(11, 83, 15, 15);
		repeat.setBounds(11, 103, 15, 15);
		
		edit.initMousePressed(() -> setEditMode(true));
		update.initMousePressed(() -> {
			boolean updated = updateEventData();
			if (updated) { setEditMode(false); }
		});
		
		JPanel panel = this;
		delete.initMousePressed(() -> {
			EventData.events.remove(event);
			JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, panel);
			
			EventQueue.invokeLater(() -> {
				frame.remove(panel);
				frame.revalidate();
				frame.repaint();
			});
		});
		
		allDay.initMousePressed(() -> {
			allDay.pressed = true;
			setTime.pressed = false;
			time.setEnabled(false);
			time.setVisible(false);
		});
		
		setTime.initMousePressed(() -> {
			setTime.pressed = true;
			allDay.pressed = false;
			time.setText(defaultTime);
			time.setEnabled(true);
			time.setVisible(true);
		});
		
		repeat.initMousePressed(() -> {
			repeat.pressed = !repeat.pressed;
			repeatComboBox.setEnabled(repeat.pressed);
			repeatComboBox.setVisible(repeat.pressed);
		});
		
		allDay.pressed = "".equals(event.getTime());
		setTime.pressed = !allDay.pressed;
	}
	
	private void initTextFields() {
		class TextLimit extends KeyAdapter {
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
		
		for (JTextComponent textComponent : new JTextComponent[] {time,
				startDate, endDate, title, notes}) {
			textComponent.setFont(new Font("Arial", Font.PLAIN, 10));
			textComponent.setBorder(null);
			textComponent.setOpaque(false);
		}
		time.setBounds(82, 84, 108, 15);
		startDate.setBounds(65, 126, 126, 15);
		endDate.setBounds(62, 140, 126, 15);
		title.setBounds(10, 24, WIDTH - 20, 20);
		
		showDate.setText(event.getLongDate(true));
		showTime.setText("".equals(event.getTime()) ? "all day" : event.getTime());
		time.setText("".equals(event.getTime()) ? defaultTime : event.getTime());
		startDate.setText(event.getShortDate(true));
		endDate.setText(event.getShortDate(false));
		title.setText("".equals(event.getTitle()) ? defaultTitle : event.getTitle());
		notes.append("".equals(event.getNotes()) ? defaultNotes : event.getNotes());
		
		time.addKeyListener(new TextLimit(time, 19));
		startDate.addKeyListener(new TextLimit(startDate, 14));
		endDate.addKeyListener(new TextLimit(endDate, 14));
		title.addKeyListener(new TextLimit(title, 25));
		notes.addKeyListener(new TextLimit(notes, 80));
		
		title.setFont(new Font("Arial", Font.BOLD, 14));
		title.setForeground(Color.WHITE);
		title.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (defaultTitle.equals(title.getText()) && editMode) {
					title.setText(""); }
			}
			@Override
			public void focusLost(FocusEvent e) {
				if ("".equals(title.getText().replace(" ", ""))) {
					title.setText(defaultTitle); }
			}
		});
		
		notes.setRows(3);
		notes.setLineWrap(true);
		notes.setWrapStyleWord(true);
		notes.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (notes.getText().equals(defaultNotes) && editMode) {
					notes.setText(""); }
			}
			@Override
			public void focusLost(FocusEvent e) {
				if (notes.getText().replace(" ", "").equals("")) {
					notes.setText(defaultNotes); }
			}
		});
	}
	
	private void setEditMode(boolean b) {
		edit.setEnabled(!b);
		for (JComponent component : new JComponent[] {update, delete,
				allDay, setTime, repeat, startDate, endDate}) {
			component.setEnabled(b); }
		
		showDate.setVisible(!b);
		showTime.setVisible(!b);
		startDate.setVisible(b);
		endDate.setVisible(b);
		title.setEditable(b);
		notes.setEditable(b);
		
		time.setEnabled(setTime.pressed && b);
		time.setVisible(setTime.pressed && b);
		
		repeatComboBox.setEnabled(repeat.pressed && b);
		repeatComboBox.setVisible(repeat.pressed && b);
		
		if (b) {
			//notes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			//title.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			//title.setBounds(9, 9, WIDTH - 19, 36);			
			notes.setBounds(45, 160, 143, 43);
			
			setSize(WIDTH, editorHeight);
		} else {
			//notes.setBorder(null);
			//title.setBorder(null);
			//title.setBounds(10, 10, WIDTH - 20, 35);
			showDate.setText(event.getLongDate(true));
			showTime.setText("".equals(event.getTime()) ? "all day" : event.getTime());
			
			try {
				int height = notes.modelToView(notes.getDocument()
						.getEndPosition().getOffset() - 1).y;
				int lines = height / notes.getFont().getSize() + 1;
				regHeight = 115 + 15 * (lines);
			} catch (BadLocationException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {}
			
			notes.setBounds(45, 94, 143, 15 * (regHeight - 110));
			edit.setLocation(175, regHeight - 25);
			setSize(WIDTH, regHeight);
		}
		
		editMode = b;
	}
	
	private boolean updateEventData() {
		String title, startDate, endDate, time, notes;
		int repeat = this.repeat.pressed ? repeatComboBox.getSelectedIndex() : -1;
		
		title = this.title.getText().equals(defaultTitle) ? "" : this.title.getText();
		startDate = EventData.verifyDate(this.startDate.getText());
		endDate = EventData.verifyDate(this.endDate.getText());
		
		if (startDate == null || endDate == null) {
			JOptionPane.showMessageDialog(null,
					"invalid start or end date, must be\n"
							+ "in format MM/DD/YYYY and must have\n"
							+ "valid number of days for month");
			return false;
		} else if (!EventData.isPrecedingOrEqualTo(startDate, endDate)) {
			JOptionPane.showMessageDialog(null,
					"start date must precede or equal end date");
			return false;
		}
		
		if (allDay.pressed) {
			time = "";
		} else {
			time = EventData.verifyTime(this.time.getText(),
					!(startDate.equals(endDate) || this.repeat.pressed));
		}
		
		if (time == null) {
			JOptionPane.showMessageDialog(null,
					"time must be in format 12:00 AM - 12:00 PM\n"
							+ "and first time must precede second (unless\n"
							+ "event non-repeating and spans multiple days)");
			return false;
		}
		
		notes = defaultNotes.equals(this.notes.getText()) ? "" : this.notes.getText();
		
		event.update(title, startDate, endDate, time, repeat, notes);
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		if (editMode) drawEditMode(g2d);
		else drawRegularMode(g2d);
		
		//g2d.setStroke(new BasicStroke(5));
		//g2d.drawRect(0, 0, getBounds().width, getBounds().height);
	}
	
	private void drawRegularMode(Graphics2D g) {
		g.setColor(new Color(106, 168, 78));
		g.fillRect(0, 0, WIDTH, 50);
		
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(1));
		g.draw(Icon.clock(10, 60, 26));
		g.setStroke(new BasicStroke(3));
		g.draw(Icon.notes(11, 98, 24));
		
		//g.setColor(Color.BLACK);
		//g.setFont(new Font("Arial", Font.PLAIN, 10));
		//g.drawString("Wednesday, January 2, 2019", 46, 72);
		//g.setColor(Color.GRAY);
		//g.setFont(new Font("Arial", Font.PLAIN, 8));
		//g.drawString("11:30 AM - 2:30 PM", 46, 82);
		
		Color pencil = edit.hovering ? Color.RED : Color.GRAY;
		g.drawImage(Icon.pencil(15, Color.WHITE, pencil), 175,
				regHeight - 25, null);
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawRect(0, 0, WIDTH - 1, regHeight - 1);
	}
	
	private void drawEditMode(Graphics2D g) {
		g.setColor(new Color(106, 168, 78));
		g.fillRect(0, 0, WIDTH, 15 * 2 + 20);
		
		String[] labels = {"all day", "set time", "repeat"};
		EventButton[] buttons = {allDay, setTime, repeat};
		Color filling;
		for (int i = 0; i < 3; i++) {			
			if (buttons[i].pressed) {
				filling = Color.RED;
			} else if (buttons[i].hovering) {
				filling = new Color(255, 153, 153);
			} else { filling = Color.WHITE; }
			
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.BLACK);
			g.drawOval(11, 63 + i * 20, 15, 15);
			g.setColor(filling);
			g.fillOval(14, 66 + i * 20, 10, 10);
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 10));
			g.drawString(labels[i], 38, 74 + i * 20);
		}
		
		g.drawString("Start date:", 10, 136);
		g.drawString("End date:", 10, 150);
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(3));
		g.draw(Icon.notes(10, 164, 20));
		
		Color check = update.hovering ? Color.RED : Color.GRAY;
		Color garbage = delete.hovering ? Color.RED : Color.GRAY;
		g.drawImage(Icon.check(15, Color.WHITE, check), 175,
				editorHeight - 25, null);
		g.drawImage(Icon.garbage(11, Color.WHITE, garbage), 154,
				editorHeight - 25, null);
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawRect(0, 0, WIDTH - 1, editorHeight - 1);
	}*/
}