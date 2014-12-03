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
 * Provides the representation for a cancel message sent to the Public WBB
 * received from a cancellation machine. The cancel messages will contain a
 * joint signature showing that the cancel of a particular ballot has been
 * cancelled.
 * 
 * @author James Rumble
 *
 */
public class CancelMessage extends VoteDataMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CancelMessage.class);

	/**
	 * The cancel authority signature
	 */
	private final String cancelAuthSig;

	/**
	 * The cancel authority identifier
	 */
	private final String cancelAuthID;

	/**
	 * The serial signature
	 */
	private final String serialSig;

	/**
	 * Constructor for a cancel message
	 * 
	 * @param json
	 * @throws VoteDataMessageInitException
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 */
	public CancelMessage(JSONObject json) throws VoteDataMessageInitException, TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the cancelAuthSig
			if (json.has(MessageFields.CancelMessage.CANCEL_AUTH_SIG)) {
				this.cancelAuthSig = json.getString(MessageFields.CancelMessage.CANCEL_AUTH_SIG);
			} else {
				logger.error("The cancelAuthSig of a CancelMessage must be specified");
				throw new VoteDataMessageInitException("The cancelAuthSig of a CancelMessage must be specified");
			}

			// get and set the cancelAuthID
			if (json.has(MessageFields.CancelMessage.CANCEL_AUTH_ID)) {
				this.cancelAuthID = json.getString(MessageFields.CancelMessage.CANCEL_AUTH_ID);
			} else {
				logger.error("The cancelAuthID for a CancelMessage must be specified");
				throw new VoteDataMessageInitException("The cancelAuthID for a CancelMessage must be specified");
			}

			// get and set the serialSig
			if (json.has(MessageFields.CancelMessage.SERIAL_SIG)) {
				this.serialSig = json.getString(MessageFields.CancelMessage.SERIAL_SIG);
			} else {
				logger.error("The serialSig for a CancelMessage must be specified");
				throw new VoteDataMessageInitException("The serialSig for a CancelMessage must be specified");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a CancelMessage. Error: {}", e);
			throw new VoteDataMessageInitException("Unable to create a CancelMessage.", e);
		}
	}

	/**
	 * Getter for a cancel authority signature
	 * 
	 * @return cancelAuthSig
	 */
	public String getCancelAuthSig() {
		return this.cancelAuthSig;
	}

	/**
	 * Getter for a cancel authority identifier
	 * 
	 * @return cancelAuthID
	 */
	public String getCancelAuthID() {
		return this.cancelAuthID;
	}

	/**
	 * Getter for a serial signature
	 * 
	 * @return serialSig
	 */
	public String getSerialSig() {
		return this.serialSig;
	}

	@Override
	public String getInternalSignableContent() throws JSONException {
		StringBuilder internalSignableContent = new StringBuilder();
		internalSignableContent.append(this.getType().getType().toString());
		internalSignableContent.append(this.getSerialNo());
		return internalSignableContent.toString();
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.CANCEL_SCHEMA;
	}

}
