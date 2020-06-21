package com.github.javawow.tools.packet;

import io.netty.buffer.ByteBuf;

public class ByteBufWoWPacket implements WoWPacket<ByteBuf> {
	private int opcode;
	private ByteBuf payload;

	public ByteBufWoWPacket(int opcode, ByteBuf payload) {
		this.opcode = opcode;
		this.payload = payload;
	}

	@Override
	public int getOpCode() {
		return opcode;
	}

	@Override
	public ByteBuf getPayload() {
		return payload;
	}
}