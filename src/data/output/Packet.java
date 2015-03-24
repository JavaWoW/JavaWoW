package data.output;

public class Packet extends LittleEndianWriterStream {
	private byte opCode;

	public Packet(byte opCode) {
		this.opCode = opCode;
	}

	public final byte getOpCode() {
		return opCode;
	}

	public final short getPacketLength() {
		return (short) super.size();
	}
}