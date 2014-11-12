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
import com.vvote.PublicWBBConstants;
import com.vvote.exceptions.JSONSchemaException;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.json.JSONSchemaStore;
import com.vvote.verifierlibrary.json.JSONUtility;

/**
 * Provides an abstract representation of a JSONMessage which is constructed
 * from a <code>JSONObject</code>
 * 
 * @author James Rumble
 * 
 */
public abstract class JSONMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(JSONMessage.class);

	/**
	 * The time the message was committed to and sent
	 */
	private final String commitTime;

	/**
	 * Constructor for a generic <code>JSONMessage</code>
	 * 
	 * @param json
	 * @throws JSONMessageInitException
	 */
	public JSONMessage(JSONObject json) throws JSONMessageInitException {

		// check that the JSON message is not null
		if (json != null) {
			
			if(!this.performValidation(json)){
				logger.error("Unable to create a JSONMessage. Validation of the schema file failed. Error: {}");
				throw new JSONMessageInitException("Unable to create a JSONMessage. Validation of the schema file failed");
			}
			
			try {
				// get and set the commit time
				if (json.has(MessageFields.JSONMessage.COMMIT_TIME)) {
					String signatureCommitTime = json.getString(MessageFields.JSONMessage.COMMIT_TIME);
					this.commitTime = signatureCommitTime.substring(0, Math.min(signatureCommitTime.length(), PublicWBBConstants.COMMIT_TIME_LENGTH));
				} else {
					logger.error("The commit time for a JSONMessage must be provided");
					throw new JSONMessageInitException("The commit time for a JSONMessage must be provided");
				}
			} catch (JSONException e) {
				logger.error("Unable to create a JSONMessage. Error: {}", e);
				throw new JSONMessageInitException("Unable to create a JSONMessage.", e);
			}
		} else {
			logger.error("A JSONMessage object must be a valid JSON message");
			throw new JSONMessageInitException("A JSONMessage object must be a valid JSON message");
		}

	}

	/**
	 * Getter for the commit time
	 * 
	 * @return commitTime
	 */
	public final String getCommitTime() {
		return this.commitTime;
	}

	/**
	 * Abstract method which needs to be implemented
	 * 
	 * @return the class's JSONSchema
	 */
	public abstract JSONSchema getSchema();

	/**
	 * Perform validation of the message using a schema file
	 * @param json 
	 * 
	 * @return true if the validation using the schema file was carried out
	 *         successfully
	 */
	private boolean performValidation(JSONObject json){
		try {
			String schema = JSONSchemaStore.getSchema(this.getSchema());

			if (!JSONUtility.validateSchema(schema, json.toString())) {
				logger.error("Unable to validate json: {} using schema file", json.toString(), this.getSchema());
				return false;
			}
			return true;
		} catch (JSONSchemaException e) {
			logger.error("Unable to validate json: {} using schema file", json.toString(), this.getSchema());
			return false;
		}
	}
}
