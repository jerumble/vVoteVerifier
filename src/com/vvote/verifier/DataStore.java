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
package com.vvote.verifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.cert.CertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.CommitAttachment;
import com.vvote.commits.CommitFileMessage;
import com.vvote.commits.CommitSignature;
import com.vvote.commits.FinalCommitment;
import com.vvote.commits.exceptions.CommitAttachmentInitException;
import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.commits.exceptions.CommitFileMessageInitException;
import com.vvote.commits.exceptions.CommitSignatureInitException;
import com.vvote.commits.exceptions.FinalCommitInitException;
import com.vvote.datafiles.wbb.CertificatesFile;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides storage for all data which is read in from file. All data stored in
 * a <code>DataStore</code> object will be unchanged after construction and will
 * simply be used to request data
 * 
 * @author James Rumble
 * 
 */
public abstract class DataStore {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DataStore.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Each component data store will have its own component specification
	 * object
	 */
	private final Spec spec;

	/**
	 * The base path of the data provided - this data will originate from the
	 * public web bulletin board
	 */
	private final String basePath;

	/**
	 * A map of the final commitments. Identifier : FinalCommitment
	 */
	private Map<String, FinalCommitment> finalCommitments;

	/**
	 * A list of the relevant message types for the component
	 */
	private List<MessageType> relevantMessageTypes;

	/**
	 * Holds the certificates file
	 */
	private CertificatesFile certificatesFile;

	/**
	 * Flag for whether the data has been read
	 */
	private boolean readData = false;

	/**
	 * Whether to use final or extra commits folders
	 */
	private boolean useExtraCommits = false;

	/**
	 * Getter for whether the extra commits folder is used
	 * 
	 * @return useExtraCommits
	 */
	public boolean isUseExtraCommits() {
		return this.useExtraCommits;
	}

	/**
	 * Constructor for a data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws DataStoreException
	 */
	public DataStore(Spec spec, String basePath, boolean useExtraCommits) throws DataStoreException {

		if (spec == null) {
			logger.error("A DataStore must be provided to a Spec");
			throw new DataStoreException("A DataStore object must be provided to a Spec");
		}

		if (basePath == null) {
			logger.error("A base path must be provided to a DataStore");
			throw new DataStoreException("A base path must be provided to a DataStore");
		}

		this.spec = spec;
		this.basePath = basePath;

		this.useExtraCommits = useExtraCommits;

		this.finalCommitments = new HashMap<String, FinalCommitment>();
	}

	/**
	 * Performs the reading in from file and organisation of commitment data
	 * taken from the public wbb
	 * 
	 * @return true if the data was read in successfully
	 */
	public boolean readData() {

		logger.debug("Reading in Election commitment data relevant for each verifier");

		if (!this.readData) {
			try {
				this.relevantMessageTypes = this.initialiseListOfRelevantMessages();
				this.readCommitsFolder();
				this.certificatesFile = new CertificatesFile(IOUtils.readStringFromFile(IOUtils.findFile(this.getSpec().getCertsFile(), this.getBasePath())));

			} catch (JSONException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (CertException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (FileNotFoundException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (IOException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (JSONIOException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (CommitAttachmentInitException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (CommitFileInitException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (FinalCommitInitException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (CommitSignatureInitException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (CommitFileMessageInitException e) {
				logger.error("Unable to read data.", e);
				return false;
			} catch (DataStoreException e) {
				logger.error("Unable to read data.", e);
				return false;
			}

			if (this.finalCommitments == null) {
				logger.warn("There is no commitment data available: {}", IOUtils.findFile(this.spec.getFinalCommitsFolder(), this.basePath));
				resultsLogger.warn("There is no commitment data available: {}", IOUtils.findFile(this.spec.getFinalCommitsFolder(), this.basePath));
			}

			if (!this.organiseCommits()) {
				return false;
			}

			this.readData = true;
		}

		return true;
	}

	/**
	 * Helper method to add an attachment
	 * 
	 * @param identifier
	 * @param attachment
	 * @throws FinalCommitInitException
	 */
	private void addAttachment(String identifier, CommitAttachment attachment) throws FinalCommitInitException {
		FinalCommitment commitment = null;

		if (this.finalCommitments.containsKey(identifier)) {
			commitment = this.finalCommitments.get(identifier);
			commitment.setAttachment(attachment);
		} else {
			commitment = new FinalCommitment(attachment);
			this.finalCommitments.put(commitment.getIdentifier(), commitment);
		}
	}

	/**
	 * Helper method to add a file message
	 * 
	 * @param identifier
	 * @param fileMessage
	 * @throws FinalCommitInitException
	 */
	private void addFileMessage(String identifier, CommitFileMessage fileMessage) throws FinalCommitInitException {
		FinalCommitment commitment = null;

		if (this.finalCommitments.containsKey(identifier)) {
			commitment = this.finalCommitments.get(identifier);
			commitment.setFileMessage(fileMessage);
		} else {
			commitment = new FinalCommitment(fileMessage);
			this.finalCommitments.put(commitment.getIdentifier(), commitment);
		}
	}

	/**
	 * Adds a <code>TypedJSONMessage</code> from the
	 * <code>FinalCommitment</code> object
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @return whether the message was added successfully
	 */
	public abstract boolean addMessage(TypedJSONMessage typedMessage, FinalCommitment commitment);

	/**
	 * Helper method to add a signature
	 * 
	 * @param identifier
	 * @param signature
	 * @throws FinalCommitInitException
	 */
	private void addSignature(String identifier, CommitSignature signature) throws FinalCommitInitException {
		FinalCommitment commitment = null;

		if (this.finalCommitments.containsKey(identifier)) {
			commitment = this.finalCommitments.get(identifier);
			commitment.setSignature(signature);
		} else {
			commitment = new FinalCommitment(signature);
			this.finalCommitments.put(commitment.getIdentifier(), commitment);
		}
	}

	/**
	 * Getter for the base path of the data provided
	 * 
	 * @return basePath
	 */
	public final String getBasePath() {
		return this.basePath;
	}

	/**
	 * Getter for the certs file
	 * 
	 * @return certificatesFile
	 */
	public CertificatesFile getCertificatesFile() {
		return this.certificatesFile;
	}

	/**
	 * Getter for the map of final commitments made to the public wbb
	 * 
	 * @return finalCommitments
	 */
	public final Map<String, FinalCommitment> getFinalCommitments() {
		return Collections.unmodifiableMap(this.finalCommitments);
	}

	/**
	 * Getter for the relevant message types for the specific component verifier
	 * 
	 * @return relevantMessageTypes
	 */
	public final List<MessageType> getRelevantMessageTypes() {
		return Collections.unmodifiableList(this.relevantMessageTypes);
	}

	/**
	 * Getter for the spec file for the specific verifier
	 * 
	 * @return spec
	 */
	public Spec getSpec() {
		return this.spec;
	}

	/**
	 * Initialise the list of relevant message types
	 * 
	 * @return a list of message types which are relevant for the specific
	 *         component verifier
	 */
	public abstract List<MessageType> initialiseListOfRelevantMessages();

	/**
	 * Each component verifier will want to organise the commit data into
	 * formats which make sense
	 * 
	 * @return true if the commitments were organised successfully
	 */
	private boolean organiseCommits() {

		logger.debug("Organising commits data");

		logger.debug("Only need to look at messages: {}", this.getRelevantMessageTypes());

		FinalCommitment commitment = null;

		for (String identifier : this.getFinalCommitments().keySet()) {
			logger.debug("Organising commits data with identifier: {}", identifier);

			commitment = this.getFinalCommitments().get(identifier);

			for (TypedJSONMessage typedMessage : commitment.getFileMessage().getJsonMessages()) {
				if (this.getRelevantMessageTypes() != null) {
					if (this.getRelevantMessageTypes().contains(typedMessage.getType())) {
						if (!this.addMessage(typedMessage, commitment)) {
							logger.error("Unable to add message: {}", typedMessage);
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	/**
	 * Reads in the commits folder
	 * 
	 * @throws JSONException
	 * @throws JSONIOException
	 * @throws CommitFileInitException
	 * @throws CommitAttachmentInitException
	 * @throws FinalCommitInitException
	 * @throws CommitSignatureInitException
	 * @throws CommitFileMessageInitException
	 * @throws DataStoreException
	 */
	private void readCommitsFolder() throws JSONException, JSONIOException, CommitAttachmentInitException, CommitFileInitException, FinalCommitInitException, CommitSignatureInitException,
			CommitFileMessageInitException, DataStoreException {

		File commitsLocation = null;

		if (this.useExtraCommits) {
			commitsLocation = new File(IOUtils.findFile(this.spec.getExtraCommitsFolder(), this.basePath));
		} else {
			commitsLocation = new File(IOUtils.findFile(this.spec.getFinalCommitsFolder(), this.basePath));
		}

		logger.debug("Commits folder data: {}", commitsLocation);

		// check whether the provided directory is valid
		if (!commitsLocation.isDirectory()) {
			logger.error("The final commits data must be a directory: {}", commitsLocation);
			throw new DataStoreException("The final commits data must be a directory: " + commitsLocation);
		}

		CommitFileMessage fileMessage = null;
		CommitAttachment attachment = null;
		CommitSignature signature = null;

		String identifier = null;

		String filePath = null;

		// loop over each file in the directory
		for (File file : commitsLocation.listFiles()) {

			filePath = IOUtils.join(commitsLocation.toString(), file.getName());

			// add attachment
			if (IOUtils.checkExtension(FileType.ZIP, file.getName())) {

				attachment = new CommitAttachment(filePath);
				identifier = attachment.getIdentifier();

				this.addAttachment(identifier, attachment);
			} else // add signature
			if (IOUtils.checkExtension(FileType.JSON, file.getName()) && file.getName().contains("_signature")) {

				signature = new CommitSignature(filePath);
				identifier = signature.getIdentifier();

				this.addSignature(identifier, signature);
			} else // add file message
			if (IOUtils.checkExtension(FileType.JSON, file.getName())) {

				fileMessage = new CommitFileMessage(filePath);
				identifier = fileMessage.getIdentifier();

				this.addFileMessage(identifier, fileMessage);
			}
		}

		for (FinalCommitment currentCommitment : this.finalCommitments.values()) {

			if (currentCommitment.getFileMessage() == null || currentCommitment.getAttachment() == null || currentCommitment.getSignature() == null) {
				logger.error("Commitment doesn't contain a FileMessage, Attachment and Signature: {}", currentCommitment);
			}
		}
	}

	/**
	 * Getter for whether the data has been read for a datastore
	 * 
	 * @return readData
	 */
	public boolean hasReadData() {
		return this.readData;
	}
}
