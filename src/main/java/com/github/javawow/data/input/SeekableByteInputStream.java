package com.github.javawow.data.input;

/**
 * @author Jon
 *
 */
interface SeekableByteInputStream extends ByteInputStream {
	void seek(int offset);
	int getPosition();
}