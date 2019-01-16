package sonraycalendar;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventData implements Serializable {
	private static final long serialVersionUID = -1755174366849161303L;
	public static final List<EventData> events = new ArrayList<>();
	public static final String[] repeatOptions = {"daily", "weekly", "monthly", "annually"};
	
	private String title;
	private String date;
	private String time;
	private String notes;
	
	public EventData() {
		this.title = "";
		this.date = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
		this.time = "";
		this.notes = "";
	}
	
	public void update(String title, String date, String time, String notes) {
		this.title = title;
		this.date = date;
		this.time = time;
		this.notes = notes;
	}
	
	public static void addTestEvents() { //for testing
		events.clear();
		
		EventData event1 = new EventData();
		event1.update("", "01/15/2019", "", "");
		EventData event2 = new EventData();
		event2.update("test event 2", "01/16/2019", "12:00A-12:00P", "");
		EventData eventgf = new EventData();
		eventgf.update("test event gf", "01/16/2019", "12:00A-12:00P", "");
		EventData event3 = new EventData();
		event3.update("test event 3", "01/17/2019", "", "");
		EventData event4 = new EventData();
		event4.update("test event 4", "01/18/2019", "", "");
		
		events.add(eventgf);
		events.add(event1);
		events.add(event2);
		events.add(event3);
		events.add(event4);
	}
	
	public static void serialize() {
		EventData[] toSave = events.toArray(new EventData[events.size()]);
		
		try {
			File file = new File("sonray calendar events.ser");
			
			if (file.exists()) file.delete();
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
	
	public static void deserialize() {
		File file = new File("sonray calendar events.ser");
		if (!file.exists()) return;
		
		EventData[] saved = null;
		
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			saved = (EventData[]) objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		for (EventData event : saved) events.add(event);
	}
	
	public static String verifyTime(String time) {
		String temp = time.replaceAll("[ .]", "").toUpperCase().replace("M", "");
		
		if (temp.length() < 1) return null;
		
		if (temp.charAt(0) == '1' && temp.charAt(1) != ':') {
			if (temp.charAt(1) > '2') return null;
		} else if (temp.charAt(0) != '0') temp = "0" + temp;
		
		int hyphen = temp.indexOf('-');
		
		if (temp.charAt(hyphen + 1) == '1' && temp.charAt(hyphen + 2) != ':') {
			if (temp.charAt(hyphen + 2) > '2') { return null; }
		} else if (temp.charAt(hyphen + 1) != '0') {
			temp = temp.substring(0, hyphen + 1) + "0" + temp.substring(hyphen + 1, temp.length());
		}
		
		String oneTime = "[\\d]{2}[:][\\d]{2}[A|P]";
		if (!temp.matches(oneTime + "[-]" + oneTime)) { return null; }
		
		int firstTimeHr = Integer.parseInt(temp.substring(0, 2));
		int firstTimeMin = Integer.parseInt(temp.substring(3, 5));
		int firstTimeVal = ((temp.charAt(5) == 'A' ? 0 : 12) + firstTimeHr) * 60 + firstTimeMin;
		int secondTimeHr = Integer.parseInt(temp.substring(7, 9));
		int secondTimeMin = Integer.parseInt(temp.substring(10, 12));
		int secondTimeVal = ((temp.charAt(12) == 'A' ? 0 : 12) + secondTimeHr) * 60 + secondTimeMin;
		
		if (firstTimeVal < secondTimeVal) return temp;
		return null;
	}
	
	public static String verifyDate(String date) {
		String temp = date.replace(" ", "");
		
		if (temp.length() < 1) return null;
		
		if (temp.charAt(0) == '1' && temp.charAt(1) != '/') {
			if (temp.charAt(1) > '2') return null;
		} else if (temp.charAt(0) != '0') temp = "0" + temp;
		
		int slash1 = temp.indexOf('/');
		
		if (temp.charAt(slash1 + 1) == '3' && temp.charAt(slash1 + 2) != '/') {
			if (temp.charAt(slash1 + 2) > '1') return null;
		} else if (temp.charAt(slash1 + 1) < '3' && temp.charAt(slash1 + 2) != '/') {
		} else if (temp.charAt(slash1 + 1) != '0') {
			temp = temp.substring(0, slash1 + 1) + "0" + temp.substring(slash1 + 1, temp.length());
		}
		
		int slash2 = temp.lastIndexOf('/');
		
		int yrlen = temp.length() - slash2 - 1;
		
		if (yrlen < 0) return null;
		StringBuilder zeros = new StringBuilder();
		for (int i = 0; i < 4 - yrlen; i++) zeros.append('0');
		temp = temp.substring(0, slash2 + 1) + zeros.toString() + temp.substring(slash2 + 1, temp.length());
		
		if (!temp.matches("[\\d]{2}[/][\\d]{2}[/][\\d]{4}")) return null;
		
		Month.leapYear(Integer.parseInt(temp.substring(6, 10)));
		int enteredDays = Integer.parseInt(temp.substring(3, 5));
		int maxDays = Month.getDays(Integer.parseInt(temp.substring(0, 2)));
		
		if (maxDays >= enteredDays) return temp;
		return null;
	}
	
	public String getTitle() {
		return title;
	}

	public String getLongDate() {
		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));
		String weekday = Month.getWeekdayAsString(month, day, year);
		
		return String.format("%s, %s %d, %d", weekday, Month.getName(month), day, year);
	}
	
	public String getShortDate() {
		String month = date.substring(0, 2);
		String day = date.substring(3, 5);
		String year = date.substring(6, 10);
		
		return month + " / " + day + " / " + year;
	}
	
	public String getComparableDate() {
		String month = date.substring(0, 2);
		String day = date.substring(3, 5);
		String year = date.substring(6, 10);
		
		return year + month + day;
	}
	
	public Color getColor(boolean darkerOrLighter) {
		Color color;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
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
		else color = eventDate.before(today) ? new Color(180, 167, 214) : new Color(147, 196, 125/*182,215,168*/);
		return color;
	}
	
	//get rid of? replace?
	public int getMonth() {
		return Integer.parseInt(date.substring(0, 2));
	}
	public int getDay() {
		return Integer.parseInt(date.substring(3, 5));
	}
	public int getYear() {
		return Integer.parseInt(date.substring(6, 10));
	}

	public String getTime() {
		if ("".equals(time)) return "";
		
		String time1 = time.charAt(0) == '0' ? time.substring(1, 5) : time.substring(0, 5);
		String ampm1 = String.valueOf(time.charAt(5));
		String time2 = time.charAt(7) == '0' ? time.substring(8, 12) : time.substring(7, 12);
		String ampm2 = String.valueOf(time.charAt(12));
		return String.format("%s %sM - %s %sM", time1, ampm1, time2, ampm2);
	}
	
	public String getNotes() {
		return notes;
	}
}