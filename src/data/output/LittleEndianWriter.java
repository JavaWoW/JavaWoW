package data.output;


/**
 * @author Jon
 *
 */
public interface LittleEndianWriter {
	void writeZeroBytes(int i);
	void write(byte b[]);
	void write(byte b);
	void write(int b);
	void writeShort(int s);
	void writeInt(int i);
	void writeInt(long i);
	void writeLong(long l);
	void writeFloat(float f);
	void writeBEFloat(float f);
	void writeDouble(double d);
	void writeBEDouble(double d);
	void writeAsciiString(String s);
	void writeAsciiString(String s, int max);
	void writeNullTerminatedAsciiString(String s);
}