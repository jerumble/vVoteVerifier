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
package com.vvote.datafiles.commits.auditcommit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.FileCommit;
import com.vvote.datafiles.exceptions.BallotAuditCommitException;
import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.messages.typed.file.BallotAuditCommitMessage;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a representation for a ballot audit commitment
 * 
 * @author James Rumble
 * 
 */
public final class BallotAuditCommit extends FileCommit {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotAuditCommit.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * The ballot audit commit message
	 */
	private final BallotAuditCommitMessage message;

	/**
	 * The ballot submission response file
	 */
	private BallotSubmitResponse response = null;

	/**
	 * audit data file name
	 */
	private final String auditDataFilename;

	/**
	 * ballot submit response data file name
	 */
	private final String ballotSubmitResponseFilename;

	/**
	 * Holds a map of serialNo : opened commitments to the randomness values
	 * received from each mix server. Each BallotGenerationRandomness object
	 * contains a list of opened commitments therefore each serial number
	 * relates to a list of lists of opened randomness commitments
	 */
	private final Map<String, BallotGenerationRandomness> randomnessCommitments;

	/**
	 * Constructor for a ballot audit commit
	 * 
	 * @param message
	 * @param attachmentFilePath
	 * @param auditDataFilename
	 * @param ballotSubmitResponseFilename
	 * @throws BallotAuditCommitException
	 * @throws FileCommitException
	 */
	public BallotAuditCommit(BallotAuditCommitMessage message, String attachmentFilePath, String auditDataFilename, String ballotSubmitResponseFilename) throws BallotAuditCommitException,
			FileCommitException {
		super(attachmentFilePath);
		logger.debug("Creating a new BallotAuditCommit object");

		if (message != null) {
			this.message = message;
		} else {
			logger.error("A BallotAuditCommit object must be provided with a BallotAuditCommitMessage");
			throw new BallotAuditCommitException("A BallotAuditCommit object must be provided with a BallotAuditCommitMessage");
		}

		if (auditDataFilename != null) {
			this.auditDataFilename = auditDataFilename;
		} else {
			logger.error("A BallotAuditCommit object must be provided with an audit data filename");
			throw new BallotAuditCommitException("A BallotAuditCommit object must be provided with an audit data filename");
		}

		if (ballotSubmitResponseFilename != null) {
			this.ballotSubmitResponseFilename = ballotSubmitResponseFilename;
		} else {
			logger.error("A BallotAuditCommit object must be provided with a ballot submit response filename");
			throw new BallotAuditCommitException("A BallotAuditCommit object must be provided with a ballot submit response filename");
		}

		this.randomnessCommitments = new HashMap<String, BallotGenerationRandomness>();
		if (!this.readZipFile()) {
			logger.error("There was a problem reading the zip file attachment for the current BallotAuditCommitMessage object");
			throw new BallotAuditCommitException("There was a problem reading the zip file attachment for the current BallotAuditCommitMessage object");
		}
	}

	/**
	 * Getter for the ballot audit commit message
	 * 
	 * @return <code>BallotAuditCommitMessage</code>
	 */
	public final BallotAuditCommitMessage getMessage() {
		return this.message;
	}

	/**
	 * Getter for the map containing randomness commitments for ballots
	 * 
	 * @return randomnessCommitments
	 */
	public final Map<String, BallotGenerationRandomness> getRandomnessCommitments() {
		return Collections.unmodifiableMap(this.randomnessCommitments);
	}

	/**
	 * Getter for the ballot submission response
	 * 
	 * @return response
	 */
	public final BallotSubmitResponse getResponse() {
		return this.response;
	}

	/**
	 * Loads in the ballot generation audit file. The ballot generation audit
	 * file contains the opened commitments to the randomness values used for
	 * encryption by the PoD Printers.
	 * 
	 * @param filename
	 * 
	 * @return true if the ballot generation audit file has been loaded
	 *         successfully
	 */
	private boolean loadBallotGenerationAuditFile(String filename) {

		if (this.randomnessCommitments != null) {
			logger.debug("Loading in the ballot generation audit file");

			String line = null;

			// Creates a representation for each line of the file
			BallotGenerationRandomness currentPODRandomnessCommitment = null;

			try (BufferedReader ballotGenerationAuditFileReader = new BufferedReader(new FileReader(filename))) {
				// loop over each line of the ballotsGenAudit file
				while ((line = ballotGenerationAuditFileReader.readLine()) != null) {
					// each line is represented by a BallotGenerationRandomness
					// object
					currentPODRandomnessCommitment = new BallotGenerationRandomness(line);

					// add each BallotGenerationRandomness object for easy
					// access
					this.randomnessCommitments.put(currentPODRandomnessCommitment.getSerialNo(), currentPODRandomnessCommitment);
				}
			} catch (FileNotFoundException e) {
				logger.error("Unable to read the ballot generation audit data", e);
				return false;
			} catch (IOException e) {
				logger.error("Unable to read the ballot generation audit data", e);
				return false;
			} catch (JSONException e) {
				logger.error("Unable to read the ballot generation audit data", e);
				return false;
			} catch (BallotAuditCommitException e) {
				logger.error("Unable to read the ballot generation audit data", e);
				return false;
			}

			logger.debug("Successfully loaded the ballot generation audit file");
			return true;
		}
		return false;
	}

	/**
	 * Helper method to read the contents of the file submission data
	 */
	@Override
	public boolean readZipFile() {

		try {
			// get filename from the message
			String outerZip = this.getAttachmentFilePath();
			String extractedOuterZipPath = IOUtils.extractZipFile(outerZip);

			String innerZip = IOUtils.join(extractedOuterZipPath, this.message.getFileName());
			String extractedInnerZipPath = IOUtils.extractZipFile(innerZip);

			String auditFileToRead = IOUtils.join(extractedInnerZipPath, this.auditDataFilename);
			String ballotSubmitResponseFileToRead = IOUtils.join(extractedInnerZipPath, this.ballotSubmitResponseFilename);

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
				logger.error("The zip folder's size did not match the size included in the ballot audit commit message: {}, expected size: {}, actual size: {}", extractedInnerZipPath,
						this.message.getFilesize(), fileSize);
				resultsLogger.error("The zip folder's size did not match the size included in the ballot audit commit message: {}, expected size: {}, actual size: {}", extractedInnerZipPath,
						this.message.getFilesize(), fileSize);
			}

			if (!this.loadBallotGenerationAuditFile(auditFileToRead)) {
				logger.error("There was a problem reading the audit data from the unzipped directory");
				return false;
			}

			this.response = new BallotSubmitResponse(IOUtils.readJSONObjectFromFile(ballotSubmitResponseFileToRead));

			if (this.response == null) {
				logger.error("There was a problem reading the ballot submit response data file from the commits data in the zip file: {}", ballotSubmitResponseFileToRead);
				return false;
			}

		} catch (BallotAuditCommitException e) {
			logger.error("There was a problem reading the audit data from the commits data in the zip file", e);
			return false;
		} catch (JSONIOException e) {
			logger.error("There was a problem reading the audit data from the commits data in the zip file", e);
			return false;
		} catch (IOException e) {
			logger.error("There was a problem reading the audit data from the commits data in the zip file", e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "BallotAuditCommit [message=" + this.message + ", response=" + this.response + ", auditDataFilename=" + this.auditDataFilename + ", ballotSubmitResponseFilename="
				+ this.ballotSubmitResponseFilename + ", randomnessCommitments=" + this.randomnessCommitments + "]";
	}
}
