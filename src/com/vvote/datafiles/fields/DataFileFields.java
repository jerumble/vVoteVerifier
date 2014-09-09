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
package com.vvote.datafiles.fields;

/**
 * Provides constants for the different data files
 * 
 * @author James Rumble
 * 
 */
public class DataFileFields {

	/**
	 * Identifiers for the candidate table files
	 */
	public static class CandidateTable {

		/**
		 * Identifier for the race id
		 */
		public static final String RACE_ID = "RaceId";

		/**
		 * Identifier for the race name
		 */
		public static final String RACE_NAME = "RaceName";

		/**
		 * Identifier for the race type
		 */
		public static final String RACE_TYPE = "RaceType";

		/**
		 * Identifier for the candidate ids
		 */
		public static final String CANDIDATE_IDS = "CandidateIds";

		/**
		 * Identifier for the name of the district
		 */
		public static final String DISTRICT_NAME = "DistrictName";
	}

	/**
	 * Identifiers for the file submission for the ballot gen commit message
	 */
	public static class BallotGenCiphers {

		/**
		 * Serial number of the device committing to the ciohers
		 */
		public static final String SERIAL_NO = "serialNo";
		/**
		 * Permutation made from the random shuffling of the candidate ids
		 */
		public static final String PERMUTATION = "permutation";
		/**
		 * The actual ciphers
		 */
		public static final String CIPHERS = "ciphers";
	}

	/**
	 * Identifiers for the ballot submit response file
	 */
	public static class BallotSubmitResponse {

		/**
		 * Identifiers for the list of embedded WBB signatures
		 */
		public static class WBBSignature {

			/**
			 * Identifier for whether the signature is valid
			 */
			public static final String IS_VALID = "valid";

			/**
			 * Identifier for the serial number of a wbb signature
			 */
			public static final String SERIAL_NO = "serialNo";

			/**
			 * Identifier for the commit time of a wbb signature
			 */
			public static final String COMMIT_TIME = "commitTime";

			/**
			 * Identifier for the type of message the signature is for
			 */
			public static final String TYPE = "type";

			/**
			 * Identifier for the wbb id of a wbb signature
			 */
			public static final String WBB_ID = "WBBID";

			/**
			 * Identifier for the wbb signature itself of a wbb signature
			 */
			public static final String WBB_SIG = "WBBSig";
		}

		/**
		 * Identifier for the submission id of the ballot submit response
		 * message
		 */
		public static final String SUBMISSION_ID = "submissionID";

		/**
		 * Identifier for the location of the ballots file
		 */
		public static final String BALLOT_FILE = "ballotFile";

		/**
		 * Identifier for the peer id
		 */
		public static final String PEER_ID = "peerID";

		/**
		 * Identifier for the fiat shamir signature
		 */
		public static final String FIAT_SHAMIR = "fiatShamir";

		/**
		 * Identifier for the wbb signatures array
		 */
		public static final String WBB_SIGNATURE = "WBBSig";
	}

	/**
	 * Identifiers for the district config file
	 */
	public static class DistrictConfig {

		/**
		 * LA Race identifier
		 */
		public static final String LA = "la";
		/**
		 * LC ATL Race identifier
		 */
		public static final String LC_ATL = "lc_atl";
		/**
		 * LC BTL Race identifier
		 */
		public static final String LC_BTL = "lc_btl";
	}
	
	/**
	 * Identifiers for the race map file
	 */
	public static class RaceMap {

		/**
		 * unique identifier
		 */
		public static final String ID = "Id";
		/**
		 * name of the race
		 */
		public static final String RACE_NAME = "Name";
		/**
		 * race identifier
		 */
		public static final String RACE_ID = "RaceId";
		/**
		 * race District
		 */
		public static final String DISTRICT = "District";
	}
}
