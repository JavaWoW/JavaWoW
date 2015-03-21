package data.input;

import java.awt.Point;

import data.output.LittleEndianWriter;

/**
 * This class provides an abstraction layer to a coordinate in a little-endian
 * stream of bytes.
 * 
 * @author Frz
 * @since Revision 299
 * @version 1.0
 */
public class StreamUtil {
	/**
	 * Read a 2-D coordinate of short integers (x, y).
	 * 
	 * @param lea The accessor to read the point from.
	 * @return A <code>point</code> object read from the accessor.
	 */
	public static Point readShortPoint(LittleEndianAccessor lea) {
		int x = lea.readShort();
		int y = lea.readShort();
		return new Point(x, y);
	}

	/**
	 * Writes a 2-D coordinate of short integers (x, y).
	 * 
	 * @param lew The stream-writer to write the point to.
	 * @param p The point to write to the stream-writer.
	 */
	public static void writeShortPoint(LittleEndianWriter lew, Point p) {
		lew.writeShort(p.x);
		lew.writeShort(p.y);
	}
}