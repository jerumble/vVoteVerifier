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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.commits.exceptions.CommitSignatureInitException;
import com.vvote.messages.SignatureMessage;
import com.vvote.messages.exceptions.JSONMessageInitException;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * @author James Rumble
 * 
 */
public final class CommitSignature extends CommitFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitSignature.class);

	/**
	 * The signature message contained within the commit signature
	 */
	private final SignatureMessage signatureMessage;

	/**
	 * Constructor for a <code>CommitSignature</code> object taking as input the
	 * filename
	 * 
	 * @param filePath
	 * @throws CommitSignatureInitException
	 * @throws CommitFileInitException
	 */
	public CommitSignature(String filePath) throws CommitSignatureInitException, CommitFileInitException {
		super(filePath);

		logger.debug("Creating a commit signature object: {}", filePath);

		if (!this.verifyExtension()) {
			logger.error("A Commit attachment must be a .zip file");
			throw new CommitSignatureInitException("A Commit signature must be a .json file");
		}

		// read in the signature message
		try {
			try {
				this.signatureMessage = new SignatureMessage(IOUtils.readJSONObjectFromFile(filePath));
			} catch (JSONMessageInitException e) {
				logger.error("Unable to create SignatureMessage: {}", this.getFilePath(), e);
				throw new CommitSignatureInitException("Unable to create SignatureMessage: " + this.getFilePath(), e);
			}
		} catch (JSONIOException e) {
			logger.error("There was a problem reading the json file containing the commitment signature: {}", this.getFilePath(), e);
			throw new CommitSignatureInitException("There was a problem reading the commitment signature: " + this.getFilePath(), e);
		}

		logger.debug("Signature message created successfully: {}", this.signatureMessage);

		logger.debug("Successfully created a commit signature object: {}", filePath);
	}

	@Override
	public String findIdentifier() {
		if (this.getFilePath().contains(CommitFileNames.SIGNATURE_NAME.getFileName())) {
			return IOUtils.getFileNameWithoutExtension(this.getFilePath()).replace(CommitFileNames.SIGNATURE_NAME.getFileName(), "");
		}
		return IOUtils.getFileNameWithoutExtension(this.getFilePath());
	}

	/**
	 * Getter for the signature message
	 * 
	 * @return signatureMessage
	 */
	public final SignatureMessage getSignatureMessage() {
		return this.signatureMessage;
	}

	@Override
	public FileType initFileType() {
		return FileType.JSON;
	}

	@Override
	public String toString() {
		return "CommitSignature [signatureMessage=" + this.signatureMessage + ", getFilePath()=" + getFilePath() + ", getIdentifier()=" + getIdentifier() + "]";
	}
}
