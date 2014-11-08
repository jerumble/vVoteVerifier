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
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides an abstract representation of a file message which is used for file
 * submissions to the public wbb. File submissions will generally contain
 * proofs, data etc.
 * 
 * @author James Rumble
 * 
 */
public class FileMessage extends TypedJSONMessage {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileMessage.class);

	/**
	 * The size of the file attached in bytes
	 */
	private final int filesize;

	/**
	 * The submission id of the file message
	 */
	private final String submissionId;

	/**
	 * The digest_ of the file message
	 */
	private final String _digest;

	/**
	 * The digest of the file message
	 */
	private final String digest;

	/**
	 * The name of the attachment file for the message
	 */
	private final String fileName;

	/**
	 * Constructor for a <code>FileMessage</code> object
	 * 
	 * @param json
	 * @throws TypedJSONMessageInitException
	 * @throws JSONMessageInitException
	 * @throws FileMessageInitException
	 */
	public FileMessage(JSONObject json) throws TypedJSONMessageInitException, JSONMessageInitException, FileMessageInitException {
		super(json);

		try {
			// get and set the filesize
			if (json.has(MessageFields.FileMessage.FILE_SIZE)) {
				this.filesize = json.getInt(MessageFields.FileMessage.FILE_SIZE);
			} else {
				logger.error("filesize of a FileMessage must be specified");
				throw new FileMessageInitException("filesize of a FileMessage must be specified");
			}

			// get and set the submission id
			if (json.has(MessageFields.FileMessage.SUBMISSION_ID)) {
				this.submissionId = json.getString(MessageFields.FileMessage.SUBMISSION_ID);
			} else {
				logger.error("The submissionId for a FileMessage must be specified");
				throw new FileMessageInitException("The submissionId for a FileMessage must be specified");
			}

			// get and set the _digest
			if (json.has(MessageFields.FileMessage._DIGEST)) {
				this._digest = json.getString(MessageFields.FileMessage._DIGEST);
			} else {
				logger.error("The digest_ for a FileMessage must be specified");
				throw new FileMessageInitException("The digest_ for a FileMessage must be specified");
			}

			// get and set the digest
			if (json.has(MessageFields.FileMessage.DIGEST)) {
				this.digest = json.getString(MessageFields.FileMessage.DIGEST);
			} else {
				logger.error("The digest for a FileMessage must be specified");
				throw new FileMessageInitException("The digest for a FileMessage must be specified");
			}

			// get and set the fileName
			if (json.has(MessageFields.FileMessage.FILE_NAME)) {
				this.fileName = json.getString(MessageFields.FileMessage.FILE_NAME);
			} else {
				logger.error("The fileName for a FileMessage must be specified");
				throw new FileMessageInitException("The fileName for a FileMessage must be specified");
			}

			if (!this.digest.equals(this._digest)) {
				logger.error("The _digest and digest for a FileMessage must be the same");
				throw new FileMessageInitException("The _digest and digest for a FileMessage must be the same");
			}
		} catch (JSONException e) {
			logger.error("Unable to create a FileMessage. Error: {}", e);
			throw new FileMessageInitException("Unable to create a FileMessage.", e);
		}
	}

	/**
	 * Getter for the _digest of the message
	 * 
	 * @return _digest
	 */
	public final String get_digest() {
		return this._digest;
	}

	/**
	 * Getter for the digest of the message
	 * 
	 * @return digest
	 */
	public final String getDigest() {
		return this.digest;
	}

	/**
	 * Getter for the filename of the message
	 * 
	 * @return fileName
	 */
	public final String getFileName() {
		return this.fileName;
	}

	/**
	 * Getter for the size of the attachment
	 * 
	 * @return filesize
	 */
	public final int getFilesize() {
		return this.filesize;
	}

	/**
	 * Getter for the submission id
	 * 
	 * @return submissionId
	 */
	public final String getSubmissionId() {
		return this.submissionId;
	}
	
	@Override
	public String getInternalSignableContent(){
	    StringBuilder internalSignableContent = new StringBuilder();
	    internalSignableContent.append(this.getSubmissionId());
	    internalSignableContent.append(this.getDigest());
	    internalSignableContent.append(this.getBoothID());
	    internalSignableContent.append(this.getCommitTime());
	    return internalSignableContent.toString();
	}

	@Override
	public JSONSchema getSchema() {
		return JSONSchema.FILE_COMMIT_SCHEMA;
	}
}
