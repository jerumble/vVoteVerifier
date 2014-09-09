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
package com.vvote.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.JSONSchema;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation for a json signature message which includes a joint
 * signature over specific data
 * 
 * @author James Rumble
 * 
 */
public final class SignatureMessage extends JSONMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(SignatureMessage.class);

	/**
	 * The joint signature
	 */
	private final String jointSig;

	/**
	 * The joint json file
	 */
	private final String jsonFile;

	/**
	 * The joint attachment file
	 */
	private final String attachmentFile;

	/**
	 * Constructor for a Signature message taking as input a
	 * <code>JSONObject</code> object
	 * 
	 * @param json
	 * @throws JSONMessageInitException
	 */
	public SignatureMessage(JSONObject json) throws JSONMessageInitException {
		super(json);

		try {
			// get and set the joint sig
			if (json.has(MessageFields.SignatureMessage.JOINT_SIG)) {
				this.jointSig = json.getString(MessageFields.SignatureMessage.JOINT_SIG);
			} else {
				logger.error("A joint signature must be provided for a SignatureMessage");
				throw new JSONMessageInitException("A joint signature must be provided for a SignatureMessage");
			}

			// get and set the json file
			if (json.has(MessageFields.SignatureMessage.JSON_FILE)) {
				this.jsonFile = json.getString(MessageFields.SignatureMessage.JSON_FILE);
			} else {
				logger.error("A json file must be provided for a SignatureMessage");
				throw new JSONMessageInitException("A json file must be provided for a SignatureMessage");
			}

			// get and set the attachment file
			if (json.has(MessageFields.SignatureMessage.ATTACHMENT_FILE)) {
				this.attachmentFile = json.getString(MessageFields.SignatureMessage.ATTACHMENT_FILE);
			} else {
				logger.error("An attachment file must be provided for a SignatureMessage");
				throw new JSONMessageInitException("An attachment file must be provided for a SignatureMessage");
			}

		} catch (JSONException e) {
			logger.error("Unable to create a SignatureMessage. Error: {}", e);
			throw new JSONMessageInitException("Unable to create a SignatureMessage.", e);
		}
	}

	/**
	 * Getter for the attachment file
	 * 
	 * @return attachmentFile
	 */
	public final String getAttachmentFile() {
		return this.attachmentFile;
	}

	/**
	 * Getter for the joint signature
	 * 
	 * @return jointSig
	 */
	public final String getJointSig() {
		return this.jointSig;
	}

	/**
	 * Getter for the json file
	 * 
	 * @return jsonFile
	 */
	public final String getJsonFile() {
		return this.jsonFile;
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.JOINT_SIGNATURE_SCHEMA;
	}
}
