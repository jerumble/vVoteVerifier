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
package com.vvote.messages.typed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.messages.JSONMessage;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides an abstract representation of a <code>TypedJSONMessage</code>.
 * TypedJSONMessage's will be used for communication in the original source code
 * between devices and components and therefore much of the information on the
 * public wbb will be of the form of a JSON Message of some specific type.
 * 
 * @author James Rumble
 * 
 */
public abstract class TypedJSONMessage extends JSONMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TypedJSONMessage.class);

	/**
	 * The booth id of the message
	 */
	private final String boothID;

	/**
	 * The signature of the device
	 */
	private final String boothSig;

	/**
	 * The type of the <code>TypedJSONMessage</code> which determines which
	 * concrete implementation will be used to represent the message
	 */
	private final MessageType type;

	/**
	 * Constructor for an abstract <code>TypedJSONMessage</code>. Checks that
	 * the required fields are included in the message and sets the values.
	 * 
	 * @param json
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 */
	public TypedJSONMessage(JSONObject json) throws TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the booth id
			if (json.has(MessageFields.TypedJSONMessage.BOOTH_ID)) {
				this.boothID = json.getString(MessageFields.TypedJSONMessage.BOOTH_ID);
			} else {
				logger.error("The booth id of a TypedJSONMessage must be specified");
				throw new TypedJSONMessageInitException("The booth id of a TypedJSONMessage must be specified");
			}

			// get and set the booth signature
			if (json.has(MessageFields.TypedJSONMessage.BOOTH_SIG)) {
				this.boothSig = json.getString(MessageFields.TypedJSONMessage.BOOTH_SIG);
			} else {
				logger.error("A booth sig must be provided for a TypedJSONMessage");
				throw new TypedJSONMessageInitException("A booth sig must be provided for a TypedJSONMessage");
			}

			// get and set the message type
			if (json.has(MessageFields.TYPE)) {
				this.type = MessageType.getMessageTypeFromType(json.getString(MessageFields.TYPE));
				if (this.type == null) {
					logger.error("The type given is not a valid type for a TypedJSONMessage");
					throw new TypedJSONMessageInitException("The type given is not a valid type for a TypedJSONMessage");
				}
			} else {
				logger.error("The type for a TypedJSONMessage must be specified");
				throw new TypedJSONMessageInitException("The type for a TypedJSONMessage must be specified");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a TypedJSONMessage. Error: {}", e);
			throw new JSONMessageInitException("Unable to create a TypedJSONMessage.", e);
		}
	}

	/**
	 * Defines the internal content that will be signed by the message. This
	 * method is used for checking the signatures over commitments made to the
	 * public wbb
	 * 
	 * @return the internal signable content
	 * @throws JSONException 
	 */
	public abstract String getInternalSignableContent() throws JSONException;

	/**
	 * Getter for the booth id
	 * 
	 * @return boothID
	 */
	public final String getBoothID() {
		return this.boothID;
	}

	/**
	 * getter for the booth signature
	 * 
	 * @return boothSig
	 */
	public final String getBoothSig() {
		return this.boothSig;
	}

	/**
	 * Getter for the message type
	 * 
	 * @return type
	 */
	public final MessageType getType() {
		return this.type;
	}
}
