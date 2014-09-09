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
package com.vvote.messages.typed.vote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.messages.typed.vote.exceptions.BallotPreferencesException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;

/**
 * Provides a representation of the preferences for each of the 3 races involved
 * in the election (i.e. la, lc atl, lc btl). Simply contains 3
 * <code>RacePreferences</code> objects
 * 
 * @author James Rumble
 * 
 */
public class BallotPreferences {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotPreferences.class);

	/**
	 * Contains the LA Preferences
	 */
	private final RacePreferences laPreferences;

	/**
	 * Contains the LC ATL preferences
	 */
	private final RacePreferences lcATLPreferences;

	/**
	 * Contains the LC BTL preferences
	 */
	private final RacePreferences lcBTLPreferences;

	/**
	 * Flag for whether the cast vote is ATL is BTL
	 */
	private final boolean isATL;

	/**
	 * Contains the actual json representation of the
	 * <code>BallotPreferences</code> object
	 */
	private final JSONArray races;

	/**
	 * Constructor for <code>BallotPreferences</code> object. Takes in a
	 * <code>JSONArray</code> as input with each JSONObject within containing an
	 * id and set of preferences
	 * 
	 * @param races
	 * @throws BallotPreferencesException
	 */
	public BallotPreferences(JSONArray races) throws BallotPreferencesException {

		// set the json
		this.races = races;

		// check that the json is not null
		if (this.races != null) {

			try {
				// check that the required number of races have been catered for
				if (this.races.length() == 3) {

					// create a new RacePreferences object for the LA race
					this.laPreferences = new RacePreferences(this.races.getJSONObject(0));
					// create a new RacePreferences object for the LC ATL race
					this.lcATLPreferences = new RacePreferences(this.races.getJSONObject(1));
					// create a new RacePreferences object for the LC BTL race
					this.lcBTLPreferences = new RacePreferences(this.races.getJSONObject(2));

					if (!this.laPreferences.getId().equals(RaceType.LA) || !this.lcATLPreferences.getId().equals(RaceType.LC_ATL) || !this.lcBTLPreferences.getId().equals(RaceType.LC_BTL)) {
						logger.error("Preferences must be provided in the order LA, LC_ATL, LC_BTL");
						throw new BallotPreferencesException("Preferences must be provided in the order LA, LC_ATL, LC_BTL");
					}

					// checks whether the voter used ATL or BTL to vote for the
					// LC
					// race
					if (this.lcATLPreferences.isUsed() && this.lcBTLPreferences.isUsed()) {
						logger.error("Preferences are not valid - cannot be both ATL and BTL");
						throw new BallotPreferencesException("Preferences are not valid - cannot be both ATL and BTL");
					}
					this.isATL = this.lcATLPreferences.isUsed();
				} else {
					logger.error("Preferences for all three races must be provided");
					throw new BallotPreferencesException("Preferences for all three races must be provided");
				}
			} catch (JSONException e) {
				logger.error("Unable to create BallotPreferences object", e);
				throw new BallotPreferencesException("Unable to create BallotPreferences object", e);
			}
		} else {
			logger.error("Preferences for a vote must be provided");
			throw new BallotPreferencesException("Preferences for a vote must be provided");
		}
	}

	/**
	 * Getter for the LA preferences
	 * 
	 * @return laPreferences
	 */
	public final RacePreferences getLaPreferences() {
		return this.laPreferences;
	}

	/**
	 * Getter for the LC ATL preferences
	 * 
	 * @return lcATLPreferences
	 */
	public final RacePreferences getLcATLPreferences() {
		return this.lcATLPreferences;
	}

	/**
	 * Getter for the LC BTL preferences
	 * 
	 * @return lcBTLPreferences
	 */
	public final RacePreferences getLcBTLPreferences() {
		return this.lcBTLPreferences;
	}

	/**
	 * Flag which indicates whether the voter used ATL or BTL for the LC race
	 * 
	 * @return isATL
	 */
	public final boolean isATL() {
		return this.isATL;
	}
}
