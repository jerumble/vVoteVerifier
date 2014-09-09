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

import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.typed.vote.exceptions.BallotReductionException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides the representation of a single reduction - this includes the index
 * which is the permuted index of the cipher – this is between 0 and the number
 * of candidates in that race, the candidateIndex which is the index of the
 * candidateID that is being removed and randomness which is the combined
 * randomness that was used to re-encrypt the base_encrypted_candidateID to
 * create the final cipher
 * 
 * @author James Rumble
 * 
 */
public final class Reduction {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Reduction.class);

	/**
	 * The permuted index of the candidate identifier to remove
	 */
	private final int index;

	/**
	 * The candidate index - this is the index of the base encrypted candidate
	 * identifier which is to be removed
	 */
	private final int candidateIndex;

	/**
	 * The randomness value used to encrypted the candidate identifier at
	 * candidateIndex from base_encrypted_candidateID to create the removed
	 * candidate identifier at index
	 */
	private final String randomness;

	/**
	 * The actual json for the reduction
	 */
	private final JSONObject json;

	/**
	 * Constructor for a reduction
	 * 
	 * @param json
	 * @throws BallotReductionException
	 */
	public Reduction(JSONObject json) throws BallotReductionException {

		// set the json object
		this.json = json;

		// Check that the json is not null
		if (this.json != null) {

			try {
				// get and set the index
				if (this.json.has(MessageFields.PODMessage.BALLOT_REDUCTIONS_INDEX)) {
					try {
						this.index = this.json.getInt(MessageFields.PODMessage.BALLOT_REDUCTIONS_INDEX);
					} catch (NumberFormatException e) {
						logger.error("Unable to get the ballot reductions index", e);
						throw new BallotReductionException("Unable to get the ballot reductions index", e);
					}
				} else {
					logger.error("Unable to get the ballot reductions index");
					throw new BallotReductionException("Unable to get the ballot reductions index");
				}

				// get and set the candidate index
				if (this.json.has(MessageFields.PODMessage.BALLOT_REDUCTIONS_CANDIDATE_INDEX)) {
					try {
						this.candidateIndex = this.json.getInt(MessageFields.PODMessage.BALLOT_REDUCTIONS_CANDIDATE_INDEX);
					} catch (NumberFormatException e) {
						logger.error("Unable to get the ballot reductions index candidate", e);
						throw new BallotReductionException("Unable to get the ballot reductions candidate index", e);
					}
				} else {
					logger.error("Unable to get the ballot reductions index candidate");
					throw new BallotReductionException("Unable to get the ballot reductions candidate index");
				}

				// get and set the randomness array
				if (this.json.has(MessageFields.PODMessage.BALLOT_REDUCTIONS_RANDOMNESS)) {
					this.randomness = this.json.getString(MessageFields.PODMessage.BALLOT_REDUCTIONS_RANDOMNESS);
				} else {
					logger.error("Unable to get the ballot reductions randomness value");
					throw new BallotReductionException("Unable to get the ballot reductions randomness value");
				}
			} catch (JSONException e) {
				logger.error("Unable to create Reduction object");
				throw new BallotReductionException("Unable to create Reduction object");
			}
		} else {
			logger.error("Unable to create Reduction object");
			throw new BallotReductionException("Unable to create Reduction object");
		}
	}

	/**
	 * Getter for the candidate index for the reduction
	 * 
	 * @return candidateIndex
	 */
	public final int getCandidateIndex() {
		return this.candidateIndex;
	}

	/**
	 * Getter for the index for the reduction
	 * 
	 * @return index
	 */
	public final int getIndex() {
		return this.index;
	}

	/**
	 * Getter for the json containing the the reduction
	 * 
	 * @return json
	 */
	public final JSONObject getJson() {
		return this.json;
	}

	/**
	 * Getter for the randomness value for the reduction
	 * 
	 * @return randomness
	 */
	public final String getRandomness() {
		return this.randomness;
	}
}
