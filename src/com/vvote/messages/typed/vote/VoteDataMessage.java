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

import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.exceptions.VoteDataMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides an abstract implementation of messages which are part of the vote
 * data sent to the public wbb by the client devices which may include fields
 * such as ballot reduction and the user's preferences
 * 
 * @author James Rumble
 * 
 */
public abstract class VoteDataMessage extends TypedJSONMessage {
	
	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VoteDataMessage.class);

	/**
	 * The serial number of a device
	 */
	private final String serialNo;

	/**
	 * The district in which the device is being used
	 */
	private final String district;

	/**
	 * Constructor for a <code>VoteDataMessages</code> object. Uses the super
	 * constructor and initialises new fields with extra values
	 * 
	 * @param json
	 * @throws VoteDataMessageInitException 
	 * @throws JSONMessageInitException 
	 * @throws TypedJSONMessageInitException 
	 */
	public VoteDataMessage(JSONObject json) throws VoteDataMessageInitException, TypedJSONMessageInitException, JSONMessageInitException {
		super(json);

		try {
			// get and set the serial number
			if (json.has(MessageFields.VoteDataMessage.SERIAL_NO)) {
				
					this.serialNo = json.getString(MessageFields.VoteDataMessage.SERIAL_NO);

			} else {
				logger.error("The serial number of a JSONMessage must be specified");
				throw new VoteDataMessageInitException("The serial number of a JSONMessage must be specified");
			}

			// get and set the district
			if (json.has(MessageFields.VoteDataMessage.DISTRICT)) {
				this.district = json.getString(MessageFields.VoteDataMessage.DISTRICT);
			} else {
				logger.error("The district for a JSONMessage must be specified");
				throw new VoteDataMessageInitException("The district for a JSONMessage must be specified");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a VoteDataMessage. Error: {}", e);
			throw new VoteDataMessageInitException("Unable to create a VoteDataMessage.", e);
		}
	}

	/**
	 * Getter for the district of a message
	 * 
	 * @return district
	 */
	public final String getDistrict() {
		return this.district;
	}

	/**
	 * Getter for the serial number of the message
	 * 
	 * @return serialNo
	 */
	public final String getSerialNo() {
		return this.serialNo;
	}
}
