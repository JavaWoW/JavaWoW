package data.input;

/**
 * @author Jon
 *
 */
public final class GenericSeekableLittleEndianAccessor extends GenericLittleEndianAccessor implements SeekableLittleEndianAccessor {
	private SeekableByteInputStream sbis;

	/**
	 * @param bis
	 */
	public GenericSeekableLittleEndianAccessor(SeekableByteInputStream sbis) {
		super(sbis);
		this.sbis = sbis;
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.SeekableLittleEndianAccessor#seek(int)
	 */
	@Override
	public final void seek(int offset) {
		sbis.seek(offset);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.GenericLittleEndianAccessor#seek(int)
	 */
	@Override
	public final void skip(int num) {
		this.seek(getPosition() + num);
	}

	/**
	 * @see net.project54.maplestory.tools.data.input.SeekableLittleEndianAccessor#getPosition()
	 */
	@Override
	public final int getPosition() {
		return sbis.getPosition();
	}
}