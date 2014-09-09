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
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Represents an Audit message sent from an audit machine within the polling
 * station during the election. The ballot will have been audited during the
 * election itself
 * 
 * @author James Rumble
 *
 */
public class AuditMessage extends VoteDataMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuditMessage.class);

	/**
	 * The commitWitness
	 */
	private final String commitWitness;

	/**
	 * The permutation
	 */
	private final String permutation;

	/**
	 * The serial signature
	 */
	private final String serialSig;

	/**
	 * The _reducedPerms
	 */
	private final String _reducedPerms;

	/**
	 * Constructor for an Audit message
	 * 
	 * @param json
	 * @throws VoteDataMessageInitException
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 */
	public AuditMessage(JSONObject json) throws VoteDataMessageInitException, TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the commitWitness
			if (json.has(MessageFields.AuditMessage.COMMIT_WITNESS)) {
				this.commitWitness = json.getString(MessageFields.AuditMessage.COMMIT_WITNESS);
			} else {
				logger.error("The commitWitness of a AuditMessage must be specified");
				throw new VoteDataMessageInitException("The commitWitness of a AuditMessage must be specified");
			}

			// get and set the permutation
			if (json.has(MessageFields.AuditMessage.PERMUTATION)) {
				this.permutation = json.getString(MessageFields.AuditMessage.PERMUTATION);
			} else {
				logger.error("The permutation for a AuditMessage must be specified");
				throw new VoteDataMessageInitException("The permutation for a AuditMessage must be specified");
			}

			// get and set the serialSig
			if (json.has(MessageFields.AuditMessage.SERIAL_SIG)) {
				this.serialSig = json.getString(MessageFields.AuditMessage.SERIAL_SIG);
			} else {
				logger.error("The serialSig for a AuditMessage must be specified");
				throw new VoteDataMessageInitException("The serialSig for a AuditMessage must be specified");
			}

			// get and set the _reducedPerms
			if (json.has(MessageFields.AuditMessage.REDUCED_PERMUTATION)) {
				this._reducedPerms = json.getString(MessageFields.AuditMessage.REDUCED_PERMUTATION);
			} else {
				logger.error("The _reducedPerms for a AuditMessage must be specified");
				throw new VoteDataMessageInitException("The _reducedPerms for a AuditMessage must be specified");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a VoteDataMessage. Error: {}", e);
			throw new VoteDataMessageInitException("Unable to create a VoteDataMessage.", e);
		}
	}

	/**
	 * Getter for the commit witness
	 * 
	 * @return commitWitness
	 */
	public String getCommitWitness() {
		return this.commitWitness;
	}

	/**
	 * Getter for the permutation
	 * 
	 * @return permutation
	 */
	public String getPermutation() {
		return this.permutation;
	}

	/**
	 * Getter for the serial signature
	 * 
	 * @return serialSig
	 */
	public String getSerialSig() {
		return this.serialSig;
	}

	/**
	 * Getter for the reduced permutation
	 * 
	 * @return _reducedPerms
	 */
	public String get_reducedPerms() {
		return this._reducedPerms;
	}

	@Override
	public String getInternalSignableContent() throws JSONException {
		StringBuilder internalSignableContent = new StringBuilder();
		internalSignableContent.append(this.getType().toString());
		internalSignableContent.append(this.getSerialNo());
		internalSignableContent.append(this.getCommitTime());
		return internalSignableContent.toString();
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.AUDIT_SCHEMA;
	}

}
