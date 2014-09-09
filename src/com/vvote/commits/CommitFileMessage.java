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
package com.vvote.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.commits.exceptions.CommitFileMessageInitException;
import com.vvote.messages.exceptions.TypedJSONMessageInitException;
import com.vvote.messages.exceptions.UnknownMessageException;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;
import com.vvote.verifierlibrary.utils.messages.MessageFactory;

/**
 * Provides a representation of a <code>CommitFileMessage</code> object which
 * will have a filename, identifier and a list of <code>JSONMessage</code>
 * objects contained within it
 * 
 * @author James Rumble
 * 
 */
public final class CommitFileMessage extends CommitFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitFileMessage.class);

	/**
	 * Storage for the <code>JSONMessage</code> objects
	 */
	private final List<TypedJSONMessage> jsonMessages;

	/**
	 * Constructor for a <code>CommitFileMessage</code> object taking as input
	 * the filename
	 * 
	 * @param filePath
	 * @throws CommitFileMessageInitException
	 * @throws CommitFileInitException
	 */
	public CommitFileMessage(String filePath) throws CommitFileMessageInitException, CommitFileInitException {
		super(filePath);

		logger.debug("Creating a CommitFileMessage object: {}", filePath);

		if (!this.verifyExtension()) {
			logger.error("A Commit file message must be a .json file");
			throw new CommitFileMessageInitException("A Commit file message must be a .json file");
		}

		this.jsonMessages = new ArrayList<TypedJSONMessage>();

		List<JSONObject> jsonObjects;
		try {
			jsonObjects = IOUtils.readJSONMessagesFromFile(this.getFilePath());

			this.loadMessages(jsonObjects);

		} catch (JSONException e) {
			logger.error("There was a problem reading the json file containing JSONMessages: {}", this.getFilePath(), e);
			throw new CommitFileMessageInitException("There was a problem reading the json file containing JSONMessages: " + this.getFilePath(), e);
		} catch (JSONIOException e) {
			logger.error("There was a problem reading the json file containing JSONMessages: {}", this.getFilePath(), e);
			throw new CommitFileMessageInitException("There was a problem reading the json file containing JSONMessages: " + this.getFilePath(), e);
		} catch (UnknownMessageException e) {
			logger.error("There was a problem reading the json file containing JSONMessages: {}", this.getFilePath(), e);
			throw new CommitFileMessageInitException("There was a problem reading the json file containing JSONMessages: " + this.getFilePath(), e);
		} catch (TypedJSONMessageInitException e) {
			logger.error("There was a problem reading the json file containing JSONMessages: {}", this.getFilePath(), e);
			throw new CommitFileMessageInitException("There was a problem reading the json file containing JSONMessages: " + this.getFilePath(), e);
		}

		logger.debug("Successfully created a CommitFileMessage object: {}", filePath);
	}

	@Override
	public String findIdentifier() {
		return IOUtils.getFileNameWithoutExtension(this.getFilePath());
	}

	/**
	 * Getter for the list of JSONMessage objects contained in the Commit File
	 * Message
	 * 
	 * @return jsonMessages
	 */
	public final List<TypedJSONMessage> getJsonMessages() {
		return Collections.unmodifiableList(this.jsonMessages);
	}

	@Override
	public FileType initFileType() {
		return FileType.JSON;
	}

	/**
	 * Private helper method to read and store the <code>JSONMessage</code>
	 * objects
	 * 
	 * @param jsonObjects
	 * 
	 * @throws JSONException
	 * @throws TypedJSONMessageInitException 
	 * @throws UnknownMessageException 
	 */
	private final void loadMessages(List<JSONObject> jsonObjects) throws JSONException, UnknownMessageException, TypedJSONMessageInitException {

		logger.debug("Loading in messages: {}", this.getFilePath());

		// Messages store has been initialised
		if (this.jsonMessages != null) {

			for (JSONObject jsonObject : jsonObjects) {
				this.jsonMessages.add(MessageFactory.constructMessage(jsonObject));
			}

			logger.debug("Successfully loaded in the messages data file: {}", this.getFilePath());
		}
	}

	@Override
	public String toString() {
		return "CommitFileMessage [jsonMessages=" + this.jsonMessages + ", getFilePath()=" + getFilePath() + ", getIdentifier()=" + getIdentifier() + "]";
	}
}
