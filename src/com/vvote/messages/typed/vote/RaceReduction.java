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

import com.vvote.messages.typed.vote.exceptions.BallotReductionException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;

/**
 * Provides a representation of a specific set of race reductions. The
 * reductions will be for a specific race only.
 * 
 * @author James Rumble
 * 
 */
public class RaceReduction {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RaceReduction.class);

	/**
	 * A list of reductions
	 */
	private final List<Reduction> reductions;

	/**
	 * Flag for whether reductions are used
	 */
	private final boolean hasReductions;

	/**
	 * Constructor for a <code>RaceReduction</code> object taking as input a
	 * reductions array
	 * 
	 * @param reductions
	 * @throws BallotReductionException
	 */
	public RaceReduction(JSONArray reductions) throws BallotReductionException {

		// ensure the reductions array isn't null - even if they aren't used []
		// will be used instead
		if (reductions != null) {

			this.reductions = new ArrayList<Reduction>();

			// check whether reductions are used
			if (reductions.length() > 0) {

				this.hasReductions = true;

				try {

					// if reductions are used then create new reductions
					for (int i = 0; i < reductions.length(); i++) {

						this.reductions.add(new Reduction(reductions.getJSONObject(i)));
					}

				} catch (JSONException e) {
					logger.error("Unable to create RaceReduction object", e);
					throw new BallotReductionException("Unable to create RaceReduction object", e);
				}
			} else {
				this.hasReductions = false;
			}
		} else {
			logger.error("Race reductions cannot not null");
			throw new BallotReductionException("Race reductions cannot not null");
		}
	}

	/**
	 * Getter for a specific reduction
	 * 
	 * @param index
	 * @return Reduction at reductions[i]
	 */
	public Reduction getReduction(int index) {
		if (index < this.reductions.size()) {
			return this.reductions.get(index);
		}
		return null;
	}

	/**
	 * Getter for the list of reductions
	 * 
	 * @return reductions
	 */
	public List<Reduction> getReductions() {
		return Collections.unmodifiableList(this.reductions);
	}

	/**
	 * Getter for the flag indicating whether reductions are used
	 * 
	 * @return hasReductions
	 */
	public boolean hasReductions() {
		return this.hasReductions;
	}
}
