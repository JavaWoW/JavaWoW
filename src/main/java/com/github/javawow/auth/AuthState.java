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

package com.github.javawow.auth;

import io.netty.util.AttributeKey;

public enum AuthState {
	/**
	 * Nothing has been received from the client, we do not know who the client is.
	 */
	UNAUTHENTICATED,
	/**
	 * The client has sent us SRP-6a (I) which is their identity, however we have
	 * not authenticated their identity.
	 */
	IDENTIFIED,
	/**
	 * The client has sent us SRP-6a (A and M1) which we have confirmed is correct,
	 * therefore the client is authenticated.
	 */
	AUTHENTICATED;

	public static final AttributeKey<AuthState> ATTRIBUTE_KEY = AttributeKey
			.newInstance(AuthState.class.getSimpleName());
}