package sonraycalendar;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

public class EventData implements Serializable {
	private static final long serialVersionUID = 7930418148699161983L;
	public static final List<EventData> events = new ArrayList<>();
	
	private String title;
	private String date; //in usable date format (more detail in verifyDate())
	private String time; //in usable time format (more detail in verifyTime())
	private String notes;
	
	public EventData() {
		this.title = "";
		this.date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		this.time = "";
		this.notes = "";
	}
	
	public boolean update(String title, String date, String time, String notes) {
		String checkedDate = EventData.verifyDate(date);
		
		if (checkedDate == null) {
			JOptionPane.showMessageDialog(null, "invalid start or end date, must be\n"
							+ "in format MM/DD/YYYY and must have\n"
							+ "valid number of days for month");
			return false;
		}
		
		String checkedTime = EventData.verifyTime(time);
		if (time == null) {
			JOptionPane.showMessageDialog(null, "time must be in format 12:00 AM - 12:00 PM\n"
							+ "and first time must come before second");
			return false;
		}
		
		this.title = title;
		this.date = checkedDate;		
		this.time = checkedTime;
		this.notes = notes;
		
		return true; //returned if successfully updated, returned false otherwise
	}
	
	public static void serialize() { //writes list of events into file
		EventData[] toSave = events.toArray(new EventData[events.size()]);
		File file = new File("sonray calendar events.ser");
		try {
			file.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(toSave);
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deserialize() { //reads list of events from file
		File file = new File("sonray calendar events.ser");
		if (!file.exists()) return;
		
		EventData[] saved = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			saved = (EventData[]) objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		for (EventData event : saved) events.add(event);
	}
	
	private static String verifyTime(String time) { //makes sure time entered can be converted to usable
		//time format and does that conversion; usable format allows to compare between other times in
		//sortEvents(), can be rewritten to different format as needed, etc.; format: 00001200
		//(i.e. 12:00 AM - 12:00 PM)
		if ("".equals(time)) return "";
		
		StringBuilder sb = new StringBuilder(time.toUpperCase().replaceAll("[^\\dAP]", ""));
		if (sb.length() < 8 || sb.length() > 10) return null;
		
		int firstAMPM = sb.indexOf("A");
		if (firstAMPM < 0) sb.indexOf("P");
		if (firstAMPM < 4) sb.insert(0, '0');
		if (sb.length() < 10) sb.insert(5, '0');
		
		for (int i = 0; i <= 4; i += 4) { //only runs twice but didn't want to rewrite twice
			if ("12".equals(sb.substring(i, i + 2))) sb.replace(i, i + 2, "00");
			if (sb.charAt(i + 4) == 'P') {
				try {
					int hour = Integer.parseInt(sb.substring(i, i + 2));
					sb.replace(i, i + 2, Integer.toString(hour + 12));
				} catch (NumberFormatException e) { return null; }
			}
			sb.deleteCharAt(i + 4);
		}
		
		String finalTime = sb.toString();
		if (!finalTime.matches("[\\d]{8}")) return null;
		if (Integer.parseInt(finalTime.substring(0, 4)) > Integer.parseInt(finalTime.substring(4, 8))) return null;
		
		return finalTime;
	}
	
	private static String verifyDate(String date) { //makes sure date entered can be converted to usable
		//time format and does that conversion; usable format allows to compare between other times in
		//sortEvents(), can be rewritten to different format as needed, etc.; format: yyyyMMdd
		StringBuilder sb = new StringBuilder(date.replaceAll("[^\\d/]", ""));
		if (sb.length() < 5 || sb.length() > 10) return null;
		
		if (sb.charAt(1) == '/') sb.insert(0, '0');
		if (sb.charAt(4) == '/') sb.insert(3, '0');
		int len = 4 - sb.substring(6).length();
		for (int i = 0; i < len; i++) sb.insert(6, '0');
		sb.deleteCharAt(2);
		sb.deleteCharAt(4);
		sb.insert(0, sb.substring(4));
		sb.delete(8, 12);
		
		String finalDate = sb.toString();
		if (!finalDate.matches("[\\d]{8}")) return null;
		
		int month = Integer.parseInt(finalDate.substring(4, 6));
		if (month > 12) return null;
		Month.leapYear(Integer.parseInt(finalDate.substring(0, 4)));
		if (Integer.parseInt(finalDate.substring(6)) > Month.getDays(month)) return null;
		
		return finalDate;
	}
	
	public static String dateToString(int month, int day, int year) { //converts month, day, and year (int
		//inputs) to yyyyMMdd format (i.e. usable date format)
		Format dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);
		return dateFormat.format(calendar.getTime());
	}
	
	public Color getColor(boolean darkerOrLighter) { //colour of event; if event before today, purple; else,
		//green; darkerOrLighter decides whether darker or lighter version of colour
		Color color;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date eventDate = null, today = null;
		try {
			eventDate = dateFormat.parse(date);
			today = dateFormat.parse(dateFormat.format(new Date()));
			//using just eventDate.before(new Date()) to check if before won't work
			//(returns false if dates are the same)
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (darkerOrLighter) color = eventDate.before(today) ? new Color(103, 78, 167) : new Color(106, 168, 78);
		else color = eventDate.before(today) ? new Color(180, 167, 214) : new Color(147, 196, 125);
		return color;
	}

	public String getDisplayableTime() { //converts event's time from usable format ("00001200") to displayable ("12:00 AM - 12:00 PM")
		if ("".equals(time)) return "";
		try {
			Date startTime = new SimpleDateFormat("HHmm").parse(time.substring(0, 4));
			Date endTime = new SimpleDateFormat("HHmm").parse(time.substring(4, 8));
			String strStartTime = new SimpleDateFormat("h:mm a").format(startTime);
			String strEndTime = new SimpleDateFormat("h:mm a").format(endTime);
			return strStartTime + " - " + strEndTime;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static void sortEvents(List<EventData> events) { //sorts by date and time (earlier = before)
		section(events, 0, events.size() - 1);
	}
	
	private static void section(List<EventData> events, int low, int high) {
		if (low < high) {
			int ind = partition(events, low, high);
			section(events, low, ind - 1);
			section(events, ind + 1, high);
		}
	}
	
	private static int partition(List<EventData> events, int low, int high) {
		String pivotDate = events.get(high).getDate();
		String pivotTime = events.get(high).getTime();
		int i = low - 1;
		for (int j = low; j < high; j++) {
			if (events.get(j).getDate().compareTo(pivotDate) < 0) {
				i++; Collections.swap(events, i, j);
			} else if (events.get(j).getDate().compareTo(pivotDate) == 0) {
				if (events.get(j).getTime().compareTo(pivotTime) <= 0) {
					i++; Collections.swap(events, i, j);
				}
			}
		}
		Collections.swap(events, i + 1, high);
		return i + 1;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDate() {
		return date;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getNotes() {
		return notes;
	}
}