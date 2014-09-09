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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.BallotAuditCommitException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Represents the opened randomness values commitments received from each mix
 * server and used by the PoD printers for encryption of the candidate ids
 * 
 * Represents each JSONObject within the JSONArray per line of the
 * ballotsGenAudit file
 * 
 * @author James Rumble
 * 
 */
public final class OpenedRandomnessCommitments {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(OpenedRandomnessCommitments.class);

	/**
	 * Identifier for the serial number
	 */
	private static final String SERIAL_NO = "serialNo";

	/**
	 * Identifier for the peer id
	 */
	private static final String PEER_ID = "peerID";

	/**
	 * Identifier for the randomness array
	 */
	private static final String RANDOMNESS = "randomness";

	/**
	 * Identifier for the random witness value
	 */
	private static final String R_COMM = "rComm";

	/**
	 * Identifier for the random data value
	 */
	private static final String R = "r";

	/**
	 * Serial number for an set of opened Randomness Commitments - identifies
	 * the recipient of the randomness values - the PoD Printer which uses the
	 * randomness values for encryption
	 */
	private final String serialNo;

	/**
	 * List of randomness pairs
	 */
	private final List<RandomnessPair> randomnessPairs;

	/**
	 * Peer id for a set of opened randomness commitments - identifies the
	 * source of the randomness values
	 */
	private final String peerId;

	/**
	 * Constructor for group of opened randomness commitments
	 * 
	 * @param json
	 * @throws BallotAuditCommitException
	 */
	public OpenedRandomnessCommitments(JSONObject json) throws BallotAuditCommitException {

		logger.debug("Creating a new OpenedRandomnessCommitments object: {}", json);

		try {
			this.serialNo = json.getString(SERIAL_NO);

			this.peerId = json.getString(PEER_ID);

			JSONArray randomnessArray = json.getJSONArray(RANDOMNESS);

			String rComm = null;
			String r = null;

			JSONObject pair = null;

			this.randomnessPairs = new ArrayList<RandomnessPair>();

			// create a new RandomnessPair object for each JSONObject
			// representing a
			// randomness pair
			for (int i = 0; i < randomnessArray.length(); i++) {
				pair = randomnessArray.getJSONObject(i);

				rComm = pair.getString(R_COMM);
				r = pair.getString(R);

				this.randomnessPairs.add(new RandomnessPair(rComm, r));
			}
		} catch (JSONException e) {
			logger.error("Unable to create a OpenedRandomnessCommitments. Error: {}", e);
			throw new BallotAuditCommitException("Unable to create a OpenedRandomnessCommitments.", e);
		}
	}

	/**
	 * Getter for the number of randomness values received
	 * 
	 * @return the number of randomness values received
	 */
	public final int getNumRandomnessValues() {
		return this.randomnessPairs.size();
	}

	/**
	 * Getter for the peer id
	 * 
	 * @return the peer id
	 */
	public final String getPeerId() {
		return this.peerId;
	}

	/**
	 * Returns a specific randomness pair if the id is known or can be used to
	 * iterate through all randomness pairs
	 * 
	 * @param index
	 * @return the randomness pair at the specified index
	 */
	public final RandomnessPair getRandomnessPair(int index) {
		if (index < 0) {
			return null;
		}
		if (index > this.randomnessPairs.size()) {
			return null;
		}
		return this.randomnessPairs.get(index);
	}

	/**
	 * Getter for the serial number
	 * 
	 * @return the serial number for the opened randomness commitment
	 */
	public final String getSerialNo() {
		return this.serialNo;
	}

	@Override
	public String toString() {
		return "OpenedRandomnessCommitments [serialNo=" + this.serialNo + ", randomnessPairs=" + this.randomnessPairs + ", peerId=" + this.peerId + "]";
	}
}
