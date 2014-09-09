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
 * Provides configuration file constants
 * 
 * @author James Rumble
 * 
 */
public class ConfigFileConstants {

	/**
	 * Vote packing configuration constants.
	 */
	public static class VotePackingConfig {

		/**
		 * Identifier for the curve used for vote packing
		 */
		public static final String CURVE = "curve";

		/**
		 * Identifier for the padding file
		 */
		public static final String PADDING_FILE = "padding.file";

		/**
		 * Identifier for the line length of the la table
		 */
		public static final String TABLE_LA_LINE_LENGTH = "table.la.linelength";

		/**
		 * Identifier for the size of the packing for the la race
		 */
		public static final String TABLE_LA_PACKING = "table.la.packing";

		/**
		 * Identifier for the line length of the lc btl table
		 */
		public static final String TABLE_BTL_LINE_LENGTH = "table.btl.linelength";

		/**
		 * Identifier for the size of the packing for the la race
		 */
		public static final String TABLE_BTL_PACKING = "table.btl.packing";

		/**
		 * Identifier for the candidate tables directory
		 */
		public static final String CANDIDATE_TABLES = "candidate.tables";

		/**
		 * Identifier for the flag use direct which may specify whether lc atl
		 * is packed or not
		 */
		public static final String USE_DIRECT = "use.direct";
	}

	/**
	 * Ballot generation configuration constants.
	 */
	public static class BallotGenerationConfig {
		/**
		 * Identifier for the races array
		 */
		public static final String RACES = "races";

		/**
		 * Identifier for the race id
		 */
		public static final String ID = "id";

		/**
		 * Identifier for which race the JSONObject belongs
		 */
		public static final String LA = "la";

		/**
		 * Identifier for which race the JSONObject belongs
		 */
		public static final String LC_ATL = "lc_atl";

		/**
		 * Identifier for which race the JSONObject belongs
		 */
		public static final String LC_BTL = "lc_btl";

		/**
		 * Identifier for how many candidates are present in each race
		 */
		public static final String CANDIDATES = "candidates";

		/**
		 * Identifier for the number of ballots to be audited
		 */
		public static final String BALLOTS_TO_AUDIT = "ballotsToAudit";

		/**
		 * Identifier for the number of ballots to be generated
		 */
		public static final String BALLOTs_TO_GENERATE = "ballotToGenerate";

		/**
		 * Identifier for the ballot output folder
		 */
		public static final String BALLOT_OUTPUT_FOLDER = "BallotOutputFolder";

		/**
		 * Identifier for the file name of where the ballots are stored
		 */
		public static final String BALLOT_LIST = "ballotList";

		/**
		 * Identifier for the ballots database
		 */
		public static final String BALLOT_DB = "ballotDB";
	}

}
