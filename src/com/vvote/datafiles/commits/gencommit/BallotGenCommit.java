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
package com.vvote.datafiles.commits.gencommit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.FileCommit;
import com.vvote.datafiles.exceptions.BallotGenCommitException;
import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.messages.typed.file.BallotGenCommitMessage;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifierlibrary.utils.comparators.BallotSerialNumberComparator;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a representation for a ballot gen commitment
 * 
 * @author James Rumble
 * 
 */
public final class BallotGenCommit extends FileCommit {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenCommit.class);
	
	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * The ballot gen commit message
	 */
	private final BallotGenCommitMessage message;

	/**
	 * A map of serial number to a committed ballot sent and stored on the
	 * public WBB
	 */
	private final Map<String, CommittedBallot> committedBallots;

	/**
	 * Ciphers data file name
	 */
	private final String ciphersDataFilename;

	/**
	 * The path to the ciphers data file
	 */
	private String ciphersDataFilePath = null;

	/**
	 * Constructor for a ballot gen commit
	 * 
	 * @param message
	 * @param attachmentFilePath
	 * @param ciphersDataFilename
	 * @throws BallotGenCommitException
	 * @throws FileCommitException
	 */
	public BallotGenCommit(BallotGenCommitMessage message, String attachmentFilePath, String ciphersDataFilename) throws BallotGenCommitException, FileCommitException {
		super(attachmentFilePath);
		logger.debug("Creating a new BallotGenCommit object");

		if (message != null) {
			this.message = message;
		} else {
			logger.error("A BallotGenCommit object must be provided with a BallotGenCommitMessage");
			throw new BallotGenCommitException("A BallotGenCommit object must be provided with a BallotGenCommitMessage");
		}

		if (ciphersDataFilename != null) {
			this.ciphersDataFilename = ciphersDataFilename;
		} else {
			logger.error("A BallotGenCommit object must be provided with the name of the file to find inside the zip file");
			throw new BallotGenCommitException("A BallotGenCommit object must be provided with the name of the file to find inside the zip file");
		}

		this.committedBallots = new TreeMap<String, CommittedBallot>(new BallotSerialNumberComparator());

		if (!this.readZipFile()) {
			logger.error("There was a problem reading the zip file attachment for the current BallotGenCommitMessage object");
			throw new BallotGenCommitException("There was a problem reading the zip file attachment for the current BallotGenCommitMessage object");
		}
	}

	/**
	 * Getter for the file path of the ciphers data file
	 * 
	 * @return location of ciphersDataFilePath
	 */
	public final String getCiphersDataFilePath() {
		return this.ciphersDataFilePath;
	}

	/**
	 * Getter for the map of committed ballots
	 * 
	 * @return committedBallots
	 */
	public final Set<String> getCommittedBallotsSerialNumbers() {
		return Collections.unmodifiableSet(this.committedBallots.keySet());
	}

	/**
	 * Getter for the ballot gen commit message
	 * 
	 * @return message
	 */
	public final BallotGenCommitMessage getMessage() {
		return this.message;
	}

	/**
	 * loads in the ciphers computed by POD printers which a number of are to be
	 * verified. There will be many more ciphers present than are to be verified
	 * 
	 * @param filename
	 * @return true if the ciphers are loaded correctly
	 * @throws BallotGenCommitException
	 */
	private boolean loadCommittedCiphers(String filename) throws BallotGenCommitException {

		if (this.committedBallots != null) {
			logger.debug("Loading in the committed ballot ciphers data file: {}", filename);

			String line = null;

			CommittedBallot ballot = null;

			try (BufferedReader committedBallotFileReader = new BufferedReader(new FileReader(filename))) {
				while ((line = committedBallotFileReader.readLine()) != null) {

					ballot = new CommittedBallot(line);

					this.committedBallots.put(ballot.getSerialNo(), null);

					ballot = null;
				}
			} catch (FileNotFoundException e) {
				logger.error("There was a problem reading the file", e);
				return false;
			} catch (IOException e) {
				logger.error("There was a problem reading the file", e);
				return false;
			} catch (JSONException e) {
				logger.error("There was a problem reading the file", e);
				return false;
			}

			logger.debug("Successfully loaded the committed ballot ciphers data file");
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
			logger.info("Extracting zip file: {}/{}", this.getAttachmentFilePath(), this.message.getFileName());
			
			// get filename from the message
			String outerZip = this.getAttachmentFilePath();
			String extractedOuterZipPath = IOUtils.extractZipFile(outerZip);

			String innerZip = IOUtils.join(extractedOuterZipPath, this.message.getFileName());
			String extractedInnerZipPath = IOUtils.extractZipFile(innerZip);

			this.ciphersDataFilePath = IOUtils.join(extractedInnerZipPath, this.ciphersDataFilename);

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
				logger.error("The zip folder's size did not match the size included in the ballot gen commit message: {}, expected size: {}, actual size: {}", this.ciphersDataFilePath, this.message.getFilesize(), fileSize);
				resultsLogger.error("The zip folder's size did not match the size included in the ballot gen commit message: {}, expected size: {}, actual size: {}", this.ciphersDataFilePath, this.message.getFilesize(), fileSize);
			}

			if (!this.loadCommittedCiphers(this.ciphersDataFilePath)) {
				return false;
			}
		} catch (IOException e) {
			logger.error("There was a problem reading the ballot generation data from the commits data in the zip file", e);
			return false;
		} catch (BallotGenCommitException e) {
			logger.error("There was a problem reading the ballot generation data from the commits data in the zip file", e);
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "BallotGenCommit [message=" + this.message + ", committedBallots=" + this.committedBallots + ", ciphersDataFilename=" + this.ciphersDataFilename + ", ciphersDataFilePath="
				+ this.ciphersDataFilePath + "]";
	}

	/**
	 * Gets a specific generic ballot and loads it if has not already been
	 * loaded
	 * 
	 * @param serialNo
	 * @return a specific generic ballot
	 */
	public CommittedBallot getCommittedBallot(String serialNo) {

		logger.debug("Getting committed ballot cipher: {}", serialNo);

		CommittedBallot ballot = null;

		if (this.committedBallots.containsKey(serialNo)) {
			ballot = this.committedBallots.get(serialNo);

			if (ballot == null) {

				logger.debug("Loading committed ballot cipher from file: {}", serialNo);

				try {
					this.loadBallot(serialNo);
				} catch (JSONException | BallotGenCommitException | FileNotFoundException e) {
					logger.error("There was a problem reading the ballot generation data and getting the requested serial number: {}", serialNo);
					return null;
				}
			}

			return this.committedBallots.get(serialNo);
		}
		return null;
	}

	/**
	 * Loads a specific generic ballot from file
	 * 
	 * @param serialNo
	 * @throws JSONException
	 * @throws BallotGenCommitException
	 * @throws FileNotFoundException
	 */
	private void loadBallot(String serialNo) throws JSONException, BallotGenCommitException, FileNotFoundException {

		try (Scanner scanner = new Scanner(new File(this.ciphersDataFilePath))) {

			String line = null;

			CommittedBallot ballot = null;

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.contains(serialNo)) {
					ballot = new CommittedBallot(line);

					this.committedBallots.put(ballot.getSerialNo(), ballot);
					break;
				}
			}

			scanner.close();
		}
	}
}
