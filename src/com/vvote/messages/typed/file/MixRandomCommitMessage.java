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
package com.vvote.messages.typed.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.JSONSchema;
import com.vvote.messages.exceptions.FileMessageInitException;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.fields.MessageFields;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides the implementation for a MixRandomCommitMessage
 * 
 * @author James Rumble
 * 
 */
public final class MixRandomCommitMessage extends FileMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MixRandomCommitMessage.class);

	/**
	 * The identifier for the printer
	 */
	private final String printerID;

	/**
	 * Constructor for a <code>MixRandomCommitMessage</code> object
	 * 
	 * @param json
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 * @throws FileMessageInitException
	 */
	public MixRandomCommitMessage(JSONObject json) throws TypedJSONMessageInitException, JSONMessageInitException, FileMessageInitException {
		super(json);

		try {
			// get and set the printerID
			if (json.has(MessageFields.MixRandomCommitMessage.PRINTER_ID)) {
				this.printerID = json.getString(MessageFields.MixRandomCommitMessage.PRINTER_ID);
			} else {
				logger.error("printerID of a MixRandomCommitMessage must be specified");
				throw new FileMessageInitException("printerID of a MixRandomCommitMessage must be specified");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a MixRandomCommitMessage. Error: {}", e);
			throw new FileMessageInitException("Unable to create a MixRandomCommitMessage.", e);
		}
	}

	/**
	 * Getter for the identifier of the printer
	 * 
	 * @return printerID
	 */
	public final String getPrinterID() {
		return this.printerID;
	}
	
	@Override
	public JSONSchema getSchema() {
		return JSONSchema.MIX_RANDOM_COMMIT_SCHEMA;
	}
	
	@Override
	public String getInternalSignableContent(){
	    StringBuilder internalSignableContent = new StringBuilder();
	    internalSignableContent.append(this.getSubmissionId());
	    internalSignableContent.append(this.getBoothID());
	    internalSignableContent.append(this.getPrinterID());
	    internalSignableContent.append(this.getDigest());
	    internalSignableContent.append(this.getCommitTime());
	    return internalSignableContent.toString();
	}
}
