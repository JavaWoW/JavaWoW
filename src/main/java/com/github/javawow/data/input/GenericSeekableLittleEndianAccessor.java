/*
 * Java World of Warcraft Emulation Project
 * Copyright (C) 2015-2020 JavaWoW
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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