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
package com.vvote.verifier.fields;

/**
 * Provides constants for all the different message fields
 * 
 * @author James Rumble
 * 
 */
public class VerifierFields {

	/**
	 * fields for a
	 * <code>com.vvote.verifier.ballotGen.BallotGenerationVerifierSpec</code>
	 */
	public static class BallotGenerationVerifierSpec extends ComponentSpec {

		/**
		 * Identifier for the name of the commit data file
		 */
		public static final String COMMIT_DATA_FILE = "commitData";

		/**
		 * Identifier for the name of the audit data file
		 */
		public static final String AUDIT_DATA_FILE = "auditData";

		/**
		 * Identifier for the name of the ballot submit response
		 */
		public static final String BALLOT_SUBMIT_RESPONSE = "ballotSubmitResponse";
	}
	
	/**
	 * fields for a
	 * <code>com.vvote.verifier.votePacking.VotePackingVerifierSpec</code>
	 */
	public static class VotePackingVerifierSpec extends ComponentSpec {

		/**
		 * Identifier for the location of the vote packing config
		 */
		public static final String VOTE_PACKING_CONFIG = "mapProperties";
		
		/**
		 * Identifier for the location of the vote packing mix output folder
		 */
		public static final String MIX_OUTPUT = "mixOutput";
		
		/**
		 * Identifier for the location of the vote packing mix input folder
		 */
		public static final String MIX_INPUT = "mixInput";
		
		/**
		 * The race map
		 */
		public static final String RACE_MAP = "raceMap";
	}
	
	/**
	 * fields for a <code>com.vvote.verifier.Spec</code>
	 */
	public static class Spec {
		/**
		 * Identifier for the location of the final commits folder
		 */
		public static final String FINAL_COMMITS_FOLDER = "finalCommits";
		/**
		 * Identifier for the location of the extra commits folder
		 */
		public static final String EXTRA_COMMITS_FOLDER = "extraCommits";
	}

	/**
	 * fields for a <code>com.vvote.verifier.ComponentSpec</code>
	 */
	public static class ComponentSpec extends Spec{

		/**
		 * Identifier for the location of the base_encrypted_candidate_ids
		 */
		public static final String BASE_ENCRYPTED_CANDIDATE_IDS = "baseEncryptedCandidateIds";

		/**
		 * Identifier for the location of the plaintext_candidate_ids
		 */
		public static final String PLAINTEXT_CANDIDATE_IDS = "plaintextCandidateIds";

		/**
		 * Identifier for the location of the Public key
		 */
		public static final String PUBLIC_KEY = "publicKey";

		/**
		 * Identifier for the location of the district config
		 */
		public static final String DISTRICT_CONFIG = "districtConfig";
		
		/**
		 * Identifier for the location of the ballot generation config
		 */
		public static final String BALLOT_GEN_CONFIG = "ballotGenConf";

		/**
		 * Identifier for the certs file
		 */
		public static final String CERTS = "certsFile";

		/**
		 * Identifier for the name of the ciphers data file
		 */
		public static final String CIPHERS_DATA_FILE = "ciphersData";
	}

	/**
	 * fields for a <code>com.vvote.verifier.VVoteVerifier</code>
	 */
	public static class VVoteVerifier {
		/**
		 * The location of the main spec file
		 */
		public static final String SPEC_FILE = "./spec_files/verifierSpecFile.json";
	}

	/**
	 * fields for a <code>com.vvote.verifier.VVoteVerifierSpec</code>
	 */
	public static class VVoteVerifierSpec {
		/**
		 * Identifier for the JSONarray
		 */
		public static final String VERIFIER_DETAILS = "verifierDetails";

		/**
		 * Identifier for the actual verifier class
		 */
		public static final String VERIFIER_CLASS = "verifierClass";

		/**
		 * Identifier for the name of the verifier
		 */
		public static final String VERIFIER_NAME = "verifierName";

		/**
		 * Identifier for the spec file for the verifier
		 */
		public static final String VERIFIER_SPEC_FILE = "specFile";
	}
}
