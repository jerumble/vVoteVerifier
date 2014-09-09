/**
 * This file is part of vVoteVerifier which is designed to be used as a verifiation tool for the vVote Election System.
 * Copyright (C) 2014  James Rumble (jerumble@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vvote;

/**
 * Constants for the certs.bks file
 * 
 * @author James Rumble
 * 
 */
public class CertsConstants {

	/**
	 * Public key entry constants
	 */
	public static class PublicKeyEntry {
		/**
		 * Identifier for the public key
		 */
		public static final String PUBLIC_KEY = "publicKey";

		/**
		 * Identifier for the constant g
		 */
		public static final String G = "g";
	}

	/**
	 * Public key entry constants
	 */
	public static class PeerPublicKeyEntry extends PublicKeyEntry {
		/**
		 * Identifier for the partial public key
		 */
		public static final String PARTIAL_PUBLIC_KEY = "partialPublicKey";

		/**
		 * Identifier for the sequence number
		 */
		public static final String SEQUENCE_NUMBER = "sequenceNo";
	}

	/**
	 * SK2 Signing entry
	 */
	public static final String SIGNING_SK2 = "_SigningSK2";

	/**
	 * Path to the jks file
	 */
	public static final String JKS_PATH = "jksPath";

	/**
	 * WBB Cert
	 */
	public static final String WBB_CERT = "WBB";

	/**
	 * identifier for a public key entry
	 */
	public static final String PUBLIC_KEY_ENTRY = "pubKeyEntry";
}
