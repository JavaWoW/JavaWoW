package data.output;

import java.awt.Point;

/**
 * @author Jon
 *
 */
public interface LittleEndianWriter {
	void writeZeroBytes(int i);
	void write(byte b[]);
	void write(byte b);
	void write(int b);
	void writeInt(int i);
	void writeInt(long i);
	void writeShort(int s);
	void writeLong(long l);
	void writeFloat(float f);
	void writeAsciiString(String s);
	void writeAsciiString(String s, int max);
	void writeNullTerminatedAsciiString(String s);
	void writePos(Point s);
	void writeMapleAsciiString(String s);
}