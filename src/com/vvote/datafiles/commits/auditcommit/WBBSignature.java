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
package com.vvote.datafiles.commits.auditcommit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.BallotAuditCommitException;
import com.vvote.datafiles.fields.DataFileFields;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation of a single WBB signature
 * 
 * @author James Rumble
 * 
 */
public final class WBBSignature {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(WBBSignature.class);

	/**
	 * flag for whether the signature is valid
	 */
	private final boolean valid;

	/**
	 * serial number of a wbb signature
	 */
	private final String serialNo;

	/**
	 * commit time of a wbb signature
	 */
	private final String commitTime;

	/**
	 * type of message the signature is for
	 */
	private final MessageType type;

	/**
	 * the wbb id of the wbb signature
	 */
	private final String WBBID;

	/**
	 * the wbb signature itself
	 */
	private final String WBBSig;

	/**
	 * Flag to indicate whether the signature was used as a partial signature
	 */
	private final boolean usedAsPartOfThreshold;

	/**
	 * Constructor for a wbb signature
	 * 
	 * @param json
	 * @throws BallotAuditCommitException
	 */
	public WBBSignature(JSONObject json) throws BallotAuditCommitException {

		logger.debug("Creating a new WBBSignature object: {}", json);

		// check that the JSON message is not null
		if (json != null) {

			try {
				// get and set the valid flag
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.IS_VALID)) {
					this.valid = json.getBoolean(DataFileFields.BallotSubmitResponse.WBBSignature.IS_VALID);
					this.usedAsPartOfThreshold = true;
				} else {
					this.valid = true;
					this.usedAsPartOfThreshold = false;
				}

				// get and set the serial number
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.SERIAL_NO)) {
					this.serialNo = json.getString(DataFileFields.BallotSubmitResponse.WBBSignature.SERIAL_NO);
				} else {
					logger.error("The serial number for a WBBSignature must be provided");
					throw new BallotAuditCommitException("The serial number for a WBBSignature must be provided");
				}

				// get and set the commit time
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.COMMIT_TIME)) {
					this.commitTime = json.getString(DataFileFields.BallotSubmitResponse.WBBSignature.COMMIT_TIME);
				} else {
					logger.error("The commit time for a WBBSignature must be provided");
					throw new BallotAuditCommitException("The commit time for a WBBSignature must be provided");
				}

				// get and set the message type
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.TYPE)) {

					this.type = MessageType.getMessageTypeFromType(json.getString(DataFileFields.BallotSubmitResponse.WBBSignature.TYPE));

					if (this.type == null) {
						logger.error("The type given is not a valid type for a WBBSignature");
						throw new BallotAuditCommitException("The type given is not a valid type for a WBBSignature");
					}

					if (this.type != MessageType.BALLOT_GEN_COMMIT) {
						logger.error("The type given is not a valid type for a WBBSignature - must be: {}", MessageType.BALLOT_GEN_COMMIT.toString());
						throw new BallotAuditCommitException("The type given is not a valid type for a WBBSignature - must be: " + MessageType.BALLOT_GEN_COMMIT.toString());
					}

				} else {
					logger.error("The type of message the signature is for must be specified");
					throw new BallotAuditCommitException("The type of message the signature is for must be specified");
				}

				// get and set the WBBID
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.WBB_ID)) {
					this.WBBID = json.getString(DataFileFields.BallotSubmitResponse.WBBSignature.WBB_ID);
				} else {
					logger.error("The WBB ID for a WBBSignature must be provided");
					throw new BallotAuditCommitException("The WBB ID for a WBBSignature must be provided");
				}

				// get and set the wbb signature
				if (json.has(DataFileFields.BallotSubmitResponse.WBBSignature.WBB_SIG)) {
					this.WBBSig = json.getString(DataFileFields.BallotSubmitResponse.WBBSignature.WBB_SIG);
				} else {
					logger.error("The wbb signature for a WBBSignature must be provided");
					throw new BallotAuditCommitException("The wbb signature for a WBBSignature must be provided");
				}

			} catch (JSONException e) {
				logger.error("Unable to create a WBBSignature. Error: {}", e);
				throw new BallotAuditCommitException("Unable to create a WBBSignature.", e);
			}
		} else {
			logger.error("A WBBSignature object must be a valid JSON message");
			throw new BallotAuditCommitException("A WBBSignature object must be a valid JSON message");
		}
	}

	/**
	 * Getter for the commit time
	 * 
	 * @return commitTime
	 */
	public final String getCommitTime() {
		return this.commitTime;
	}

	/**
	 * Getter for the serial number
	 * 
	 * @return serialNo
	 */
	public final String getSerialNo() {
		return this.serialNo;
	}

	/**
	 * Getter for the type of message the wbb signature is for
	 * 
	 * @return type
	 */
	public final MessageType getType() {
		return this.type;
	}

	/**
	 * Getter for whether the signature is valid (may not be used as part of the
	 * threshold and will then be omitted from the json)
	 * 
	 * @return valid
	 */
	public final boolean getValid() {
		return this.valid;
	}

	/**
	 * Getter for the wbb id
	 * 
	 * @return WBBID
	 */
	public final String getWBBID() {
		return this.WBBID;
	}

	/**
	 * Getter for the wbb signature
	 * 
	 * @return WBBSig
	 */
	public final String getWBBSig() {
		return this.WBBSig;
	}

	/**
	 * Getter for whether the current wbb signature was used as part of the
	 * threshold check
	 * 
	 * @return usedAsPartOfThreshold
	 */
	public boolean isUsedAsPartOfThreshold() {
		return this.usedAsPartOfThreshold;
	}

	@Override
	public String toString() {
		return "WBBSignature [valid=" + this.valid + ", serialNo=" + this.serialNo + ", commitTime=" + this.commitTime + ", type=" + this.type + ", WBBID=" + this.WBBID + ", WBBSig=" + this.WBBSig
				+ ", usedAsPartOfThreshold=" + this.usedAsPartOfThreshold + "]";
	}
}
