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
package com.vvote.datafiles;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.messages.typed.file.FileMessage;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Abstract class for a committed file
 * 
 * @author James Rumble
 * 
 */
public class FileCommit {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileCommit.class);

	/**
	 * Attachment file location
	 */
	private final String attachmentFilePath;

	/**
	 * The file commit message
	 */
	private FileMessage message;

	/**
	 * The mix data file path - used to be the new base path
	 */
	private String mixDataPath;

	/**
	 * Constructor for a FileCommit
	 * 
	 * @param attachmentFilePath
	 * @throws FileCommitException
	 */
	public FileCommit(String attachmentFilePath) throws FileCommitException {
		if (attachmentFilePath != null) {
			this.attachmentFilePath = attachmentFilePath;
		} else {
			logger.error("A FileCommit object must be provided with an attachment path");
			throw new FileCommitException("A FileCommit object must be provided with an attachment path");
		}
	}

	/**
	 * Constructor for a FileCommit
	 * 
	 * @param message
	 * @param attachmentFilePath
	 * @throws FileCommitException
	 */
	public FileCommit(FileMessage message, String attachmentFilePath) throws FileCommitException {

		if (message != null) {
			this.message = message;
		} else {
			logger.error("A FileCommit object must be provided with a FileCommit Message");
			throw new FileCommitException("A FileCommit object must be provided with a FileCommit Message");
		}

		if (attachmentFilePath != null) {
			this.attachmentFilePath = attachmentFilePath;
		} else {
			logger.error("A FileCommit object must be provided with an attachment path");
			throw new FileCommitException("A FileCommit object must be provided with an attachment path");
		}

		if (!this.readZipFile()) {
			logger.error("There was a problem reading the zip file attachment for the current File Commit object");
			throw new FileCommitException("There was a problem reading the zip file attachment for the current File Commit object");
		}
	}

	/**
	 * Getter for the attachment file path
	 * 
	 * @return attachmentFilePath
	 */
	public String getAttachmentFilePath() {
		return this.attachmentFilePath;
	}

	/**
	 * Getter for the mix data path
	 * 
	 * @return the mix data path
	 */
	public String getMixDataPath() {
		return this.mixDataPath;
	}

	/**
	 * All file commits will need to read their zip file
	 * 
	 * @return true if the file was read successfully
	 */
	public boolean readZipFile() {
		try {
			// get filename from the message
			String outerZip = this.getAttachmentFilePath();
			String extractedOuterZipPath = IOUtils.extractZipFile(outerZip);

			String innerZip = IOUtils.join(extractedOuterZipPath, this.message.getFileName());
			String extractedInnerZipPath = IOUtils.extractZipFile(innerZip);

			// check the extension of the filename
			if (!IOUtils.checkExtension(FileType.ZIP, outerZip)) {
				logger.error("There was a problem with the zip folder: {}", outerZip);
				return false;
			}

			// check the extension of the filename
			if (!IOUtils.checkExtension(FileType.ZIP, innerZip)) {
				logger.error("There was a problem with the zip folder: {}", innerZip);
				return false;
			}
			
			this.mixDataPath = extractedInnerZipPath;
			
		} catch (IOException e) {
			logger.error("There was a problem reading the file commit data from the commits data in the zip file", e);
			return false;
		}

		return true;
	}
}
