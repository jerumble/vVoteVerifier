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
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * This class holds the received randomness values from a number of mix servers
 * The received randomness values will be of the form rComm and r. There will be
 * the number of candidate ids + 1 randomness pairs. These randomness values are
 * the opened commitments to the randomness values that the specific client
 * device has used when carrying out encryption on the candidate ids. Each
 * serial number identifies a specific ballot which will then have a number of
 * sources of randomness values which are combined before being used for
 * encryption of the base candidate ids.
 * 
 * This class is used to hold the contents of a single line from the file
 * ballotsGenAudit.json Each line is mapped to a BallotGenerationRandomness
 * object
 * 
 * @author James Rumble
 * 
 */
public final class BallotGenerationRandomness {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenerationRandomness.class);

	/**
	 * The serial number of the ballot
	 */
	private final String serialNo;

	/**
	 * The number of randomness values stored (should be candidate ids + 1)
	 */
	private final int numberOfRandomnessValues;

	/**
	 * Holds each of the opened randomness commitments received from each Mix
	 * Server - each of these opened randomness commitments will be used by a
	 * PoD printer for the same specific ballot
	 */
	private final List<OpenedRandomnessCommitments> openedRandomness;

	/**
	 * Constructor taking in a JSONArray representation, each JSONObject within
	 * contains the opened randomness values from a number of different mix
	 * servers
	 * 
	 * @param jsonArray
	 *            The JSONArray representation
	 * @throws BallotAuditCommitException
	 */
	public BallotGenerationRandomness(JSONArray jsonArray) throws BallotAuditCommitException {

		this.openedRandomness = new ArrayList<OpenedRandomnessCommitments>();

		JSONObject currentObj = null;

		logger.debug("Creating a new BallotGenerationRandomness object: {}", jsonArray);

		// create a newOpenedRandomnessCommitments object for each list of
		// randomness
		// values received from each mix server
		for (int i = 0; i < jsonArray.length(); i++) {

			// get the current element/randomness pair
			try {
				currentObj = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				logger.error("Unable to read the current randomness pair: {}", e);
				throw new BallotAuditCommitException("Unable to read the current randomness pair");
			}

			this.openedRandomness.add(new OpenedRandomnessCommitments(currentObj));
		}

		// verify the opened randomness values - check that the actual data is
		// valid
		if (!this.verifyOpenedRandomnessValues()) {
			logger.error("Opened randomness values are not valid, check the logs and the file");
			throw new BallotAuditCommitException("Opened randomness values are not valid, check the logs and the file");
		}

		// sets the serial number and number of randomness values
		if (this.openedRandomness.size() > 0) {
			this.serialNo = this.openedRandomness.get(0).getSerialNo();
			this.numberOfRandomnessValues = this.openedRandomness.get(0).getNumRandomnessValues();
		} else {
			// shouldn't get to this point
			this.serialNo = null;
			this.numberOfRandomnessValues = 0;
			logger.error("Ballots Gen Audit file does not contain valid randomness values - check the file");
			throw new BallotAuditCommitException("Ballots Gen Audit file does not contain valid randomness values - check the file");
		}
	}

	/**
	 * Constructor taking in a string representation of a JSONArray which
	 * contain the opened randomness commitments from a number of different mix
	 * servers
	 * 
	 * @param str
	 *            The string representation of the JSONArray
	 * @throws JSONException
	 * @throws BallotAuditCommitException
	 */
	public BallotGenerationRandomness(String str) throws JSONException, BallotAuditCommitException {
		this(new JSONArray(str));
	}

	/**
	 * Getter for the number of randomness values received
	 * 
	 * @return the numebr of randomness pairings
	 */
	public final int getNumberOfRandomnessValues() {
		return this.numberOfRandomnessValues;
	}

	/**
	 * Returns an iterator for the list of opened randomness values
	 * 
	 * @return the list of opened randomness values
	 */
	public final List<OpenedRandomnessCommitments> getOpenedRandomnessValues() {
		return Collections.unmodifiableList(this.openedRandomness);
	}

	/**
	 * Getter for the serial number for the specific PoD Printer
	 * 
	 * @return the serial number
	 */
	public final String getSerialNo() {
		return this.serialNo;
	}

	/**
	 * Verifies that each group of randomness values shares the same serial
	 * number and the same number of randomness pairs
	 * 
	 * @return true if all opened randomness values have the same serial number
	 *         and all have the same number of randomness values
	 */
	private boolean verifyOpenedRandomnessValues() {
		// verify each group of randomness values shares the same serial
		// number and the same number of randomness pairs
		for (int i = 0; i < this.openedRandomness.size(); i++) {
			for (int j = i + 1; j < this.openedRandomness.size(); j++) {
				if (!this.openedRandomness.get(i).getSerialNo().equals(this.openedRandomness.get(j).getSerialNo())) {
					logger.error("Serial numbers do not match within the Ballots Gen Audit file");
					return false;
				}

				if (this.openedRandomness.get(i).getNumRandomnessValues() != this.openedRandomness.get(j).getNumRandomnessValues()) {
					logger.error("number of randomness values do not match within the Ballots Gen Audit file");
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "BallotGenerationRandomness [serialNo=" + this.serialNo + ", numberOfRandomnessValues=" + this.numberOfRandomnessValues + ", openedRandomness=" + this.openedRandomness + "]";
	}
}
