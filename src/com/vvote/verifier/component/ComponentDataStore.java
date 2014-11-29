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
package com.vvote.verifier.component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.CommitIdentifier;
import com.vvote.commits.FinalCommitment;
import com.vvote.commits.exceptions.CommitIdentifierException;
import com.vvote.datafiles.DistrictConfig;
import com.vvote.datafiles.DistrictConfigurationFile;
import com.vvote.datafiles.commits.gencommit.BallotGenCommit;
import com.vvote.datafiles.exceptions.BallotGenCommitException;
import com.vvote.datafiles.exceptions.DistrictConfigurationException;
import com.vvote.datafiles.exceptions.FileCommitException;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.typed.file.BallotGenCommitMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.DataStore;
import com.vvote.verifier.component.ballotGen.BallotGenerationConfig;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ConfigException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides storage for all data which is read in from file. All data stored in
 * a <code>ComponentDataStore</code> object will be unchanged after construction
 * and will simply be used to request data
 * 
 * @author James Rumble
 * 
 */
public abstract class ComponentDataStore extends DataStore {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ComponentDataStore.class);

	/**
	 * Each component verifier will have a list of district configurations
	 */
	private DistrictConfigurationFile districtConfig;

	/**
	 * Holds the plaintext candidate ids
	 */
	private List<ECPoint> plaintextIds = null;

	/**
	 * Holds the base encrypted candidate ids
	 */
	private List<ElGamalECPoint> baseEncryptedIds = null;

	/**
	 * ECPoint containing the public key
	 */
	private ECPoint publicKey;

	/**
	 * Holds the generated ciphers by commitment - CommitIdentifier :
	 * BallotGenCommit
	 */
	private Map<CommitIdentifier, BallotGenCommit> generatedCiphers;

	/**
	 * Ballot gen config
	 */
	private BallotGenerationConfig ballotGenerationConfig;

	/**
	 * Holds the number of randomness values that are expected to be received
	 * from the mix servers for ballot generation
	 */
	private int numberOfRandomnessValuesExpected;

	/**
	 * Constructor for a component data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentDataStoreException
	 * @throws DataStoreException
	 */
	public ComponentDataStore(ComponentSpec spec, String basePath, boolean useExtraCommits) throws ComponentDataStoreException, DataStoreException {
		super(spec, basePath, useExtraCommits);

		this.plaintextIds = new ArrayList<ECPoint>();
		this.baseEncryptedIds = new ArrayList<ElGamalECPoint>();
		this.generatedCiphers = new HashMap<CommitIdentifier, BallotGenCommit>();
	}

	@Override
	public boolean readData() {

		logger.debug("Reading in Election data relevant for each verifier");

		boolean result = true;

		if (!this.hasReadData()) {
			result = super.readData();

			if (result) {

				try {
					logger.debug("Reading plaintexts");
					// load in unencrypted candidate ids as EC points
					this.loadPlaintextIds();

					logger.debug("Reading base encrypted ids");
					// load in the base encrypted ids
					this.loadBaseEncryptedIds();

					// setting the public key
					logger.debug("Setting the public key");
					this.publicKey = ECUtils.constructECPointFromJSON(IOUtils.readJSONObjectFromFile(IOUtils.findFile(this.getSpec().getPublicKeyLocation(), this.getBasePath())));

					logger.debug("Reading district config");
					this.districtConfig = new DistrictConfigurationFile(IOUtils.readStringFromFile(IOUtils.findFile(this.getSpec().getDistrictConfig(), this.getBasePath())));

					logger.debug("Reading ballot generation config");
					this.ballotGenerationConfig = new BallotGenerationConfig(IOUtils.readStringFromFile(IOUtils.findFile(this.getSpec().getBallotGenConfig(), this.getBasePath())));

					this.numberOfRandomnessValuesExpected = this.ballotGenerationConfig.getNumberOfCandidates() + 1;

				} catch (FileNotFoundException e) {
					logger.error("Unable to read data.", e);
					return false;
				} catch (DistrictConfigurationException e) {
					logger.error("Unable to read data.", e);
					return false;
				} catch (JSONException e) {
					logger.error("Unable to read data.", e);
					return false;
				} catch (IOException e) {
					logger.error("Unable to read data.", e);
					return false;
				} catch (ConfigException e) {
					logger.error("Unable to read data.", e);
					return false;
				} catch (JSONIOException e) {
					logger.error("Unable to read data.", e);
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	@Override
	public ComponentSpec getSpec() {
		if (super.getSpec() instanceof ComponentSpec) {
			return (ComponentSpec) super.getSpec();
		}
		return null;
	}

	/**
	 * Getter for the list of base encrypted candidate ids
	 * 
	 * @return baseEncryptedIds
	 */
	public List<ElGamalECPoint> getBaseEncryptedIds() {
		return Collections.unmodifiableList(this.baseEncryptedIds);
	}

	/**
	 * Getter for the district configuration store
	 * 
	 * @return districtConfig
	 */
	public final DistrictConfigurationFile getDistrictConfig() {
		return this.districtConfig;
	}

	/**
	 * Gets a specific district configuration by its name
	 * 
	 * @param districtName
	 * @return the district config
	 */
	public DistrictConfig getDistrictConfigByName(String districtName) {
		if (this.districtConfig.getDistricts().containsKey(districtName)) {
			return this.districtConfig.getDistricts().get(districtName);
		}
		return null;
	}

	/**
	 * Getter for the list of plaintext candidate ids
	 * 
	 * @return plaintextIds
	 */
	public List<ECPoint> getPlaintextIds() {
		return Collections.unmodifiableList(this.plaintextIds);
	}

	/**
	 * Getter for the ballotGenerationConfig
	 * 
	 * @return ballotGenerationConfig
	 */
	public BallotGenerationConfig getBallotGenerationConfig() {
		return this.ballotGenerationConfig;
	}

	/**
	 * Getter for the number of randomness values expected
	 * 
	 * @return numberOfRandomnessValuesExpected
	 */
	public int getNumberOfRandomnessValuesExpected() {
		return this.numberOfRandomnessValuesExpected;
	}

	/**
	 * Getter for the <code>ECPoint</code> public key
	 * 
	 * @return publicKey
	 */
	public ECPoint getPublicKey() {
		return this.publicKey;
	}

	/**
	 * Initialise the list of relevant message types
	 * 
	 * @return a list of message types which are relevant for the specific
	 *         component verifier
	 */
	@Override
	public List<MessageType> initialiseListOfRelevantMessages() {
		List<MessageType> relevantTypes = super.initialiseListOfRelevantMessages();
		relevantTypes.add(MessageType.BALLOT_GEN_COMMIT);
		return relevantTypes;
	}

	/**
	 * Loads in the base encrypted candidate ids. The base encrypted candidate
	 * ids are the plaintext ids encrypted under a fixed randomness value of 1
	 * 
	 * @throws JSONIOException
	 * @throws JSONException
	 */
	private void loadBaseEncryptedIds() throws JSONIOException, JSONException {

		if (this.baseEncryptedIds != null) {
			JSONObject currentEncryptedId = null;

			logger.debug("Loading in the base encrypted candidate ids file");

			// reads the base encrypted candidate ids
			JSONArray baseEncryptedCandidateIds = IOUtils.readJSONArrayFromFile(IOUtils.findFile(this.getSpec().getBaseEncryptedCandidateIds(), this.getBasePath()));

			logger.debug("Plaintext ids: {}", baseEncryptedCandidateIds.toString());
			for (int i = 0; i < baseEncryptedCandidateIds.length(); i++) {

				currentEncryptedId = baseEncryptedCandidateIds.getJSONObject(i);

				this.baseEncryptedIds.add(ECUtils.constructElGamalECPointFromJSON(currentEncryptedId));
			}

			logger.debug("Successfully loaded the base encrypted candidate ids file");
		}
	}

	/**
	 * Loads in the plaintext ids for the election. The plaintext ids file
	 * contains the unencrypted plaintext candidate ids which have been selected
	 * from the underlying EC curve used.
	 * 
	 * @throws JSONIOException
	 * @throws JSONException
	 */
	private void loadPlaintextIds() throws JSONIOException, JSONException {

		if (this.plaintextIds != null) {
			JSONObject currentPlaintextId = null;

			logger.debug("Loading in the plaintext ids file");

			// reads the plaintext candidate ids
			JSONArray plaintextCandidateIds = IOUtils.readJSONArrayFromFile(IOUtils.findFile(this.getSpec().getPlaintextCandidateIds(), this.getBasePath()));

			logger.debug("Plaintext ids: {}", plaintextCandidateIds.toString());

			// add the plaintext candidate ids in EC Point form into storage
			for (int i = 0; i < plaintextCandidateIds.length(); i++) {

				currentPlaintextId = plaintextCandidateIds.getJSONObject(i);

				this.plaintextIds.add(ECUtils.constructECPointFromJSON(currentPlaintextId));
			}

			logger.debug("Successfully loaded the plaintext ids file");
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
	@Override
	public boolean addMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) {
		if (!super.addMessage(typedMessage, commitment)) {
			return false;
		}
		
		if (typedMessage.getType().equals(MessageType.BALLOT_GEN_COMMIT)) {
			try {
				this.addBallotGenCommitMessage(typedMessage, commitment);
			} catch (BallotGenCommitException e) {
				logger.error("Unable to add ballot gen commit message");
				return false;
			} catch (FileCommitException e) {
				logger.error("Unable to add ballot gen commit message");
				return false;
			} catch (CommitIdentifierException e) {
				logger.error("Unable to add ballot gen commit message");
				return false;
			}
		}

		return true;
	}

	/**
	 * Adds a ballot gen commit message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws FileCommitException
	 * @throws BallotGenCommitException
	 * @throws CommitIdentifierException
	 */
	private void addBallotGenCommitMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws BallotGenCommitException, FileCommitException, CommitIdentifierException {
		if (typedMessage instanceof BallotGenCommitMessage) {
			BallotGenCommitMessage message = (BallotGenCommitMessage) typedMessage;

			BallotGenCommit commit = new BallotGenCommit(message, commitment.getAttachment().getFilePath(), this.getSpec().getCiphersDataFile());

			String boothID = commit.getMessage().getBoothID();

			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), boothID);

			if (!this.generatedCiphers.containsKey(identifier)) {
				this.generatedCiphers.put(identifier, commit);
			} else {
				throw new BallotGenCommitException("Cannot have another ballot gen commit from the same printer");
			}
		}
	}

	/**
	 * Getter for the generated ciphers
	 * 
	 * @return generatedCiphers
	 */
	public Map<CommitIdentifier, BallotGenCommit> getGeneratedCiphers() {
		return this.generatedCiphers;
	}
}
