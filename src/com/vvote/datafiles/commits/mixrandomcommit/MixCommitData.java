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
package com.vvote.datafiles.commits.mixrandomcommit;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.MixCommitException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Holds the randomness commitments made by a particular server
 * 
 * @author James Rumble
 * 
 */
public final class MixCommitData {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MixCommitData.class);

	/**
	 * Identifier for the serial number the randomness values were sent to
	 */
	private static final String SERIAL_NO = "serialNo";

	/**
	 * Identifier for the randomness array
	 */
	private static final String RANDOMNESS = "randomness";

	/**
	 * The serial number of the device the randomness values were sent to
	 */
	private final String serialNo;

	/**
	 * The commitments made to randomness values the server sent to a client
	 * device
	 */
	private final ArrayList<String> randomnessValues;

	/**
	 * The server name of where the commitments to randomness values are coming
	 * from - this isn't taken from the JSON itself however is useful for
	 * context
	 */
	private final String serverName;

	/**
	 * Constructor for a mix server randomness commitments object
	 * 
	 * @param serverName
	 * @param json
	 * @throws MixCommitException
	 */
	private MixCommitData(String serverName, JSONObject json) throws MixCommitException {

		logger.debug("Creating a new MixRandomnessCommit object: {}: {}", serverName, json);

		// setup variables
		this.serverName = serverName;

		try {
			this.serialNo = json.getString(SERIAL_NO);

			JSONArray randomnessArray = json.getJSONArray(RANDOMNESS);

			this.randomnessValues = new ArrayList<String>();

			// read in the randomness commitment values
			for (int i = 0; i < randomnessArray.length(); i++) {
				this.randomnessValues.add(randomnessArray.getString(i));
			}
		} catch (JSONException e) {
			logger.error("Unable to create a MixCommitData. Error: {}", e);
			throw new MixCommitException("Unable to create a MixCommitData.", e);
		}
	}

	/**
	 * Constructor for a mix server randomness commitments object from a string
	 * representation of the JSON
	 * 
	 * @param serverName
	 * @param json
	 * @throws JSONException
	 * @throws MixCommitException
	 */
	public MixCommitData(String serverName, String json) throws JSONException, MixCommitException {
		this(serverName, new JSONObject(json));
	}

	/**
	 * Getter for the number of randomness values
	 * 
	 * @return the number of randomness values
	 */
	public final int getNumberOfRandomnessValues() {
		return this.randomnessValues.size();
	}

	/**
	 * Getter for the randomness value at a specific index
	 * 
	 * @param index
	 * @return the randomness value at the specified index
	 */
	public final String getRandomnessValue(int index) {
		return this.randomnessValues.get(index);
	}

	/**
	 * Getter for the serial number
	 * 
	 * @return the serial number
	 */
	public final String getSerialNo() {
		return this.serialNo;
	}

	/**
	 * Getter for the server name
	 * 
	 * @return the name of the server
	 */
	public final String getServerName() {
		return this.serverName;
	}

	@Override
	public String toString() {
		return "MixCommitData [serialNo=" + this.serialNo + ", randomnessValues=" + this.randomnessValues + ", serverName=" + this.serverName + "]";
	}
}
