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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.typed.vote.exceptions.BallotPreferencesException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation for a single set of preferences for a specific race
 * 
 * @author James Rumble
 * 
 */
public class RacePreferences {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RacePreferences.class);

	/**
	 * Identifier for the set of preferences - will be either LA, LC ATL or LC
	 * BTL
	 */
	private final RaceType id;

	/**
	 * String array representing the preferences
	 */
	private final List<String> preferencesArray;

	/**
	 * Determines whether the specific race preferences was used
	 */
	private final boolean isUsed;

	/**
	 * the actual json
	 */
	private final JSONObject json;

	/**
	 * json array representing the preferences
	 */
	private final JSONArray preferences;

	/**
	 * Constructor for a <code>RacePreferences</code> object taking as input a
	 * <code>JSONObject</code> object
	 * 
	 * @param json
	 * @throws BallotPreferencesException
	 */
	public RacePreferences(JSONObject json) throws BallotPreferencesException {

		// set the json
		this.json = json;

		// check the json is not null
		if (this.json != null) {

			try {
				// get and set the id which is of type enum RaceType
				if (this.json.has(MessageFields.VoteMessage.RACE_ID)) {
					this.id = RaceType.fromString(this.json.getString(MessageFields.VoteMessage.RACE_ID));

					// check that the race type was initialised correctly
					if (this.id == null) {
						logger.error("Unable to create RacePreferences object - The type of race provided is not valid");
						throw new BallotPreferencesException("Unable to create RacePreferences object - The type of race provided is not valid");
					}
				} else {
					logger.error("Unable to create RacePreferences object - The preferences for a vote must have an id identifying the race");
					throw new BallotPreferencesException(
							"Unable to create RacePreferences object - The preferences for a vote must have an id identifying the race");
				}

				// get and set the preferences list
				if (this.json.has(MessageFields.VoteMessage.PREFERENCES)) {

					// set the json array
					this.preferences = this.json.getJSONArray(MessageFields.VoteMessage.PREFERENCES);

					// initialise string arraylist
					this.preferencesArray = new ArrayList<String>();

					boolean isUsed = false;

					// add json prefs to string preferences list
					for (int i = 0; i < this.preferences.length(); i++) {
						this.preferencesArray.add(this.preferences.getString(i));

						// check whether it is used
						if (!this.preferences.get(i).equals(MessageFields.VoteMessage.PREFERENCE_IS_BLANK)) {
							isUsed = true;
						}
					}

					this.isUsed = isUsed;

				} else {
					logger.error("Unable to create RacePreferences object - The preferences must be provided for a vote");
					throw new BallotPreferencesException("Unable to create RacePreferences object - The preferences must be provided for a vote");
				}
			} catch (JSONException e) {
				logger.error("Unable to create RacePreferences object");
				throw new BallotPreferencesException("Unable to create RacePreferences object");
			}
		} else {
			logger.error("Unable to create RacePreferences object - The preferences for a vote must be provided");
			throw new BallotPreferencesException("Unable to create RacePreferences object - The preferences for a vote must be provided");
		}
	}

	/**
	 * Getter for the race type
	 * 
	 * @return id
	 */
	public RaceType getId() {
		return this.id;
	}

	/**
	 * Getter for a specific preference
	 * 
	 * @param index
	 * @return preferences at index
	 * @throws JSONException
	 */
	public String getPreference(int index) throws JSONException {
		return this.preferences.getString(index);
	}

	/**
	 * Getter for the string arraylist representation of the preferences array
	 * 
	 * @return preferencesArray
	 */
	public List<String> getPreferencesArray() {
		return Collections.unmodifiableList(this.preferencesArray);
	}

	/**
	 * Getter for the flag as to whether any preferences are provided for the
	 * race
	 * 
	 * @return isUsed
	 */
	public boolean isUsed() {
		return this.isUsed;
	}

	/**
	 * Getter for the number of preferences included
	 * 
	 * @return length of preferences
	 */
	public int numberOfPreferences() {
		return this.preferences.length();
	}
}
