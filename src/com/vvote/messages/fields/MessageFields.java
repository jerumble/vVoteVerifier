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
package com.vvote.messages.fields;

/**
 * Provides constants for all the different message fields
 * 
 * @author James Rumble
 * 
 */
public class MessageFields {

	/**
	 * Message fields for a <code>com.vvote.messages.FileMessage</code>
	 */
	public static class FileMessage extends TypedJSONMessage {

		/**
		 * Identifier for the size of the file attached to the message
		 */
		public static final String FILE_SIZE = "fileSize";

		/**
		 * Identifier for the submission id of the message
		 */
		public static final String SUBMISSION_ID = "submissionID";

		/**
		 * Identifier for the _digest of the message
		 */
		public static final String _DIGEST = "_digest";

		/**
		 * Identifier for the digest of the message
		 */
		public static final String DIGEST = "digest";

		/**
		 * Identifier for the name of the file attachment
		 */
		public static final String FILE_NAME = "_fileName";
	}

	/**
	 * Message fields for a <code>com.vvote.messages.JSONMessage</code>
	 */
	public static class JSONMessage {
		/**
		 * Commit message
		 */
		public static final String COMMIT_TIME = "commitTime";
	}

	/**
	 * Message fields for a
	 * <code>com.vvote.messages.MixRandomCommitMessage</code>
	 */
	public static class MixRandomCommitMessage extends FileMessage {
		/**
		 * Identifier for the printer id
		 */
		public static final String PRINTER_ID = "printerID";
	}

	/**
	 * Message fields for a <code>com.vvote.messages.PODMessage</code>
	 */
	public static class PODMessage extends VoteDataMessage {

		/**
		 * Identifier for the ballot reductions
		 */
		public static final String BALLOT_REDUCTIONS = "ballotReductions";

		/**
		 * Identifier for the index
		 */
		public static final String BALLOT_REDUCTIONS_INDEX = "index";

		/**
		 * Identifier for the candidate index
		 */
		public static final String BALLOT_REDUCTIONS_CANDIDATE_INDEX = "candidateIndex";

		/**
		 * Identifier for the randomness value
		 */
		public static final String BALLOT_REDUCTIONS_RANDOMNESS = "randomness";
	}

	/**
	 * Message fields for a <code>com.vvote.messages.SignatureMessage</code>
	 */
	public static class SignatureMessage extends JSONMessage {
		/**
		 * Identifier for the joint signature
		 */
		public static final String JOINT_SIG = "jointSig";

		/**
		 * Identifier for the json file
		 */
		public static final String JSON_FILE = "jsonFile";

		/**
		 * Identifier for the attachment file
		 */
		public static final String ATTACHMENT_FILE = "attachmentFile";
	}

	/**
	 * Message fields for a <code>com.vvote.messages.TypedJSONMessage</code>
	 */
	public static class TypedJSONMessage extends JSONMessage {
		/**
		 * Identifier for the booth id of the device
		 */
		public static final String BOOTH_ID = "boothID";

		/**
		 * Identifier for the signature for the device
		 */
		public static final String BOOTH_SIG = "boothSig";
	}

	/**
	 * Message fields for a
	 * <code>com.vvote.messages.typed.vote.VoteDataMessage</code>
	 */
	public static class VoteDataMessage extends TypedJSONMessage {

		/**
		 * Identifier for the serial number of a device
		 */
		public static final String SERIAL_NO = "serialNo";

		/**
		 * Identifier for the district of the device
		 */
		public static final String DISTRICT = "district";
	}

	/**
	 * Message fields for a
	 * <code>com.vvote.messages.typed.vote.CancelMessage</code>
	 *
	 */
	public static class CancelMessage extends VoteDataMessage {

		/**
		 * Identifier for a cancel authority signature
		 */
		public static final String CANCEL_AUTH_SIG = "cancelAuthSig";

		/**
		 * Identifier for a cancel authority identifier
		 */
		public static final String CANCEL_AUTH_ID = "cancelAuthID";

		/**
		 * Identifier for a serial signature identifier
		 */
		public static final String SERIAL_SIG = "serialSig";
	}

	/**
	 * Message fields for a
	 * <code>com.vvote.messages.typed.vote.AuditMessage</code>
	 *
	 */
	public static class AuditMessage extends VoteDataMessage {

		/**
		 * Identifier for a serial signature identifier
		 */
		public static final String SERIAL_SIG = "serialSig";

		/**
		 * Identifier for a commit witness
		 */
		public static final String COMMIT_WITNESS = "commitWitness";

		/**
		 * Identifier for a permutation
		 */
		public static final String PERMUTATION = "permutation";

		/**
		 * Identifier for a reduced permutation
		 */
		public static final String REDUCED_PERMUTATION = "_reducedPerms";
	}

	/**
	 * Message fields for a
	 * <code>com.vvote.messages.typed.vote.VoteMessage</code>
	 */
	public static class VoteMessage extends VoteDataMessage {

		/**
		 * Identifier for the startEVMSig signature
		 */
		public static final String START_EVM_SIG = "startEVMSig";

		/**
		 * Identifier for the races array
		 */
		public static final String RACES = "races";

		/**
		 * Identifier for the serialSig signature
		 */
		public static final String SERIAL_SIG = "serialSig";

		/**
		 * Identifier for the _vPrefs
		 */
		public static final String _vPREFS = "_vPrefs";

		/**
		 * Identifier for the id of a specific race preference
		 */
		public static final String RACE_ID = "id";

		/**
		 * Identifier for the preferences of a specific race preference
		 */
		public static final String PREFERENCES = "preferences";

		/**
		 * String which represents whether a specific ranking has been left
		 * blank and therefore not used
		 */
		public static final String PREFERENCE_IS_BLANK = " ";
	}

	/**
	 * Identifies a separation between races
	 */
	public static final String RACE_SEPARATOR = ":";

	/**
	 * Identifies a separation between preferences
	 */
	public static final String PREFERENCE_SEPARATOR = ",";

	/**
	 * Identifies a separation between serial numbers
	 */
	public static final String SERIAL_NO_SEPARATOR = ":";

	/**
	 * Identifier for the type of the message currently being processed
	 */
	public static final String TYPE = "type";
}
