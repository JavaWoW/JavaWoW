package com.github.javawow.data.input;

/**
 * @author Jon
 *
 */
public interface SeekableLittleEndianAccessor extends LittleEndianAccessor {
	void seek(int offset);
	int getPosition();
}