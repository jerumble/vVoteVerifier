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
package com.vvote.datafiles.commits.mixrandomcommit;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.FileCommit;
import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.datafiles.exceptions.MixCommitException;
import com.vvote.messages.typed.file.MixRandomCommitMessage;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides storage for both a mixrandomcommit message and its file
 * attachment/submission data held in appropriate format as a
 * <code>RandomnessServerCommits</code> object
 * 
 * @author James Rumble
 * 
 */
public final class MixRandomCommit extends FileCommit {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MixRandomCommit.class);
	
	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Stores the mixrandomcommit message
	 */
	private final MixRandomCommitMessage message;

	/**
	 * Stores the mix server committed data
	 */
	private RandomnessServerCommits serverCommits;

	/**
	 * Filename of the commit data file
	 */
	private final String commitDataFilename;

	/**
	 * Constructor for a <code>MixRandomCommit</code> object taking in the
	 * <code>MixRandomCommitMessage</code> message
	 * 
	 * @param message
	 * @param attachmentFilePath
	 * @param commitDataFilename
	 * @throws FileCommitException
	 * @throws MixCommitException
	 */
	public MixRandomCommit(MixRandomCommitMessage message, String attachmentFilePath, String commitDataFilename) throws FileCommitException, MixCommitException {
		super(attachmentFilePath);
		logger.debug("Creating a new MixRandomCommit object");

		if (message != null) {
			this.message = message;
		} else {
			logger.error("A MixRandomCommit object must be provided with a MixRandomCommitMessage");
			throw new MixCommitException("A MixRandomCommit object must be provided with a MixRandomCommitMessage");
		}

		if (commitDataFilename != null) {
			this.commitDataFilename = commitDataFilename;
		} else {
			logger.error("A MixRandomCommit object must be provided with the name of the commit data file inside the zip to find");
			throw new MixCommitException("A MixRandomCommit object must be provided with the name of the commit data file inside the zip to find");
		}

		if (!this.readZipFile()) {
			logger.error("There was a problem reading the zip file attachment for the current MixRandomCommitMessage object");
			throw new MixCommitException("There was a problem reading the zip file attachment for the current MixRandomCommitMessage object");
		}
	}

	/**
	 * Getter for the <code>MixRandomCommitMessage</code> message
	 * 
	 * @return mixRandomCommitMessage
	 */
	public final MixRandomCommitMessage getMessage() {
		return this.message;
	}

	/**
	 * Getter for the server data
	 * 
	 * @return serverCommits
	 */
	public final RandomnessServerCommits getServerCommits() {
		return this.serverCommits;
	}

	/**
	 * Helper method to read the contents of the file submission data
	 */
	@Override
	public boolean readZipFile() {
		
		try {
			logger.info("Extracting zip file: {}/{}", this.getAttachmentFilePath(), this.message.getFileName());
			
			// get filename from the message
			String outerZip = this.getAttachmentFilePath();
			String extractedOuterZipPath = IOUtils.extractZipFile(outerZip);

			String innerZip = IOUtils.join(extractedOuterZipPath, this.message.getFileName());
			String extractedInnerZipPath = IOUtils.extractZipFile(innerZip);

			String fileToRead = IOUtils.join(extractedInnerZipPath, this.commitDataFilename);

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

			// verify the filesize of the attachment
			long fileSize = new File(innerZip).length();

			if (this.message.getFilesize() != fileSize) {
				logger.error("The zip folder's size did not match the size included in the mix random commit message: {}, expected size: {}, actual size: {}", fileToRead, this.message.getFilesize(), fileSize);
				resultsLogger.error("The zip folder's size did not match the size included in the mix random commit message: {}, expected size: {}, actual size: {}", fileToRead, this.message.getFilesize(), fileSize);
			}
			
			this.serverCommits = new RandomnessServerCommits(this.message.getBoothID(), fileToRead);
			
		} catch (IOException e) {
			logger.error("There was a problem reading the mix commit data from the commits data in the zip file", e);
			return false;
		} catch (MixCommitException e) {
			logger.error("There was a problem reading the mix commit data from the commits data in the zip file", e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "MixRandomCommit [message=" + this.message + ", serverCommits=" + this.serverCommits + ", commitDataFilename=" + this.commitDataFilename + "]";
	}
}
