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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.BallotAuditCommitException;
import com.vvote.datafiles.fields.DataFileFields;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a wrapper for the ballot submission response file
 * 
 * @author James Rumble
 * 
 */
public final class BallotSubmitResponse {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotSubmitResponse.class);

	/**
	 * submission id of the ballot submit response message
	 */
	private final String submissionID;

	/**
	 * location of the ballots file
	 */
	private final String ballotFile;

	/**
	 * peer id of the ballot submit response message (printer id)
	 */
	private final String peerID;

	/**
	 * fiat shamir signature which is used to check whether the correct
	 * proportion of ballots have been chosen for auditing
	 */
	private final String fiatShamir;

	/**
	 * List of wbb signatures
	 */
	private final List<WBBSignature> wbbSignatures;

	/**
	 * Constructor for a ballot submission response
	 * 
	 * @param json
	 * @throws BallotAuditCommitException
	 */
	public BallotSubmitResponse(JSONObject json) throws BallotAuditCommitException {

		logger.debug("Creating a new BallotSubmitResponse object: {}", json);

		// check that the JSON message is not null
		if (json != null) {

			try {
				// get and set the submission id
				if (json.has(DataFileFields.BallotSubmitResponse.SUBMISSION_ID)) {
					this.submissionID = json.getString(DataFileFields.BallotSubmitResponse.SUBMISSION_ID);
				} else {
					logger.error("The submission id for a BallotSubmitResponse must be provided");
					throw new BallotAuditCommitException("The submission id for a BallotSubmitResponse must be provided");
				}

				// get and set the ballot file location
				if (json.has(DataFileFields.BallotSubmitResponse.BALLOT_FILE)) {
					this.ballotFile = json.getString(DataFileFields.BallotSubmitResponse.BALLOT_FILE);
				} else {
					logger.error("The location of the ballot file for a BallotSubmitResponse must be provided");
					throw new BallotAuditCommitException("The location of the ballot file for a BallotSubmitResponse must be provided");
				}

				// get and set the peer id
				if (json.has(DataFileFields.BallotSubmitResponse.PEER_ID)) {
					this.peerID = json.getString(DataFileFields.BallotSubmitResponse.PEER_ID);
				} else {
					logger.error("The peer id for a BallotSubmitResponse must be provided");
					throw new BallotAuditCommitException("The peer id for a BallotSubmitResponse must be provided");
				}

				// get and set the fiat shamir signature
				if (json.has(DataFileFields.BallotSubmitResponse.FIAT_SHAMIR)) {
					this.fiatShamir = json.getString(DataFileFields.BallotSubmitResponse.FIAT_SHAMIR);
				} else {
					logger.error("The fiat shamir signature for a BallotSubmitResponse must be provided");
					throw new BallotAuditCommitException("The fiat shamir signature for a BallotSubmitResponse must be provided");
				}

				// get and set the wbb signatures
				if (json.has(DataFileFields.BallotSubmitResponse.WBB_SIGNATURE)) {

					this.wbbSignatures = new ArrayList<WBBSignature>();

					JSONArray WBBSig = json.getJSONArray(DataFileFields.BallotSubmitResponse.WBB_SIGNATURE);

					for (int i = 0; i < WBBSig.length(); i++) {
						this.wbbSignatures.add(new WBBSignature(WBBSig.getJSONObject(i)));
					}

				} else {
					logger.error("A BallotSubmitResponse must contain a list of wbb signatures");
					throw new BallotAuditCommitException("A BallotSubmitResponse must contain a list of wbb signatures");
				}
			} catch (JSONException e) {
				logger.error("Unable to create a BallotSubmitResponse. Error: {}", e);
				throw new BallotAuditCommitException("Unable to create a BallotSubmitResponse.", e);
			}
		} else {
			logger.error("A BallotSubmitResponse object must be a valid JSON message");
			throw new BallotAuditCommitException("A BallotSubmitResponse object must be a valid JSON message");
		}
	}

	/**
	 * Getter for the location of the ballot file
	 * 
	 * @return ballotFile
	 */
	public final String getBallotFile() {
		return this.ballotFile;
	}

	/**
	 * Getter for the fiat shamir signature
	 * 
	 * @return fiatShamir
	 */
	public final String getFiatShamir() {
		return this.fiatShamir;
	}

	/**
	 * Getter for the id of the peer
	 * 
	 * @return peerID
	 */
	public final String getPeerID() {
		return this.peerID;
	}

	/**
	 * Getter for the submission id
	 * 
	 * @return submissionID
	 */
	public final String getSubmissionID() {
		return this.submissionID;
	}

	/**
	 * Getter for the list of wbb signatures
	 * 
	 * @return wbbSignatures
	 */
	public final List<WBBSignature> getWbbSignatures() {
		return Collections.unmodifiableList(this.wbbSignatures);
	}

	@Override
	public String toString() {
		return "BallotSubmitResponse [submissionID=" + this.submissionID + ", ballotFile=" + this.ballotFile + ", peerID=" + this.peerID + ", fiatShamir=" + this.fiatShamir + ", wbbSignatures="
				+ this.wbbSignatures + "]";
	}
}
