package com.github.javawow.data.input;

/**
 * @author Jon
 *
 */
public final class GenericSeekableLittleEndianAccessor extends GenericLittleEndianAccessor implements SeekableLittleEndianAccessor {
	private SeekableByteInputStream sbis;

	/**
	 * @param sbis
	 */
	public GenericSeekableLittleEndianAccessor(SeekableByteInputStream sbis) {
		super(sbis);
		this.sbis = sbis;
	}

	/**
	 * @see com.github.javawow.data.input.SeekableLittleEndianAccessor#seek(int)
	 */
	@Override
	public final void seek(int offset) {
		sbis.seek(offset);
	}

	/**
	 * @see com.github.javawow.data.input.SeekableLittleEndianAccessor#seek(int)
	 */
	@Override
	public final void skip(int num) {
		this.seek(getPosition() + num);
	}

	/**
	 * @see com.github.javawow.data.input.SeekableLittleEndianAccessor#getPosition()
	 */
	@Override
	public final int getPosition() {
		return sbis.getPosition();
	}
}