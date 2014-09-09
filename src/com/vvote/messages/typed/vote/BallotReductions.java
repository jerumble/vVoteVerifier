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

import com.vvote.messages.typed.vote.exceptions.BallotReductionException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;

/**
 * Provides a representation for the ballot reductions which any race may have
 * to reduce the un-needed ciphers from a generic ballot to match only the
 * candidates needed for that race. <code>BallotReductions</code> objects
 * contain <code>RaceReduction</code> objects for each of the individual races
 * LA, LC_ATL and LC_BTL and also a flag to indicate whether the ballot has
 * reductions
 * 
 * @author James Rumble
 * 
 */
public class BallotReductions {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotReductions.class);

	/**
	 * Flag to indicate whether reductions are used for the ballot
	 */
	private final boolean hasReductions;

	/**
	 * Race Reductions for the LA race
	 */
	private final RaceReduction laRaceReduction;

	/**
	 * Race reductions for the LC ATL race
	 */
	private final RaceReduction lcATLRaceReduction;

	/**
	 * Race reductions for the LC BTL race
	 */
	private final RaceReduction lcBTLRaceReduction;

	/**
	 * Constructor for a BallotReductions object
	 * 
	 * @param reductions
	 * @throws BallotReductionException
	 */
	public BallotReductions(JSONArray reductions) throws BallotReductionException {

		// ensure reductions aren't null - even when they aren't used they are
		// specified as [[],[],[]] and not null
		if (reductions != null) {

			// check the array is of the correct length
			if (reductions.length() == 3) {

				// check whether reductions are used
				try {
					if (reductions.getJSONArray(0).length() == 0 && reductions.getJSONArray(1).length() == 0 && reductions.getJSONArray(2).length() == 0) {
						this.hasReductions = false;
					} else {
						this.hasReductions = true;
					}

					// initialise the specific race reductions
					this.laRaceReduction = new RaceReduction(reductions.getJSONArray(0));
					this.lcATLRaceReduction = new RaceReduction(reductions.getJSONArray(1));
					this.lcBTLRaceReduction = new RaceReduction(reductions.getJSONArray(2));

				} catch (JSONException e) {
					logger.error("Unable to create BallotReductions object", e);
					throw new BallotReductionException("Unable to create BallotReductions object", e);
				}

			} else {
				logger.error("The reductions must be provided for a POD message");
				throw new BallotReductionException("The reductions must be provided for a POD message");
			}
		} else {
			logger.error("The reductions must be provided for a POD message");
			throw new BallotReductionException("The reductions must be provided for a POD message");
		}
	}

	/**
	 * Getter for the LA race reductions
	 * 
	 * @return laRaceReduction
	 */
	public final RaceReduction getLaRaceReduction() {
		return this.laRaceReduction;
	}

	/**
	 * Getter for the LC_ATL race reductions
	 * 
	 * @return lcATLRaceReduction
	 */
	public final RaceReduction getLcATLRaceReduction() {
		return this.lcATLRaceReduction;
	}

	/**
	 * Getter for the LC_BTL race reductions
	 * 
	 * @return lcBTLRaceReduction
	 */
	public final RaceReduction getLcBTLRaceReduction() {
		return this.lcBTLRaceReduction;
	}

	/**
	 * Getter for whether reductions are used
	 * 
	 * @return hasReductions
	 */
	public final boolean hasReductions() {
		return this.hasReductions;
	}

}
