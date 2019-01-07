package sonraycalendar;
/*
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;*/
import java.io.Serializable;
/*import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
*/
public class OldEventData implements Serializable {
	private static final long serialVersionUID = -1755174366849161303L;
	/*public static final List<OldEventData> events = new ArrayList<>();
	public static final String[] repeatOptions = {"daily", "weekly",
			"monthly", "annually"};
	
	private String title;
	private String startDate;
	private String endDate;
	private String time;
	private int repeat;
	private String notes;
	
	public OldEventData() {
		this.title = "";
		this.startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
		this.endDate = this.startDate;
		this.time = "";
		this.repeat = -1;
		this.notes = "";
	}
	
	public void update(String title, String startDate, String endDate,
			String time, int repeat, String notes) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.time = time;
		this.repeat = repeat;
		this.notes = notes;
	}
	
	/*public EventData(String title, int startDate, int endDate, int time, int repeat, String notes) {
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.time = time;
		this.repeat = repeat;
		this.notes = notes;
	}*/
	
	/*@Override
	public String toString() {
		String data = String.format("title=%s,startDate=%d,endDate=%d,time=%d,repeat=%d,notes=%s",
				title, startDate, endDate, time, repeat, notes);
		return "sonraycalendar.EventData[" + data + "]";
	}*/
	/*
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
	
	public static String verifyTime(String time, boolean spansMultipleDays) {
		String temp = time.replaceAll("[ .]", "").toUpperCase().replace("M", "");
		
		if (temp.length() < 1) { return null; }
		
		if (temp.charAt(0) == '1' && temp.charAt(1) != ':') {
			if (temp.charAt(1) > '2') { return null; }
		} else if (temp.charAt(0) != '0') {
			temp = "0" + temp; }
		
		int hyphen = temp.indexOf('-');
		
		if (temp.charAt(hyphen + 1) == '1' && temp.charAt(hyphen + 2) != ':') {
			if (temp.charAt(hyphen + 2) > '2') { return null; }
		} else if (temp.charAt(hyphen + 1) != '0') {
			temp = temp.substring(0, hyphen + 1) + "0" + temp.substring(hyphen + 1, temp.length());
		}
		
		String oneTime = "[\\d]{2}[:][\\d]{2}[A|P]";
		if (!temp.matches(oneTime + "[-]" + oneTime)) { return null; }
		if (spansMultipleDays) { return temp; }
		
		int firstTimeHr = Integer.parseInt(temp.substring(0, 2));
		int firstTimeVal = (temp.charAt(5) == 'A' ?
				firstTimeHr : firstTimeHr + 12) * 60
				+ Integer.parseInt(temp.substring(3, 5));
		int secondTimeHr = Integer.parseInt(temp.substring(7, 9));
		int secondTimeVal = (temp.charAt(12) == 'A' ?
				secondTimeHr : secondTimeHr + 12) * 60
				+ Integer.parseInt(temp.substring(10, 12));
		if (firstTimeVal < secondTimeVal) { return temp; }
		
		return null;
	}
	
	public static String verifyDate(String date) {
		String temp = date.replace(" ", "");
		
		if (temp.length() < 1) { return null; }
		
		if (temp.charAt(0) == '1' && temp.charAt(1) != '/') {
			if (temp.charAt(1) > '2') { return null; }
		} else if (temp.charAt(0) != '0') {
			temp = "0" + temp; }
		
		int slash1 = temp.indexOf('/');
		
		if (temp.charAt(slash1 + 1) == '3' && temp.charAt(slash1 + 2) != '/') {
			if (temp.charAt(slash1 + 2) > '1') { return null; }
		} else if (temp.charAt(slash1 + 1) < '3' && temp.charAt(slash1 + 2) != '/') {
		} else if (temp.charAt(slash1 + 1) != '0'){
			temp = temp.substring(0, slash1 + 1) + "0" + temp.substring(slash1 + 1, temp.length());
		}
		
		int slash2 = temp.lastIndexOf('/');
		
		int yrlen = temp.length() - slash2 - 1;
		
		if (yrlen < 0) { return null; }
		StringBuilder zeros = new StringBuilder();
		for (int i = 0; i < 4 - yrlen; i++) { zeros.append('0'); }
		temp = temp.substring(0, slash2 + 1) + zeros.toString() + temp.substring(slash2 + 1, temp.length());
		
		if (!temp.matches("[\\d]{2}[/][\\d]{2}[/][\\d]{4}")) { return null; }
		
		Month.leapYear(Integer.parseInt(temp.substring(6, 10)));
		int enteredDays = Integer.parseInt(temp.substring(3, 5));
		int maxDays = Month.getDays(Integer.parseInt(temp.substring(0, 2)));
		
		if (maxDays >= enteredDays) { return temp; }
		
		return null;
	}
	
	public static boolean isPrecedingOrEqualTo(String datestr1, String datestr2) {
		try {
			Date date1 = new SimpleDateFormat("MM/dd/yyyy").parse(datestr1);
			Date date2 = new SimpleDateFormat("MM/dd/yyyy").parse(datestr2);
			return date1.before(date2) || date1.equals(date2);
		} catch (ParseException e) {
			System.out.println("invalid entry or unexpected error while parsing");
		}
		return false;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLongDate(boolean startOrEnd) {
		String date = startOrEnd ? startDate : endDate;
		int month = Integer.parseInt(date.substring(0, 2));
		int day = Integer.parseInt(date.substring(3, 5));
		int year = Integer.parseInt(date.substring(6, 10));
		String weekday = Month.getWeekdayAsString(month, day, year);
		
		return String.format("%s, %s %d, %d", weekday, Month.getName(month), day, year);
	}
	
	public String getShortDate(boolean startOrEnd) {
		String date = startOrEnd ? startDate : endDate;
		
		String month = date.substring(0, 2);
		String day = date.substring(3, 5);
		String year = date.substring(6, 10);
		
		return month + " / " + day + " / " + year;
	}
	
	public boolean inMonth(boolean startOrEnd, int month, int year) {
		String date = startOrEnd ? startDate : endDate;
		
		int dateMonth = Integer.parseInt(date.substring(0, 2));
		int dateYear = Integer.parseInt(date.substring(6, 10));
		return dateMonth == month && dateYear == year;
	}
	
	public int getMonth(boolean startOrEnd) {
		String date = startOrEnd ? startDate : endDate;
		return Integer.parseInt(date.substring(0, 2));
	}
	public int getDay(boolean startOrEnd) {
		String date = startOrEnd ? startDate : endDate;
		return Integer.parseInt(date.substring(3, 5));
	}
	public int getYear(boolean startOrEnd) {
		String date = startOrEnd ? startDate : endDate;
		return Integer.parseInt(date.substring(6, 10));
	}
	//public Date getDate(boolean startOrEnd) throws ParseException {
		//String dateToParse = startOrEnd ? startDate : endDate;
		//return new SimpleDateFormat("MM/dd/yyyy").parse(dateToParse);
	//}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTime() {
		if (time.equals("")) return "";
		
		String time1 = time.charAt(0) == '0' ? time.substring(1, 5) : time.substring(0, 5);
		String ampm1 = String.valueOf(time.charAt(5));
		String time2 = time.charAt(7) == '0' ? time.substring(8, 12) : time.substring(7, 12);
		String ampm2 = String.valueOf(time.charAt(12));
		return String.format("%s %sM - %s %sM", time1, ampm1, time2, ampm2);
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}*/
}