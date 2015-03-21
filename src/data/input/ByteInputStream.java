package data.input;

/**
 * @author Jon
 *
 */
interface ByteInputStream {
	int readByte();
	int getBytesRead();
	int available();
}