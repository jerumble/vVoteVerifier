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

import com.vvote.JSONSchema;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.exceptions.VoteDataMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.typed.vote.exceptions.BallotPreferencesException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides an implementation of a VoteMessage. A Vote message contains the
 * actual preferences a voter chooses. The preferences determine the way in
 * which a voter has chosen to cast their vote. Preferences are separated into
 * separate races (i.e. LA, LC_ATL, LC_BTL). The voter may choose to only
 * provide a partial ranking and therefore leave out the ranking for any number
 * of candidate ids. A cast vote can also only be either ATL or BTL but not
 * both.
 * 
 * @author James Rumble
 * 
 */
public final class VoteMessage extends VoteDataMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VoteMessage.class);

	/**
	 * Private helper method to check whether the racePrefs from vPrefs matches
	 * the RacePreferences
	 * 
	 * @param racePrefs
	 * @param racePreferences
	 * @return true if race preferences match
	 */
	private static boolean verifyRacePreferencesMatch(String[] racePrefs, RacePreferences racePreferences) {

		logger.debug("Verifying that preferences match for a single race");

		// check contents of preferences
		for (int i = 0; i < racePreferences.numberOfPreferences(); i++) {
			try {
				if (!racePrefs[i].equals(racePreferences.getPreference(i))) {
					logger.warn("The preferences provided for the current race are not equal");
				}
			} catch (JSONException e) {
				logger.error("Unable to get the current preference", e);
				return false;
			}
		}

		logger.debug("Successfully verifyied that preferences match for a single race");

		return true;
	}

	/**
	 * The startEVMSig signature for a Vote Message
	 */
	private final String startEVMSig;

	/**
	 * The races array, this array contains a voters preferences separated into
	 * separate JSON Objects
	 */
	private final BallotPreferences races;

	/**
	 * The serialSig signature for a Vote Message
	 */
	private final String serialSig;

	/**
	 * The _vPrefs string - contains the full list of preferences
	 */
	private final String _vPrefs;

	/**
	 * Constructor for a Vote Message taking in a <code>JSONObject</code> as
	 * input
	 * 
	 * @param json
	 * @throws VoteDataMessageInitException
	 * @throws JSONMessageInitException
	 * @throws TypedJSONMessageInitException
	 */
	public VoteMessage(JSONObject json) throws VoteDataMessageInitException, TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the startEVMSig
			if (json.has(MessageFields.VoteMessage.START_EVM_SIG)) {
				this.startEVMSig = json.getString(MessageFields.VoteMessage.START_EVM_SIG);
			} else {
				logger.error("A VoteMessage must contain a start EVM signature");
				throw new VoteDataMessageInitException("A VoteMessage must contain a start EVM signature");
			}

			// get and set the races
			if (json.has(MessageFields.VoteMessage.RACES)) {
				try {
					this.races = new BallotPreferences(json.getJSONArray(MessageFields.VoteMessage.RACES));
				} catch (BallotPreferencesException e) {
					logger.error("Unable to create a VoteMessage. Error: {}", e);
					throw new VoteDataMessageInitException("Unable to create a VoteMessage.", e);
				}
			} else {
				logger.error("A VoteMessage must contain a valid set of preferences: {}", json);
				throw new VoteDataMessageInitException("A VoteMessage must contain a valid set of preferences: " + json);
			}

			// get and set the serialSig
			if (json.has(MessageFields.VoteMessage.SERIAL_SIG)) {
				this.serialSig = json.getString(MessageFields.VoteMessage.SERIAL_SIG);
			} else {
				logger.error("A VoteMessage must contain a serial signature");
				throw new VoteDataMessageInitException("A VoteMessage must contain a serial signature");
			}

			// get and set the vPrefs
			if (json.has(MessageFields.VoteMessage._vPREFS)) {
				this._vPrefs = json.getString(MessageFields.VoteMessage._vPREFS);
			} else {
				logger.error("A VoteMessage must contain a valid set of preferences: {}", json);
				throw new VoteDataMessageInitException("A VoteMessage must contain a valid set of preferences: " + json);
			}
		} catch (JSONException e) {
			logger.error("Unable to create a VoteMessage. Error: {}", e);
			throw new VoteDataMessageInitException("Unable to create a VoteMessage.", e);
		}

		if (!this.verifyPreferencesMatch()) {
			logger.error("The preferences found in {} and {} do not match", MessageFields.VoteMessage._vPREFS, MessageFields.VoteMessage.RACES);
			throw new VoteDataMessageInitException("The preferences found in " + MessageFields.VoteMessage._vPREFS + " and " + MessageFields.VoteMessage.RACES + " do not match");
		}
	}

	/**
	 * Getter for the preferences
	 * 
	 * @return races
	 */
	public final BallotPreferences getRaces() {
		return this.races;
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.VOTE_SCHEMA;
	}

	/**
	 * Getter for the serial signature
	 * 
	 * @return serialSig
	 */
	public final String getSerialSig() {
		return this.serialSig;
	}

	/**
	 * Getter for the startEVMSig
	 * 
	 * @return startEVMSig
	 */
	public final String getStartEVMSig() {
		return this.startEVMSig;
	}

	/**
	 * Getter for the vPrefs String
	 * 
	 * @return _vPrefs
	 */
	public final String getvPrefs() {
		return this._vPrefs;
	}
	
	/**
	 * Used as a private method to simply check that the races array matches the
	 * _vPrefs string
	 * 
	 * @return true if the preferences match in _vPrefs the races array
	 */
	private boolean verifyPreferencesMatch() {

		logger.debug("Verifying that preferences match between the _vPrefs list and the races array");

		// example vPrefs:
		// "_vPrefs":"5,2,4,3,1,6: , , : ,7, , , ,3,2,1,4,5,6, , :"

		// split into races
		String[] vPrefsRaceSplit = this._vPrefs.split(MessageFields.SERIAL_NO_SEPARATOR);

		// check 3 races have been provided
		if (vPrefsRaceSplit.length != 3) {
			logger.error("_vPrefs list isn't the correct length (3)");
			return false;
		}
		// split into single preferences
		String[] laRacePrefs = vPrefsRaceSplit[0].split(MessageFields.PREFERENCE_SEPARATOR);
		String[] lcATLRacePrefs = vPrefsRaceSplit[1].split(MessageFields.PREFERENCE_SEPARATOR);
		String[] lcBTLRacePrefs = vPrefsRaceSplit[2].split(MessageFields.PREFERENCE_SEPARATOR);

		// check la race
		if (!verifyRacePreferencesMatch(laRacePrefs, this.races.getLaPreferences())) {
			logger.warn("LA Race preferences do not match");
			return false;
		}

		// check lc atl race
		if (!verifyRacePreferencesMatch(lcATLRacePrefs, this.races.getLcATLPreferences())) {
			logger.warn("LC ATL Race preferences do not match");
			return false;
		}

		// check lc btl race
		if (!verifyRacePreferencesMatch(lcBTLRacePrefs, this.races.getLcBTLPreferences())) {
			logger.warn("LC BTL Race preferences do not match");
			return false;
		}

		logger.debug("Successfully verifyied that preferences match between the _vPrefs list and the races array");

		return true;
	}
	
	@Override
	public String getInternalSignableContent() throws JSONException{
	    StringBuilder internalSignableContent = new StringBuilder();
	    internalSignableContent.append(this.getSerialNo());
	    internalSignableContent.append(this.getDistrict());
	    internalSignableContent.append(this.getvPrefs());
	    internalSignableContent.append(this.getBoothSig());
	    internalSignableContent.append(this.getBoothID());
	    internalSignableContent.append(this.getCommitTime());
	    return internalSignableContent.toString();
	}
}
