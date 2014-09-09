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
package com.vvote.verifier.component.ballotGen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.CommitIdentifier;
import com.vvote.commits.FinalCommitment;
import com.vvote.commits.exceptions.CommitIdentifierException;
import com.vvote.datafiles.commits.auditcommit.BallotAuditCommit;
import com.vvote.datafiles.commits.mixrandomcommit.MixRandomCommit;
import com.vvote.datafiles.exceptions.BallotAuditCommitException;
import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.datafiles.exceptions.MixCommitException;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.typed.file.BallotAuditCommitMessage;
import com.vvote.messages.typed.file.MixRandomCommitMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.component.ComponentDataStore;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;

/**
 * Specific data store object for the ballot generation data
 * 
 * @author James Rumble
 * 
 */
public class BallotGenDataStore extends ComponentDataStore {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenDataStore.class);

	/**
	 * Holds the mix server commits - Server name: Map<CommitIdentifier : Mix
	 * Random Commit>
	 */
	private final Map<String, Map<CommitIdentifier, List<MixRandomCommit>>> mixServerCommits;

	/**
	 * Holds the audit data - CommitIdentifier : BallotAuditCommit
	 */
	private final Map<CommitIdentifier, BallotAuditCommit> auditData;

	/**
	 * Constructor for a ballot gen data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentDataStoreException
	 * @throws DataStoreException
	 */
	public BallotGenDataStore(BallotGenerationVerifierSpec spec, String basePath, boolean useExtraCommits) throws ComponentDataStoreException, DataStoreException {
		super(spec, basePath, useExtraCommits);
		logger.debug("Constructing Ballot Generation Data Store");

		this.mixServerCommits = new HashMap<String, Map<CommitIdentifier, List<MixRandomCommit>>>();
		this.auditData = new HashMap<CommitIdentifier, BallotAuditCommit>();
	}

	/**
	 * Constructor for a ballot gen data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentDataStoreException
	 * @throws ComponentSpecException
	 * @throws SpecException
	 * @throws DataStoreException
	 */
	public BallotGenDataStore(JSONObject spec, String basePath, boolean useExtraCommits) throws ComponentDataStoreException, ComponentSpecException, DataStoreException, SpecException {
		this(new BallotGenerationVerifierSpec(spec), basePath, useExtraCommits);
	}

	@Override
	public List<MessageType> initialiseListOfRelevantMessages() {

		List<MessageType> relevantTypes = super.initialiseListOfRelevantMessages();
		relevantTypes.add(MessageType.MIX_RANDOM_COMMIT);
		relevantTypes.add(MessageType.BALLOT_AUDIT_COMMIT);
		return relevantTypes;
	}

	@Override
	public BallotGenerationVerifierSpec getSpec() {
		if (super.getSpec() instanceof BallotGenerationVerifierSpec) {
			return (BallotGenerationVerifierSpec) super.getSpec();
		}
		return null;
	}

	/**
	 * Adds an audit commit message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws FileCommitException
	 * @throws BallotAuditCommitException
	 * @throws CommitIdentifierException
	 */
	private void addAuditCommitMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws BallotAuditCommitException, FileCommitException, CommitIdentifierException {
		if (typedMessage instanceof BallotAuditCommitMessage) {
			BallotAuditCommitMessage message = (BallotAuditCommitMessage) typedMessage;

			BallotAuditCommit commit = new BallotAuditCommit(message, commitment.getAttachment().getFilePath(), this.getSpec().getAuditDataFile(), this.getSpec().getBallotSubmitResponse());

			String boothID = commit.getMessage().getBoothID();

			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), boothID);

			if (!this.auditData.containsKey(identifier)) {
				this.auditData.put(identifier, commit);
			} else {
				throw new BallotAuditCommitException("Cannot have another ballot audit commit from the same printer");
			}
		}
	}

	/**
	 * Adds a <code>TypedJSONMessage</code> from the
	 * <code>FinalCommitment</code> object
	 * 
	 * @param typedMessage
	 * @param commitment
	 */
	@Override
	public boolean addMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) {
		if (!super.addMessage(typedMessage, commitment)) {
			return false;
		}

		try {
			if (typedMessage.getType().equals(MessageType.MIX_RANDOM_COMMIT)) {
				this.addMixRandomCommitMessage(typedMessage, commitment);
			} else if (typedMessage.getType().equals(MessageType.BALLOT_AUDIT_COMMIT)) {
				this.addAuditCommitMessage(typedMessage, commitment);
			}
		} catch (CommitIdentifierException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		} catch (FileCommitException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		} catch (MixCommitException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		} catch (BallotAuditCommitException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		}

		return true;
	}

	/**
	 * Adds a mix random commit message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws CommitIdentifierException
	 * @throws MixCommitException
	 * @throws FileCommitException
	 */
	private void addMixRandomCommitMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws CommitIdentifierException, FileCommitException, MixCommitException {
		if (typedMessage instanceof MixRandomCommitMessage) {
			MixRandomCommitMessage message = (MixRandomCommitMessage) typedMessage;

			MixRandomCommit commit = new MixRandomCommit(message, commitment.getAttachment().getFilePath(), this.getSpec().getCommitDataFile());

			List<MixRandomCommit> randomCommits = null;

			// server name
			String boothID = commit.getMessage().getBoothID();

			// printer id
			String printerID = commit.getMessage().getPrinterID();

			logger.debug("PrinterID: {}, boothID: {}, commitment: {}", printerID, boothID, commitment.getIdentifier());

			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), printerID);

			Map<CommitIdentifier, List<MixRandomCommit>> serverMap = null;

			if (this.mixServerCommits.containsKey(boothID)) {
				serverMap = this.mixServerCommits.get(boothID);
				if (serverMap.containsKey(identifier)) {

					randomCommits = serverMap.get(identifier);
					randomCommits.add(commit);

					serverMap.remove(identifier);
					serverMap.put(identifier, randomCommits);
				} else {
					randomCommits = new ArrayList<MixRandomCommit>();
					randomCommits.add(commit);
					serverMap.put(identifier, randomCommits);
				}
			} else {
				serverMap = new HashMap<CommitIdentifier, List<MixRandomCommit>>();
				randomCommits = new ArrayList<MixRandomCommit>();
				randomCommits.add(commit);
				serverMap.put(identifier, randomCommits);
				this.mixServerCommits.put(boothID, serverMap);
			}
		}
	}

	/**
	 * Gets the serial numbers for all audited ballot
	 * 
	 * @return a set of the serial numbers for audited ballot
	 */
	public Set<String> getAudittedBallotsSerialNumbers() {
		Set<String> serialNumbers = new HashSet<String>();

		BallotAuditCommit auditCommit = null;

		for (CommitIdentifier identifier : this.getAuditData().keySet()) {
			auditCommit = this.getAuditData().get(identifier);
			serialNumbers.addAll(auditCommit.getRandomnessCommitments().keySet());
		}

		return Collections.unmodifiableSet(serialNumbers);
	}

	/**
	 * Getter for the audit data
	 * 
	 * @return auditData
	 */
	public Map<CommitIdentifier, BallotAuditCommit> getAuditData() {
		return Collections.unmodifiableMap(this.auditData);
	}

	/**
	 * Getter for the mix server commits data
	 * 
	 * @return mixServerCommits
	 */
	public Map<String, Map<CommitIdentifier, List<MixRandomCommit>>> getMixServerCommits() {
		return Collections.unmodifiableMap(this.mixServerCommits);
	}

	@Override
	public boolean readData() {
		
		logger.info("Reading in Election data to verify the Ballot Generation process");

		boolean result = true;

		if (!this.hasReadData()) {
			result = super.readData();
		}

		return result;
	}
}
