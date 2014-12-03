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
package com.vvote.datafiles.commits.gencommit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.BallotGenCommitException;
import com.vvote.datafiles.fields.DataFileFields;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;

/**
 * This class represents a committed ballot cipher taken from the public WBB.
 * For any audited ballots we first get the opened randomness commitment values
 * for that ballot, combine the randomness values, re-encrypt the base candidate
 * ids using these randomness values and then compare the ciphers produced
 * against those stored by this class.
 * 
 * Each CommittedBallot object represents a single line of the ciphers.json
 * file.
 * 
 * @author James Rumble
 * 
 */
public class CommittedBallot {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommittedBallot.class);

	/**
	 * Holds the serial number for a specific committed ballot
	 */
	private final String serialNo;

	/**
	 * Holds the permutation string for a specific committed ballot
	 */
	private final String permutation;

	/**
	 * Holds an array of ElGamalECPoint objects representing the encrypted
	 * candidate ids
	 */
	private final List<ElGamalECPoint> ciphers;

	/**
	 * Constructor for a CommittedBallot from a JSON object. Sets the
	 * appropriate variable and constructs the list of ElGamalECPoint's.
	 * 
	 * @param json
	 * @throws BallotGenCommitException
	 */
	private CommittedBallot(JSONObject json) throws BallotGenCommitException {

		//logger.debug("Creating a new committed ballot object: {}", json);

		try {
			this.ciphers = new ArrayList<ElGamalECPoint>();
			this.serialNo = json.getString(DataFileFields.BallotGenCiphers.SERIAL_NO);
			this.permutation = json.getString(DataFileFields.BallotGenCiphers.PERMUTATION);

			JSONArray ciphersArray = json.getJSONArray(DataFileFields.BallotGenCiphers.CIPHERS);

			// construct an ElGamal EC Point for each element of the ciphers
			// array
			for (int i = 0; i < ciphersArray.length(); i++) {

				this.ciphers.add(ECUtils.constructElGamalECPointFromJSON(ciphersArray.getJSONObject(i)));
			}
		} catch (JSONException e) {
			logger.error("Unable to create a CommittedBallot. Error: {}", e);
			throw new BallotGenCommitException("Unable to create a CommittedBallot.", e);
		}
	}

	/**
	 * Constructor for a CommittedBallot from a string object. Merely constructs
	 * a JSONObject and passes the CommittedBallot(JSONObject json) constructor.
	 * 
	 * @param string
	 * @throws JSONException
	 * @throws BallotGenCommitException
	 */
	public CommittedBallot(String string) throws JSONException, BallotGenCommitException {
		this(new JSONObject(string));
	}

	/**
	 * Getter for the ciphers array for the committed ballot
	 * 
	 * @return the ciphers
	 */
	public List<ElGamalECPoint> getCiphers() {
		return Collections.unmodifiableList(this.ciphers);
	}

	/**
	 * Getter for the permutation string for the committed ballot
	 * 
	 * @return the permutation string
	 */
	public String getPermutation() {
		return this.permutation;
	}

	/**
	 * Getter for the serial number of the committed ballot
	 * 
	 * @return the serial number
	 */
	public String getSerialNo() {
		return this.serialNo;
	}

	@Override
	public String toString() {
		return "CommittedBallot [serialNo=" + this.serialNo + ", permutation=" + this.permutation + ", ciphers=" + this.ciphers + "]";
	}
}
