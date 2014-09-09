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
 * Provides cryptographic constants
 * 
 * @author James Rumble
 * 
 */
public class CryptoConstants {

	/**
	 * Ballot Generation verification crypto constants
	 */
	public static class BallotGenerationVerifier {
		/**
		 * Holds name of the EC curve used to setup the EC curved used by
		 * ECUtils
		 */
		public static final String CURVE_NAME = "P-256";

	}

	/**
	 * Commitment constants
	 */
	public static class Commitments {

		/**
		 * The hash algorithm used for checking commitments
		 */
		public static final String COMMITMENT_HASH_ALGORITHM = "SHA-256";

		/**
		 * maximum length for the random value
		 */
		public static final int RANDOM_VALUE_MAXIMUM_LENGTH = 32;
	}

	/**
	 * Elliptic curve crypto constants
	 */
	public static class EC {

		/**
		 * Defines the name of the curve to use for Elliptic curve operations
		 */
		public static final String CURVE_NAME = "P-256";

		/**
		 * Identifier for x coordinate of an EC point
		 */
		public static final String X = "x";

		/**
		 * Identifier for y coordinate of an EC point
		 */
		public static final String Y = "y";

		/**
		 * Identifier for g^r (alpha) of an EC cipher
		 */
		public static final String GR = "gr";

		/**
		 * Identifier for my^r (beta) of an EC cipher
		 */
		public static final String MYR = "myr";
	}

	/**
	 * Commitment constants
	 */
	public static class FiatShamirSignature {

		/**
		 * The hash algorithm used for checking commitments
		 */
		public static final String FIAT_SHAMIR_HASH_ALGORITHM = "SHA256";

		/**
		 * The crypto provider to use for calculating the fiat shamir signature
		 */
		public static final String FIAT_SHAMIR_PROVIDER = "BC";
	}

	/**
	 * Crypto open ssl library for verifying cryptographic operations using
	 * openssl
	 */
	public static final String CRYPTO_OPENSSL_LIBRARY = "CryptoOpenSSL";
}
