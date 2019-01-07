package sonraycalendar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public final class Icon { //delete all deprecated once all methods fixed
	private Icon() {}
	
	private static Image getUnscaledIcon(Path2D path, Color bgcolor,
			Color strcolor, int width, int height, int strwidth) {
		Image img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		
		g2d.setColor(bgcolor);
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(strcolor);
		g2d.setStroke(new BasicStroke(strwidth, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
		g2d.draw(path);
		
		g2d.dispose();
		
		return img;
	}
	
	//move?
	public static String getFittedString(FontMetrics fontMetrics,
			String str, int space) {
		int len = fontMetrics.stringWidth(str);
		int oldstrlen = str.length();
		int newstrlen = oldstrlen;
		while (len > space - 10) {
			newstrlen--;
			String temp = str.substring(0, newstrlen) + "...";
			len = fontMetrics.stringWidth(temp);
		}
		if (newstrlen != oldstrlen) str = str.substring(0, newstrlen) + "...";
		return str;
	}
	
	public static Path2D clock(double x, double y, double size) {
		double space = size / 8.0;
		double centrex = x + size / 2.0 + 0.5;
		double centrey = y + size / 2.0 + 0.5;
		double radius = (size - 2 * space) / 2.0;
		
		Path2D clock = new Path2D.Double();
		
		clock.append(new Ellipse2D.Double(x, y, size, size), false);
		clock.append(new Ellipse2D.Double(centrex - radius, centrey - radius,
				2 * radius, 2 * radius), false);
		
		clock.moveTo(centrex, centrey - radius / 2.0);
		clock.lineTo(centrex, centrey);
		clock.lineTo(centrex + radius / 2.0, centrey);
		
		for (double i = 0; i <= Math.PI / 2; i += Math.PI / 6) {
			double x1 = radius * Math.cos(i);
			double y1 = radius * Math.sin(i);
			double x2 = (radius - space / 2.0) * Math.cos(i);
			double y2 = (radius - space / 2.0) * Math.sin(i);
			
			clock.moveTo(centrex + x1, centrey - y1);
			clock.lineTo(centrex + x2, centrey - y2);
			clock.moveTo(centrex - x1, centrey - y1);
			clock.lineTo(centrex - x2, centrey - y2);
			clock.moveTo(centrex + x1, centrey + y1);
			clock.lineTo(centrex + x2, centrey + y2);
			clock.moveTo(centrex - x1, centrey + y1);
			clock.lineTo(centrex - x2, centrey + y2);
		}
		
		return clock;
	}
	
	public static Path2D notes(double x, double y, double width) {
		Path2D notes = new Path2D.Double();
		
		notes.moveTo(x, y);
		notes.lineTo(x + width, y);
		notes.moveTo(x, y + width / 4.0);
		notes.lineTo(x + width, y + width / 4.0);
		notes.moveTo(x, y + width / 2.0);
		notes.lineTo(x + width / 2.0, y + width / 2.0);
		
		return notes;
	}
	
	@Deprecated
	public static Image clock(int size, Color bgcolor, Color strcolor) {
		int unscaledSize = 4 * size + 32 - (4 * size) % 32;
		int strwidth = unscaledSize / 32;
		
		int space = unscaledSize / 16;
		int centre = unscaledSize / 2;
		int radius = (unscaledSize - 2*space - 2*strwidth) / 2;
		
		Path2D clock = new Path2D.Float();
		
		clock.append(new Ellipse2D.Float(strwidth / 2, strwidth / 2,
				unscaledSize - strwidth, unscaledSize - strwidth), false);
		clock.append(new Ellipse2D.Float(space + strwidth,
				space + strwidth, radius * 2, radius * 2), false);
		
		clock.moveTo(centre, centre);
		clock.lineTo(centre, centre - (radius/2 + strwidth));
		clock.moveTo(centre, centre);
		clock.lineTo(centre + radius/2, centre);
		
		for (double i = 0; i <= Math.PI / 2; i += Math.PI / 6) {
			int x1 = (int) (radius * Math.cos(i));
			int y1 = (int) (radius * Math.sin(i));
			int x2 = (int) ((radius - (space + strwidth)) * Math.cos(i));
			int y2 = (int) ((radius - (space + strwidth)) * Math.sin(i));
			
			clock.moveTo(centre + x1, centre - y1);
			clock.lineTo(centre + x2, centre - y2);
			clock.moveTo(centre - x1, centre - y1);
			clock.lineTo(centre - x2, centre - y2);
			clock.moveTo(centre + x1, centre + y1);
			clock.lineTo(centre + x2, centre + y2);
			clock.moveTo(centre - x1, centre + y1);
			clock.lineTo(centre - x2, centre + y2);
		}
		
		Image unscaledClock = getUnscaledIcon(clock, bgcolor, strcolor,
				unscaledSize, unscaledSize, strwidth);
		
		return unscaledClock.getScaledInstance(size, size,
				Image.SCALE_AREA_AVERAGING);
	}
	
	@Deprecated
	public static Image notes(int width, Color bgcolor, Color strcolor) {
		int unscaledWidth = 4 * width + 32 - (4 * width) % 32;
		int unscaledHeight = unscaledWidth * 9 / 16;
		int halfstrwidth = unscaledHeight * 3 / 32;
		
		Path2D notes = new Path2D.Float();
		
		notes.moveTo(halfstrwidth, halfstrwidth);
		notes.lineTo(unscaledWidth - halfstrwidth, halfstrwidth);
		notes.moveTo(halfstrwidth, unscaledHeight / 2);
		notes.lineTo(unscaledWidth - halfstrwidth, unscaledHeight / 2);
		notes.moveTo(halfstrwidth, unscaledHeight - halfstrwidth);
		notes.lineTo(unscaledWidth / 2, unscaledHeight - halfstrwidth);
		
		Image unscaledNotes = getUnscaledIcon(notes, bgcolor, strcolor,
				unscaledWidth, unscaledHeight, 2 * halfstrwidth);
		
		return unscaledNotes.getScaledInstance(width, width * 9 / 16,
				Image.SCALE_AREA_AVERAGING);
		
	}
	
	public static Image pencil(int size, Color bgcolor, Color strcolor) {
		int unscaledSize = 4 * size + 32 - (4 * size) % 32;
		int halfstrwidth = unscaledSize / 32;
		
		int spacing = unscaledSize / 8;
		
		int topLeftX = unscaledSize * 3 / 4;
		int topLeftY = halfstrwidth;
		int topRightX = unscaledSize - halfstrwidth;
		int topRightY = unscaledSize * 1 / 4;
		
		int bottomRightX = unscaledSize * 1 / 4 + spacing;
		int bottomRightY = unscaledSize - spacing;
		int bottomLeftX = halfstrwidth + spacing;
		int bottomLeftY = unscaledSize - bottomRightX;
		
		Path2D pencil = new Path2D.Float();
		
		pencil.moveTo(bottomRightX, bottomRightY);
		pencil.lineTo(halfstrwidth, unscaledSize - halfstrwidth);
		pencil.lineTo(bottomLeftX, bottomLeftY);
		
		pencil.moveTo(bottomLeftX, bottomLeftY);
		pencil.lineTo(bottomRightX, bottomRightY);
		
		pencil.moveTo(topLeftX - spacing, topLeftY + spacing);
		pencil.lineTo(topRightX - spacing, topRightY + spacing);
		pencil.lineTo(topRightX, topRightY);
		pencil.lineTo(topLeftX, topLeftY);
		pencil.lineTo(bottomLeftX, bottomLeftY);
		
		for (int i = 1; i < 4; i++) {
			pencil.moveTo(bottomLeftX + (bottomRightX - bottomLeftX) * i / 3,
					bottomLeftY + (bottomRightX - bottomLeftX) * i / 3);
			pencil.lineTo(topLeftX - spacing + (topRightX - topLeftX) * i / 3,
					topLeftY + spacing + (topRightY - topLeftY) * i / 3);
		}
		
		Image unscaledPencil = getUnscaledIcon(pencil, bgcolor, strcolor,
				unscaledSize, unscaledSize, 2 * halfstrwidth);
		
		return unscaledPencil.getScaledInstance(size, size,
				Image.SCALE_AREA_AVERAGING);
	}
	
	public static Image search(int size, Color bgcolor, Color strcolor) {
		int unscaledSize = 4 * size + 32 - (4 * size) % 32;
		int halfstrwidth = unscaledSize / 32;
		
		int space = unscaledSize * 3 / 32;
		int radius = unscaledSize * 3 / 8;
		int centre = halfstrwidth + radius;
		
		int mid = (int) (radius / Math.sqrt(2));
		int end = unscaledSize - halfstrwidth;
		
		int x1 = (int) (radius * Math.cos(Math.PI * 7 / 32));
		int y1 = (int) (radius * Math.sin(Math.PI * 7 / 32));
		int x2 = (int) (radius * Math.cos(Math.PI * 9 / 32));
		int y2 = (int) (radius * Math.sin(Math.PI * 9 / 32));
		
		Path2D search = new Path2D.Float();
		
		search.append(new Ellipse2D.Float(halfstrwidth, 
				halfstrwidth, 2*radius, 2*radius), false);
		search.append(new Ellipse2D.Float(halfstrwidth + space, 
				halfstrwidth + space, 2*radius - 2*space,
				2*radius - 2*space), false);
		
		search.moveTo(centre + x1, centre + y1);
		search.lineTo(end, end - 2 * (mid - y1));
		search.lineTo(end - 2 * (mid - x2), end);
		search.lineTo(centre + x2, centre + y2);
		
		Image unscaledSearch = getUnscaledIcon(search, bgcolor, strcolor,
				unscaledSize, unscaledSize, 2 * halfstrwidth);
		
		return unscaledSearch.getScaledInstance(size, size,
				Image.SCALE_AREA_AVERAGING);
	}
	
	@Deprecated
	public static Image plus(int size, Color bgcolor, Color strcolor) {
		int unscaledSize = 4 * size + 32 - (4 * size) % 32;
		int halfstrwidth = unscaledSize / 8;
		
		int centre = unscaledSize / 2;
		
		Path2D plus = new Path2D.Float();
		
		plus.moveTo(centre, halfstrwidth);
		plus.lineTo(centre, unscaledSize - halfstrwidth);
		plus.moveTo(halfstrwidth, centre);
		plus.lineTo(unscaledSize - halfstrwidth, centre);
		
		Image unscaledAdd = getUnscaledIcon(plus, bgcolor, strcolor,
				unscaledSize, unscaledSize, 2 * halfstrwidth);
		
		return unscaledAdd.getScaledInstance(size, size,
				Image.SCALE_AREA_AVERAGING);
	}
	
	public static Path2D plus(double x, double y, double size) {
		Path2D plus = new Path2D.Double();
		
		plus.moveTo(x, y + size / 2.0);
		plus.lineTo(x + size, y + size / 2.0);
		plus.moveTo(x + size / 2.0, y);
		plus.lineTo(x + size / 2.0, y + size);
		
		return plus;
	} //unnecessary?
	
	public static Image leftArrow(int width, Color bgcolor, Color strcolor) {
		int unscaledWidth = 4 * width + 72 - width % 18;
		int unscaledHeight = unscaledWidth * 16 / 9;
		int halfstrwidth = unscaledHeight / 8;
		
		Path2D leftArrow = new Path2D.Float();
		
		leftArrow.moveTo(unscaledWidth - halfstrwidth, halfstrwidth);
		leftArrow.lineTo(halfstrwidth, unscaledHeight / 2);
		leftArrow.lineTo(unscaledWidth - halfstrwidth,
				unscaledHeight - halfstrwidth);
		
		Image unscaledLeftArrow = getUnscaledIcon(leftArrow, bgcolor,
				strcolor, unscaledWidth, unscaledHeight, 2 * halfstrwidth);
		
		return unscaledLeftArrow.getScaledInstance(width, width * 16 / 9,
				Image.SCALE_AREA_AVERAGING);
	}
	
	public static Image rightArrow(int width, Color bgcolor, Color strcolor) {
		int unscaledWidth = 4 * width + 72 - (4 * width) % 72;
		int unscaledHeight = unscaledWidth * 16 / 9;
		int halfstrwidth = unscaledHeight / 8;
		
		Path2D rightArrow = new Path2D.Float();
		
		rightArrow.moveTo(halfstrwidth, halfstrwidth);
		rightArrow.lineTo(unscaledWidth - halfstrwidth, unscaledHeight / 2);
		rightArrow.lineTo(halfstrwidth, unscaledHeight - halfstrwidth);
		
		Image unscaledLeftArrow = getUnscaledIcon(rightArrow, bgcolor,
				strcolor, unscaledWidth, unscaledHeight, 2 * halfstrwidth);
		
		return unscaledLeftArrow.getScaledInstance(width, width * 16 / 9,
				Image.SCALE_AREA_AVERAGING);
	}
	
	public static Image garbage(int width, Color bgcolor, Color strcolor) {
		int unscaledWidth = 4 * width + 96 - (4 * width) % 96;
		int unscaledHeight = unscaledWidth * 4 / 3;
		int halfstrwidth = unscaledHeight / 32;
		
		int right = unscaledWidth - halfstrwidth;
		int bottom = unscaledHeight - halfstrwidth;
		
		Path2D garbage = new Path2D.Float();
		
		garbage.moveTo(halfstrwidth, halfstrwidth);
		garbage.lineTo(right, halfstrwidth);
		garbage.lineTo(right, bottom);
		garbage.lineTo(halfstrwidth, bottom);
		garbage.closePath();
		
		int top = 5 * halfstrwidth;
		
		garbage.moveTo(halfstrwidth, top);
		garbage.lineTo(right, top);
		
		for (int i = 1; i <= 3; i++) {
			int xpos = unscaledWidth * i / 4;
			
			garbage.moveTo(xpos, top + unscaledHeight / 8);
			garbage.lineTo(xpos, bottom - unscaledHeight / 8);
		}
		
		Image unscaledAdd = getUnscaledIcon(garbage, bgcolor, strcolor,
				unscaledWidth, unscaledHeight, 2 * halfstrwidth);
		
		return unscaledAdd.getScaledInstance(width, width * 4 / 3,
				Image.SCALE_AREA_AVERAGING);
	}
	
	public static Image check(int size, Color bgcolor, Color strcolor) {
		int unscaledSize = 4 * size + 32 - (4 * size) % 32;
		int halfstrwidth = unscaledSize / 32;
		
		int centre = unscaledSize / 2;
		int space = unscaledSize / 16;
		int radius = (unscaledSize - 2 * halfstrwidth) / 4;
		
		int x1 = (int) (radius * Math.cos(Math.PI / 12));
		int y1 = (int) (radius * Math.sin(Math.PI / 12));
		int x2 = (int) (radius * Math.cos(Math.PI * 5 / 12));
		int y2 = (int) (radius * Math.sin(Math.PI * 5 / 12));
		
		int slope = -(y2 - y1) / (x2 - x1);
		int lastx = centre + x1;
		int lasty = centre - space / 2 - y1;
		
		Path2D check = new Path2D.Float();
		
		check.append(new Ellipse2D.Float(halfstrwidth, halfstrwidth,
				unscaledSize - 2 * halfstrwidth, unscaledSize
				- 2 * halfstrwidth), false);
		
		check.moveTo(centre - x1, centre - space / 2 + y1);
		check.lineTo(centre - x2, centre - space / 2 + y2);
		check.lineTo(lastx, lasty);
		
		int currentx;
		check.lineTo(currentx = centre + x1 - space, lasty
				+= (lastx - currentx) * -1 / slope);
		lastx = currentx;
		check.lineTo(currentx = centre - x2, lasty
				+= (lastx - currentx) * slope);
		lastx = currentx;
		check.lineTo(currentx = centre - x1 + space, lasty
				+= (lastx - currentx) * -1 / slope);
		check.closePath();
		
		Image unscaledAdd = getUnscaledIcon(check, bgcolor, strcolor,
				unscaledSize, unscaledSize, 2 * halfstrwidth);
		
		return unscaledAdd.getScaledInstance(size, size,
				Image.SCALE_AREA_AVERAGING);
	}
}