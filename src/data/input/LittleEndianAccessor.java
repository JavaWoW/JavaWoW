package data.input;

import java.math.BigInteger;

/**
 * @author Jon
 *
 */
public interface LittleEndianAccessor {
	byte readByte();
	short readUnsignedByte();
	char readChar();
	short readShort();
	int readUnsignedShort();
	int readInt();
	long readUnsignedInt();
	long readLong();
	BigInteger readUnsignedLong();
	void skip(int num);
	byte[] read(int num);
	float readFloat();
	double readDouble();
	String readAsciiString(int n);
	String readLEAsciiString(int n);
	String readNullTerminatedAsciiString();
	String readLENullTerminatedAsciiString();
	int getBytesRead();
	int available();
}