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
package com.vvote.verifier.component.votePacking;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.CommitIdentifier;
import com.vvote.commits.FinalCommitment;
import com.vvote.commits.exceptions.CommitIdentifierException;
import com.vvote.datafiles.RaceMapFile;
import com.vvote.datafiles.commits.votes.VotingProcess;
import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.datafiles.exceptions.RaceMapException;
import com.vvote.datafiles.exceptions.VoteMessageCommitException;
import com.vvote.datafiles.mix.MixOutput;
import com.vvote.datafiles.mix.RaceIdentifier;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.typed.vote.CancelMessage;
import com.vvote.messages.typed.vote.PODMessage;
import com.vvote.messages.typed.vote.VoteMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.component.ComponentDataStore;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.ConfigException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifierlibrary.exceptions.ASN1Exception;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.comparators.BallotSerialNumberComparator;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;
import com.vvote.verifierlibrary.utils.io.ASN1ToJSONConverter;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Specific data store object for the vote packing data
 * 
 * @author James Rumble
 * 
 */
public class VotePackingDataStore extends ComponentDataStore {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VotePackingDataStore.class);
	
	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * vote packing config
	 */
	private VotePackingConfig votePackingConfig;

	/**
	 * Holds the path to the mix input data
	 */
	private String mixInputPath;

	/**
	 * Holds the path to the mix output data
	 */
	private String mixOutputPath;

	/**
	 * Holds a list of cancelled ballots
	 */
	private Map<CommitIdentifier, Map<String, CancelMessage>> cancelMessages;

	/**
	 * Holds a list of vote messages
	 */
	private Map<CommitIdentifier, Map<String, VoteMessage>> voteMessages;

	/**
	 * Holds a list of pod messages
	 */
	private Map<CommitIdentifier, Map<String, PODMessage>> podMessages;

	/**
	 * Holds a list of related pod and vote messages
	 */
	private Map<String, VotingProcess> votingProcesses;

	/**
	 * mixnet input data
	 */
	private Map<RaceIdentifier, List<List<ElGamalECPoint>>> mixInput = null;

	/**
	 * mixnet output data
	 */
	private Map<RaceIdentifier, List<List<ECPoint>>> mixOutput = null;

	/**
	 * mixnet output preferences
	 */
	private Map<RaceIdentifier, MixOutput> mixOutputPreferences = null;

	/**
	 * The unencrypted padding point
	 */
	private ECPoint paddingPoint = null;

	/**
	 * The encrypted padding point
	 */
	private ElGamalECPoint encryptedPaddingPoint = null;

	/**
	 * The race map
	 */
	private RaceMapFile raceMap = null;

	/**
	 * Flag for whether the race map has been provided
	 */
	private boolean hasRaceMap = false;

	/**
	 * Constructor for a ballot gen data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentDataStoreException
	 * @throws DataStoreException
	 */
	public VotePackingDataStore(VotePackingVerifierSpec spec, String basePath, boolean useExtraCommits) throws ComponentDataStoreException, DataStoreException {
		super(spec, basePath, useExtraCommits);

		logger.debug("Constructing VotePacking Data Store");

		this.voteMessages = new HashMap<CommitIdentifier, Map<String, VoteMessage>>();
		this.cancelMessages = new HashMap<CommitIdentifier, Map<String, CancelMessage>>();
		this.podMessages = new HashMap<CommitIdentifier, Map<String, PODMessage>>();
		this.votingProcesses = new TreeMap<String, VotingProcess>(new BallotSerialNumberComparator());
	}

	@Override
	public boolean readData() {

		logger.info("Reading in Election data to verify the Vote Packing process");

		boolean result = true;

		if (!this.hasReadData()) {
			result = super.readData();

			if (result) {
				try {
					this.mixInputPath = IOUtils.findFile(this.getSpec().getMixInputFolder(), this.getBasePath());
					this.mixOutputPath = IOUtils.findFile(this.getSpec().getMixOutputFolder(), this.getBasePath());

					try {
						this.votePackingConfig = new VotePackingConfig(IOUtils.findFile(getSpec().getVotePackingConfig(), this.getBasePath()));
					} catch (ConfigException e) {
						logger.error("Unable to create a read data.", e);
						return false;
					}

					String raceMapPath = IOUtils.findFile(this.getSpec().getRaceMap(), this.getBasePath());

					if (raceMapPath != null) {
						this.raceMap = new RaceMapFile(raceMapPath);
						this.hasRaceMap = true;
					}

					this.loadMixOutputPreferences();

					this.loadPaddingPoints();

					this.loadMixInputData();

					this.loadMixOutputData();

				} catch (MixDataException e) {
					logger.error("Unable to create a read data.", e);
					return false;
				} catch (JSONException e) {
					logger.error("Unable to create a read data.", e);
					return false;
				} catch (JSONIOException e) {
					logger.error("Unable to create a read data.", e);
					return false;
				} catch (ASN1Exception e) {
					logger.error("Unable to create a read data.", e);
					return false;
				} catch (RaceMapException e) {
					logger.error("Unable to create a read data.", e);
					return false;
				}

				if (!this.organiseVoteMessages()) {
					logger.error("Unable to create a read data.");
					return false;
				}
				
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets the race map file
	 * 
	 * @return raceMap
	 */
	public RaceMapFile getRaceMap() {
		return this.raceMap;
	}

	/**
	 * Load the mixnet output data
	 * 
	 * @throws JSONException
	 * @throws MixDataException
	 * @throws ASN1Exception
	 * @throws JSONIOException
	 */
	private void loadMixOutputData() throws JSONException, MixDataException, ASN1Exception, JSONIOException {
		this.mixOutput = new HashMap<RaceIdentifier, List<List<ECPoint>>>();

		final File mixOutputDirectory = new File(this.mixOutputPath);

		logger.debug("Loading in the mix output data from the folder: {}", mixOutputDirectory);

		// check whether the provided directory is valid
		if (!mixOutputDirectory.isDirectory()) {
			logger.error("The mix output folder must be a directory: {}", mixOutputDirectory);
			throw new MixDataException("The mix output folder must be a directory: " + mixOutputDirectory);
		}

		String jsonFile = null;

		RaceIdentifier currentIdentifier = null;

		JSONArray mixOutputArray = null;

		JSONArray plaintexts = null;

		List<ECPoint> currentIds = null;

		List<List<ECPoint>> currentFileIds = null;

		// loop over each file in the directory
		for (File file : mixOutputDirectory.listFiles()) {

			logger.debug("Current file in mix output directory: {}", file.getName());

			if (IOUtils.checkExtension(FileType.MIX_OUTPUT, file.getName())) {

				// get equivalent json filename
				jsonFile = IOUtils.addExtension(FilenameUtils.removeExtension(file.getPath()), FileType.JSON);

				// convert from asn.1 to json format
				ASN1ToJSONConverter.asn1ToJSON(file.getPath(), jsonFile, FileType.MIX_OUTPUT);

				// get current identifier
				currentIdentifier = Utils.getRaceIdentifierFromFileName(jsonFile, this.hasRaceMap);

				mixOutputArray = IOUtils.readJSONArrayFromFile(jsonFile);

				currentFileIds = new ArrayList<List<ECPoint>>();

				// loop over each ballot
				for (int i = 0; i < mixOutputArray.length(); i++) {

					currentIds = new ArrayList<ECPoint>();

					plaintexts = mixOutputArray.getJSONArray(i);

					// loop over each plaintext
					for (int j = 0; j < plaintexts.length(); j++) {
						currentIds.add(ECUtils.constructECPointFromJSON(plaintexts.getJSONObject(j)));
					}

					currentFileIds.add(currentIds);
				}

				this.mixOutput.put(currentIdentifier, currentFileIds);
			}
		}

		logger.debug("Successfully loaded mixnet output data");
	}

	/**
	 * Loads the mix input data
	 * 
	 * @throws ASN1Exception
	 * @throws MixDataException
	 * @throws JSONIOException
	 * @throws JSONException
	 */
	private void loadMixInputData() throws ASN1Exception, MixDataException, JSONIOException, JSONException {

		this.mixInput = new HashMap<RaceIdentifier, List<List<ElGamalECPoint>>>();

		final File mixInputDirectory = new File(this.mixInputPath);

		logger.debug("Loading in the mix input data from the folder: {}", mixInputDirectory);

		// check whether the provided directory is valid
		if (!mixInputDirectory.isDirectory()) {
			logger.error("The mix input folder must be a directory: {}", mixInputDirectory);
			throw new MixDataException("The mix input folder must be a directory: " + mixInputDirectory);
		}

		String jsonFile = null;

		RaceIdentifier currentIdentifier = null;

		JSONArray mixInputArray = null;

		JSONArray ciphers = null;

		List<ElGamalECPoint> currentPackings = null;

		List<List<ElGamalECPoint>> currentFilePackings = null;

		// loop over each file in the directory
		for (File file : mixInputDirectory.listFiles()) {

			logger.debug("Current file in mix input directory: {}", file.getName());

			if (IOUtils.checkExtension(FileType.MIX_INPUT, file.getName())) {

				// get equivalent json filename
				jsonFile = IOUtils.addExtension(FilenameUtils.removeExtension(file.getPath()), FileType.JSON);

				// convert from asn.1 to json format
				ASN1ToJSONConverter.asn1ToJSON(file.getPath(), jsonFile, FileType.MIX_INPUT);

				// get current identifier
				currentIdentifier = Utils.getRaceIdentifierFromFileName(jsonFile, this.hasRaceMap);

				mixInputArray = IOUtils.readJSONArrayFromFile(jsonFile);

				currentFilePackings = new ArrayList<List<ElGamalECPoint>>();

				// loop over each ballot
				for (int i = 0; i < mixInputArray.length(); i++) {

					currentPackings = new ArrayList<ElGamalECPoint>();

					ciphers = mixInputArray.getJSONArray(i);

					// loop over each cipher
					for (int j = 0; j < ciphers.length(); j++) {
						currentPackings.add(ECUtils.constructElGamalECPointFromJSON(ciphers.getJSONObject(j)));
					}

					currentFilePackings.add(currentPackings);
				}

				this.mixInput.put(currentIdentifier, currentFilePackings);
			}
		}

		logger.debug("Successfully loaded mixnet input data");
	}

	/**
	 * Helper method to load the mixnet output preferences from csv files
	 * 
	 * @throws MixDataException
	 */
	private void loadMixOutputPreferences() throws MixDataException {
		this.mixOutputPreferences = new HashMap<RaceIdentifier, MixOutput>();

		final File mixOutputDirectory = new File(this.mixOutputPath);

		logger.debug("Loading in the mix output preferences from the folder: {}", mixOutputDirectory);

		// check whether the provided directory is valid
		if (!mixOutputDirectory.isDirectory()) {
			logger.error("The mix output folder must be a directory: {}", mixOutputDirectory);
			throw new MixDataException("The mix output folder must be a directory: " + mixOutputDirectory);
		}

		// loop over each file in the directory
		for (File file : mixOutputDirectory.listFiles()) {

			logger.debug("Current file in mix output directory: {}", file.getName());

			if (IOUtils.checkExtension(FileType.CSV, file.getName())) {
				this.addMixOutput(file.toString());
			}
		}

		logger.debug("Successfully loaded mixnet output preferences");
	}

	/**
	 * Loads the padding points to be used after vote packing has taken place to
	 * ensure all packed votes have the same number of columns
	 * 
	 * @return true if the padding points are loaded correctly
	 * @throws JSONException
	 * @throws JSONIOException
	 */
	private boolean loadPaddingPoints() throws JSONException, JSONIOException {
		String paddingPointFile = IOUtils.findFile(this.getVotePackingConfig().getPaddingFile(), this.getBasePath());

		// read padding point for padding the reordered packed plaintext
		// candidate ids
		this.paddingPoint = ECUtils.constructECPointFromJSON(IOUtils.readJSONObjectFromFile(paddingPointFile));

		this.encryptedPaddingPoint = ECUtils.encrypt(this.paddingPoint, this.getPublicKey(), BigInteger.ONE);

		return true;
	}

	/**
	 * Helper method for creating and adding a <code>MixOutput</code> file
	 * containing preferences
	 * 
	 * @param mixOutputFile
	 * @throws MixDataException
	 */
	private void addMixOutput(String mixOutputFile) throws MixDataException {

		// read the input csv and construct a MixOutput object
		MixOutput currentMixOutput = new MixOutput(mixOutputFile, this.hasRaceMap);
		this.mixOutputPreferences.put(currentMixOutput.getIdentifier(), currentMixOutput);
	}

	/**
	 * Helper method used to organise voting messages together grouping them by
	 * serial numbers
	 * 
	 * @return true if the organisation took place successfully
	 */
	private boolean organiseVoteMessages() {

		logger.debug("Organising Voting messages together");

		Map<String, PODMessage> podMessageMap = null;
		Map<String, VoteMessage> voteMessageMap = null;

		try {
			for (CommitIdentifier podIdentifier : this.podMessages.keySet()) {
				podMessageMap = this.podMessages.get(podIdentifier);

				for (String serialNumber : podMessageMap.keySet()) {
					for (CommitIdentifier voteIdentifier : this.voteMessages.keySet()) {
						voteMessageMap = this.voteMessages.get(voteIdentifier);

						if (voteMessageMap.containsKey(serialNumber)) {
							this.votingProcesses.put(serialNumber, new VotingProcess(podMessageMap.get(serialNumber), voteMessageMap.get(serialNumber)));
						}
					}
				}
			}
		} catch (VoteMessageCommitException e) {
			logger.error("Unable to organise voting messages: {}", e);
			return false;
		}

		logger.debug("Removing cancelled ballots");

		Map<String, CancelMessage> cancelMessageMap = null;

		for (CommitIdentifier cancelIdentifier : this.cancelMessages.keySet()) {

			logger.debug("Removing cancelled ballot: {}", cancelIdentifier);

			cancelMessageMap = this.cancelMessages.get(cancelIdentifier);

			for (String serialNumber : cancelMessageMap.keySet()) {

				if (this.votingProcesses.containsKey(serialNumber)) {
					this.votingProcesses.remove(serialNumber);
					logger.debug("Removing cancelled ballot: {}", serialNumber);
				}
			}
		}

		logger.debug("Successfully organised Voting messages together");

		return true;
	}

	/**
	 * Constructor for a ballot gen data store object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentDataStoreException
	 * @throws ComponentSpecException
	 * @throws DataStoreException
	 * @throws SpecException
	 */
	public VotePackingDataStore(JSONObject spec, String basePath, boolean useExtraCommits) throws ComponentDataStoreException, ComponentSpecException, DataStoreException, SpecException {
		this(new VotePackingVerifierSpec(spec), basePath, useExtraCommits);
	}

	@Override
	public List<MessageType> initialiseListOfRelevantMessages() {

		List<MessageType> relevantTypes = super.initialiseListOfRelevantMessages();
		relevantTypes.add(MessageType.VOTE);
		relevantTypes.add(MessageType.POD);
		relevantTypes.add(MessageType.CANCEL);
		relevantTypes.add(MessageType.AUDIT);
		return relevantTypes;
	}

	@Override
	public VotePackingVerifierSpec getSpec() {
		if (super.getSpec() instanceof VotePackingVerifierSpec) {
			return (VotePackingVerifierSpec) super.getSpec();
		}
		return null;
	}

	/**
	 * Getter for the votePackingConfig
	 * 
	 * @return votePackingConfig
	 */
	public VotePackingConfig getVotePackingConfig() {
		return this.votePackingConfig;
	}

	/**
	 * Getter for the mix input data path
	 * 
	 * @return mixInputPath
	 */
	public String getMixInputPath() {
		return this.mixInputPath;
	}

	/**
	 * Getter for the mix output data path
	 * 
	 * @return mixOutputPath
	 */
	public String getMixOutputPath() {
		return this.mixOutputPath;
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
			if (typedMessage.getType().equals(MessageType.VOTE)) {
				this.addVoteMessage(typedMessage, commitment);
			} else if (typedMessage.getType().equals(MessageType.POD)) {
				this.addPODMessage(typedMessage, commitment);
			} else if (typedMessage.getType().equals(MessageType.CANCEL)) {
				this.addCancelMessage(typedMessage, commitment);
			}
		} catch (CommitIdentifierException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		} catch (VoteMessageCommitException e) {
			logger.error("Unable to add message: {}", typedMessage, e);
			return false;
		}

		return true;
	}

	/**
	 * Adds a cancel message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws CommitIdentifierException
	 * @throws VoteMessageCommitException 
	 */
	private void addCancelMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws CommitIdentifierException, VoteMessageCommitException {
		if (typedMessage instanceof CancelMessage) {
			CancelMessage message = (CancelMessage) typedMessage;

			String serialNumber = message.getSerialNo();
			
			this.checkCancelSerialNumber(serialNumber);

			String boothID = message.getBoothID();
			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), boothID);

			Map<String, CancelMessage> cancelMessageMap = null;

			if (this.cancelMessages.containsKey(identifier)) {
				cancelMessageMap = this.cancelMessages.get(identifier);
				cancelMessageMap.put(serialNumber, message);
			} else {
				cancelMessageMap = new TreeMap<String, CancelMessage>(new BallotSerialNumberComparator());
				cancelMessageMap.put(serialNumber, message);
				this.cancelMessages.put(identifier, cancelMessageMap);
			}
		}
	}
	
	/**
	 * Checks that a serial number doesn't already exist within the cancel message
	 * store
	 * 
	 * @param serialNumber
	 * @throws VoteMessageCommitException
	 */
	private void checkCancelSerialNumber(String serialNumber) throws VoteMessageCommitException {
		for (CommitIdentifier identifier : this.cancelMessages.keySet()) {
			if (this.cancelMessages.get(identifier).containsKey(serialNumber)) {
				logger.warn("Should not have a cancel message already existing with the same serial number: {}", serialNumber);
				resultsLogger.warn("Should not have a cancel message already existing with the same serial number: {}", serialNumber);
			}
		}
	}

	/**
	 * Private helper method to add a POD message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws CommitIdentifierException
	 * @throws VoteMessageCommitException
	 */
	private void addPODMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws CommitIdentifierException, VoteMessageCommitException {
		if (typedMessage instanceof PODMessage) {
			PODMessage message = (PODMessage) typedMessage;

			String serialNumber = message.getSerialNo();

			this.checkPODSerialNumber(serialNumber);

			String boothID = message.getBoothID();
			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), boothID);

			Map<String, PODMessage> podMessageMap = null;

			if (this.podMessages.containsKey(identifier)) {
				podMessageMap = this.podMessages.get(identifier);
				if (podMessageMap.containsKey(serialNumber)) {
					podMessageMap.remove(serialNumber);
				}
				podMessageMap.put(serialNumber, message);
			} else {
				podMessageMap = new TreeMap<String, PODMessage>(new BallotSerialNumberComparator());
				podMessageMap.put(serialNumber, message);
				this.podMessages.put(identifier, podMessageMap);
			}
		}
	}

	/**
	 * Private helper method to add a Vote message
	 * 
	 * @param typedMessage
	 * @param commitment
	 * @throws CommitIdentifierException
	 * @throws VoteMessageCommitException
	 */
	private void addVoteMessage(TypedJSONMessage typedMessage, FinalCommitment commitment) throws CommitIdentifierException, VoteMessageCommitException {
		if (typedMessage instanceof VoteMessage) {
			VoteMessage message = (VoteMessage) typedMessage;

			String serialNumber = message.getSerialNo();

			this.checkVoteSerialNumber(serialNumber);

			String boothID = message.getBoothID();
			CommitIdentifier identifier = new CommitIdentifier(commitment.getIdentifier(), boothID);

			Map<String, VoteMessage> voteMessageMap = null;

			if (this.voteMessages.containsKey(identifier)) {
				voteMessageMap = this.voteMessages.get(identifier);
				if (voteMessageMap.containsKey(serialNumber)) {
					voteMessageMap.remove(serialNumber);
				}
				voteMessageMap.put(serialNumber, message);
			} else {
				voteMessageMap = new TreeMap<String, VoteMessage>(new BallotSerialNumberComparator());
				voteMessageMap.put(serialNumber, message);
				this.voteMessages.put(identifier, voteMessageMap);
			}
		}
	}

	/**
	 * Checks that a serial number doesn't already exist within the pod message
	 * store
	 * 
	 * @param serialNumber
	 * @throws VoteMessageCommitException
	 */
	private void checkPODSerialNumber(String serialNumber) throws VoteMessageCommitException {
		for (CommitIdentifier identifier : this.podMessages.keySet()) {
			if (this.podMessages.get(identifier).containsKey(serialNumber)) {
				logger.warn("Should not have a pod message already existing with the same serial number: {}", serialNumber);
				resultsLogger.warn("Should not have a pod message already existing with the same serial number: {}", serialNumber);
			}
		}
	}

	/**
	 * Checks that a serial number doesn't already exist within the vote message
	 * store
	 * 
	 * @param serialNumber
	 * @throws VoteMessageCommitException
	 */
	private void checkVoteSerialNumber(String serialNumber) throws VoteMessageCommitException {
		for (CommitIdentifier identifier : this.voteMessages.keySet()) {
			if (this.voteMessages.get(identifier).containsKey(serialNumber)) {
				logger.warn("Should not have a vote message already existing with the same serial number: {}", serialNumber);
				resultsLogger.warn("Should not have a vote message already existing with the same serial number: {}", serialNumber);
			}
		}
	}

	/**
	 * Getter for the vote messages
	 * 
	 * @return voteMessages
	 */
	public Map<CommitIdentifier, Map<String, VoteMessage>> getVoteMessages() {
		return Collections.unmodifiableMap(this.voteMessages);
	}

	/**
	 * Getter for the pod messages
	 * 
	 * @return podMessages
	 */
	public Map<CommitIdentifier, Map<String, PODMessage>> getPodMessages() {
		return Collections.unmodifiableMap(this.podMessages);
	}

	/**
	 * Getter for the voting processes
	 * 
	 * @return votingProcesses
	 */
	public Map<String, VotingProcess> getVotingProcesses() {
		return Collections.unmodifiableMap(this.votingProcesses);
	}

	/**
	 * Getter for the mixnet output preferences
	 * 
	 * @return mixOutput
	 */
	public Map<RaceIdentifier, MixOutput> getMixOutputPreferences() {
		return Collections.unmodifiableMap(this.mixOutputPreferences);
	}

	/**
	 * Getter for the padding point
	 * 
	 * @return paddingPoint
	 */
	public ECPoint getPaddingPoint() {
		return this.paddingPoint;
	}

	/**
	 * Getter for the encrypted padding point
	 * 
	 * @return encryptedPaddingPoint
	 */
	public ElGamalECPoint getEncryptedPaddingPoint() {
		return this.encryptedPaddingPoint;
	}

	/**
	 * Getter for the mix input data
	 * 
	 * @return mixInput
	 */
	public Map<RaceIdentifier, List<List<ElGamalECPoint>>> getMixInput() {
		return Collections.unmodifiableMap(this.mixInput);
	}

	/**
	 * Getter for the mix output data
	 * 
	 * @return mixOutput
	 */
	public Map<RaceIdentifier, List<List<ECPoint>>> getMixOutput() {
		return Collections.unmodifiableMap(this.mixOutput);
	}

	/**
	 * Getter for has race map
	 * 
	 * @return hasRaceMap
	 */
	public boolean hasRaceMap() {
		return this.hasRaceMap;
	}

}
