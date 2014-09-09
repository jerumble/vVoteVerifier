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
import com.vvote.messages.typed.vote.exceptions.BallotReductionException;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * A print on Demand (POD) message includes any ballot reductions a
 * VoteDataMessage may include. The ballot reductions will provide the details
 * needed to remove any specific candidate identifiers from the generic ballots
 * included in ciphers.json which were not used in the specific race. Ballot
 * reductions also include the randomness values which were used on the
 * individual candidate identifiers to encrypt the plaintext candidate
 * identifier to the encrypted candidate identifier included in the generic
 * ballot
 * 
 * @author James Rumble
 * 
 */
public final class PODMessage extends VoteDataMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PODMessage.class);

	/**
	 * The ballot reductions in the json message in json form
	 */
	private final JSONArray jsonBallotReductions;

	/**
	 * The ballot reductions in the json message
	 */
	private final BallotReductions ballotReductions;

	/**
	 * Constructor for a PODMessage
	 * 
	 * @param json
	 * @throws VoteDataMessageInitException
	 * @throws JSONMessageInitException
	 * @throws TypedJSONMessageInitException
	 */
	public PODMessage(JSONObject json) throws VoteDataMessageInitException, TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the ballot reductions
			if (json.has(MessageFields.PODMessage.BALLOT_REDUCTIONS)) {
				this.jsonBallotReductions = json.getJSONArray(MessageFields.PODMessage.BALLOT_REDUCTIONS);
				try {
					this.ballotReductions = new BallotReductions(this.jsonBallotReductions);
				} catch (BallotReductionException e) {
					logger.error("Unable to create a PODMessage. Error: {}", e);
					throw new VoteDataMessageInitException("Unable to create a PODMessage.", e);
				}
			} else {
				logger.error("A POD Message must contain ballot reductions");
				throw new VoteDataMessageInitException("A POD Message must contain ballot reductions");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a PODMessage. Error: {}", e);
			throw new VoteDataMessageInitException("Unable to create a PODMessage.", e);
		}
	}

	/**
	 * Getter for the ballot reductions
	 * 
	 * @return ballotReductions
	 */
	public final BallotReductions getBallotReductions() {
		return this.ballotReductions;
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.POD_SCHEMA;
	}
	
	@Override
	public String getInternalSignableContent() throws JSONException{
	    StringBuilder internalSignableContent = new StringBuilder();
	    internalSignableContent.append(this.getSerialNo());
	    internalSignableContent.append(this.getDistrict());
	    internalSignableContent.append(this.jsonBallotReductions.toString());
	    internalSignableContent.append(this.getCommitTime());
	    return internalSignableContent.toString();
	}
}
