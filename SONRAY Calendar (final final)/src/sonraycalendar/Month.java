package sonraycalendar;

public enum Month {
	JAN("January", 31), FEB("February", 28), MAR("March", 31), APR("April", 30), MAY("May", 31), JUN("June", 30),
	JUL("July", 31), AUG("August", 31), SEP("September", 30), OCT("October", 31), NOV("November", 30), DEC("December", 31);
	
	public static final String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	private String name;
	private int days;
	
	Month(String name, int days) {
		this.name = name;
		this.days = days;
	}
	
	public static void leapYear(int year) {
		if (year % 4 == 0) FEB.days = 29;
		else FEB.days = 28;
	}
	
	public static int getWeekday(String date) { //date must be in format yyyyMMdd
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6));
		int day = Integer.parseInt(date.substring(6, 8));
		
		int renumMonth, century, yearInCentury;
		
		if (month < 3) {
			int renumYear = year - 1;
			renumMonth = month + 10;
			century = renumYear / 100;
			yearInCentury = renumYear % 100;
		} else {
			renumMonth = month - 2;
			century = year / 100;
			yearInCentury = year % 100;
		}
		
		int weekday = (day + (int) (2.6*renumMonth - 0.2) - 2*century + yearInCentury + yearInCentury/4 + century/4) % 7;
		if (weekday < 0) weekday += 7;
		return weekday;
	}
	
	public static String getWeekdayAsString(String date) {
		return weekdays[getWeekday(date)];
	}
	
	public static String getName(int month) {
		return Month.values()[month - 1].name;
	}
	
	public static int getDays(int month) {
		return Month.values()[month - 1].days;
	}
}