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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.CommitIdentifier;
import com.vvote.datafiles.DistrictConfig;
import com.vvote.datafiles.RaceMap;
import com.vvote.datafiles.commits.gencommit.BallotGenCommit;
import com.vvote.datafiles.commits.gencommit.CommittedBallot;
import com.vvote.datafiles.commits.votes.VotingProcess;
import com.vvote.datafiles.exceptions.DistrictConfigurationException;
import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.datafiles.mix.BallotRaceIdentifier;
import com.vvote.datafiles.mix.CSVPreferences;
import com.vvote.datafiles.mix.MixOutput;
import com.vvote.datafiles.mix.RaceIdentifier;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.messages.typed.vote.RacePreferences;
import com.vvote.messages.typed.vote.RaceType;
import com.vvote.messages.typed.vote.VoteMessage;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.component.ComponentVerifier;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.ComponentVerifierException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifier.exceptions.VotePackingException;
import com.vvote.verifierlibrary.utils.comparators.BallotSerialNumberComparator;
import com.vvote.verifierlibrary.utils.crypto.CryptoUtils;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;

/**
 * VotePackingVerifier is used for carrying out validation and verification of
 * the vote packing process carried out prior to ciphers being sent to the
 * mixnet for shuffling and decryption. VotePackingVerifier is important to
 * ensure that the mix has taken place successfully and all ballots have been
 * sent properly to the mixnet and are included in the mix.
 * 
 * @author James Rumble
 * 
 */
public class VotePackingVerifier extends ComponentVerifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VotePackingVerifier.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Holds the generic ballot sizes - could be looked up but provides easier
	 * access
	 */
	private final Map<RaceType, Integer> genericBallotSizes;

	/**
	 * Holds a list of reduced ballots
	 */
	private final Map<String, ReducedBallot> reducedBallots;

	/**
	 * Holds the reordered ballots
	 */
	private final Map<BallotRaceIdentifier, Map<String, SortedMap<Integer, ElGamalECPoint>>> reorderedBallots;

	/**
	 * Holds the packed ciphers
	 */
	private final Map<BallotRaceIdentifier, Map<String, List<ElGamalECPoint>>> packedCiphers;

	/**
	 * Holds the packed mixnet output
	 */
	private final Map<RaceIdentifier, List<List<ECPoint>>> packedMixOutput;

	/**
	 * Holds the reordered mix output
	 */
	private final Map<RaceIdentifier, List<SortedMap<Integer, ECPoint>>> reorderedMixOutput;

	/**
	 * Constructor for a VotePackingVerifier
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws ComponentVerifierException
	 * @throws VerifierException
	 */
	public VotePackingVerifier(VotePackingDataStore dataStore, VotePackingVerifierSpec spec) throws ComponentVerifierException, VerifierException {
		super(dataStore, spec);

		logger.info("Setting up the Vote Packing Verifier");

		CryptoUtils.initProvider();

		this.genericBallotSizes = new HashMap<RaceType, Integer>();

		this.reducedBallots = new TreeMap<String, ReducedBallot>(new BallotSerialNumberComparator());
		this.reorderedBallots = new HashMap<BallotRaceIdentifier, Map<String, SortedMap<Integer, ElGamalECPoint>>>();
		this.reorderedMixOutput = new HashMap<RaceIdentifier, List<SortedMap<Integer, ECPoint>>>();
		this.packedMixOutput = new HashMap<RaceIdentifier, List<List<ECPoint>>>();
		this.packedCiphers = new HashMap<BallotRaceIdentifier, Map<String, List<ElGamalECPoint>>>();
	}

	@Override
	public VotePackingVerifierSpec getSpec() {
		if (super.getSpec() instanceof VotePackingVerifierSpec) {
			return (VotePackingVerifierSpec) super.getSpec();
		}
		return null;
	}

	@Override
	public VotePackingDataStore getDataStore() {
		if (super.getDataStore() instanceof VotePackingDataStore) {
			return (VotePackingDataStore) super.getDataStore();
		}
		return null;
	}

	/**
	 * Constructor for a VotePackingVerifier from JSONObject representations of
	 * the spec objects
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws SpecException
	 * @throws VerifierException
	 */
	public VotePackingVerifier(VotePackingDataStore dataStore, JSONObject spec) throws ComponentVerifierException, ComponentSpecException, VerifierException, SpecException {
		this(dataStore, new VotePackingVerifierSpec(spec));
	}

	/**
	 * Constructor for a VotePackingVerifier from string representations of the
	 * spec objects
	 * 
	 * @param dataStore
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws SpecException
	 * @throws VerifierException
	 */
	public VotePackingVerifier(VotePackingDataStore dataStore, String spec) throws ComponentVerifierException, ComponentSpecException, VerifierException, SpecException {
		this(dataStore, new VotePackingVerifierSpec(spec));
	}

	/**
	 * Constructor for a VotePackingVerifier from string representations of the
	 * spec objects
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws ComponentDataStoreException
	 * @throws SpecException
	 * @throws DataStoreException
	 * @throws VerifierException
	 */
	public VotePackingVerifier(String spec, String basePath, boolean useExtraCommits) throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, VerifierException,
			DataStoreException, SpecException {
		this(new VotePackingDataStore(new VotePackingVerifierSpec(spec), basePath, useExtraCommits), new VotePackingVerifierSpec(spec));
	}

	/**
	 * Constructor for a VotePackingVerifier from JSONObject representations of
	 * the spec objects
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws ComponentDataStoreException
	 * @throws SpecException
	 * @throws DataStoreException
	 * @throws VerifierException
	 */
	public VotePackingVerifier(JSONObject spec, String basePath, boolean useExtraCommits) throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, VerifierException,
			DataStoreException, SpecException {
		this(new VotePackingDataStore(spec, basePath, useExtraCommits), new VotePackingVerifierSpec(spec));
	}

	@Override
	public boolean doVerification() {
		logger.debug("Starting vote packing verification");
		resultsLogger.info("Starting vote packing verification");

		ECUtils.changeCurve(this.getDataStore().getVotePackingConfig().getCurve());
		
		this.genericBallotSizes.put(RaceType.LA, this.getDataStore().getBallotGenerationConfig().getLASize());
		this.genericBallotSizes.put(RaceType.LC_ATL, this.getDataStore().getBallotGenerationConfig().getLcATLSize());
		this.genericBallotSizes.put(RaceType.LC_BTL, this.getDataStore().getBallotGenerationConfig().getLcBTLSize());
		
		boolean verified = super.doVerification();

		try {
			if (!this.reduceBallots()) {
				verified = false;
			}

			if (!this.verifyBallotReductions()) {
				verified = false;
			}

			if (!this.reorderReducedBallots()) {
				verified = false;
			}

			if (!this.packReorderedBallots()) {
				verified = false;
			}

			// pad the packed ciphers to all be the same length
			this.padPackedCiphers();

			if (!this.verifyMixInput()) {
				verified = false;
			}

			if (this.reorderMixOutput()) {
				if (this.packMixOutput()) {
					this.padPackedPlaintexts();
					if (this.verifyMixOutput()) {
						if (!this.verifyNumberOfMixValues()) {
							logger.error("Unable to verify the number of input and output Mixnet datasets - stopping further verification on the Mixnet output data");
							resultsLogger.error("Unable to verify the number of input and output Mixnet datasets - stopping further verification on the Mixnet output data");
							verified = false;
						}
					} else {
						logger.error("Unable to verify Mixnet output data - stopping further verification on the Mixnet output data");
						resultsLogger.error("Unable to verify Mixnet output data - stopping further verification on the Mixnet output data");
						verified = false;
					}
				} else {
					logger.error("Unable to pack Mixnet output data - stopping further verification on the Mixnet output data");
					resultsLogger.error("Unable to pack Mixnet output data - stopping further verification on the Mixnet output data");
					verified = false;
				}
			} else {
				logger.error("Unable to reorder Mixnet output data - stopping further verification on the Mixnet output data");
				resultsLogger.error("Unable to reorder Mixnet output data - stopping further verification on the Mixnet output data");
				verified = false;
			}

		} catch (MixDataException e) {
			return false;
		} catch (DistrictConfigurationException e) {
			return false;
		}

		if (verified) {
			resultsLogger.info("Vote Packing Verification was carried out successfully");
		}

		return verified;
	}

	/**
	 * Verifies that the number of Mixnet input values matches the number of
	 * Mixnet output values
	 * 
	 * @return true if the number of Mixnet input values matches the number of
	 *         Mixnet output values
	 */
	private boolean verifyNumberOfMixValues() {

		logger.info("Starting the verification that the number of Mixnet input values matches the number of Mixnet output values");

		List<List<ECPoint>> mixOutputPlaintexts = null;
		List<List<ElGamalECPoint>> mixInputCiphers = null;

		BallotRaceIdentifier raceIdentifier = null;
		RaceMap raceMap = null;

		try {
			for (RaceIdentifier identifier : this.packedMixOutput.keySet()) {

				if (this.getDataStore().hasRaceMap()) {
					raceMap = this.getDataStore().getRaceMap().getRaceMap(identifier.getRaceId());

					// get the corresponding district config
					raceIdentifier = new BallotRaceIdentifier(identifier.getRaceType(), raceMap.getDistrict());
				} else {
					raceIdentifier = new BallotRaceIdentifier(identifier.getRaceType(), identifier.getDistrict());
				}

				// get the corresponding mix input
				mixInputCiphers = getMixInputUsingBallotIdentifier(raceIdentifier);

				mixOutputPlaintexts = this.getDataStore().getMixOutput().get(identifier);

				if (mixInputCiphers == null || mixOutputPlaintexts == null) {
					logger.error("Could not verify Mixnet input and Mixnet output values for: {}", identifier);
					resultsLogger.error("Could not verify Mixnet input and Mixnet output values for: {}", identifier);
				} else {

					if (mixInputCiphers.size() != mixOutputPlaintexts.size()) {
						logger.error("Mixnet input and Mixnet output values for: {} have different number of ballots", identifier);
						resultsLogger.error("Mixnet input and Mixnet output values for: {} have different number of ballots", identifier);
						return false;
					}

					for (int i = 0; i < mixInputCiphers.size(); i++) {
						if (mixInputCiphers.get(i).size() != mixOutputPlaintexts.get(i).size()) {

							logger.error("Mixnet input and Mixnet output values for: {} for row: {} have different number of packings", identifier, i);
							resultsLogger.error("ixnet input and Mixnet output values for: {} for row: {} have different number of packings", identifier, i);

							return false;
						}
					}
				}
			}
		} catch (MixDataException e) {
			logger.error("Could not verify the Mixnet input and Mixnet output values", e);
			resultsLogger.error("Could not verify the Mixnet input and Mixnet output values", e);
		}

		logger.debug("Successfully verified that the number of Mixnet input values matches the number of Mixnet output values");
		resultsLogger.info("Successfully verified that the number of Mixnet input values matches the number of Mixnet output values");

		return true;
	}

	/**
	 * Pads the plaintext mixnet output values so that they are all the same
	 * length
	 */
	private void padPackedPlaintexts() {

		logger.info("Starting the process of padding the packed plaintext candidate identifiers");

		List<ECPoint> currentPackingList = null;

		int maxLaColumns = 0;
		List<Integer> laColumns = new ArrayList<Integer>();

		int maxLcBTLColumns = 0;
		List<Integer> lcBTLColumns = new ArrayList<Integer>();

		int requiredPadding = 0;

		// loop over the packed ciphers again with the aim of padding all
		// columns to be the same length
		for (RaceIdentifier identifier : this.packedMixOutput.keySet()) {

			if (!identifier.getRaceType().equals(RaceType.LC_ATL)) {
				// loop over packed ciphers with the aim of getting the la and
				// lc btl maximum columns

				for (int i = 0; i < this.packedMixOutput.get(identifier).size(); i++) {
					if (identifier.getRaceType().equals(RaceType.LA)) {
						currentPackingList = this.packedMixOutput.get(identifier).get(i);
						laColumns.add(currentPackingList.size());
					}
					if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
						currentPackingList = this.packedMixOutput.get(identifier).get(i);
						lcBTLColumns.add(currentPackingList.size());
					}
				}

				// get the maximum columns
				if (identifier.getRaceType().equals(RaceType.LA)) {
					maxLaColumns = Collections.max(laColumns);
				} else if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
					maxLcBTLColumns = Collections.max(lcBTLColumns);
				}

				for (int i = 0; i < this.packedMixOutput.get(identifier).size(); i++) {

					if (identifier.getRaceType().equals(RaceType.LA)) {
						currentPackingList = this.packedMixOutput.get(identifier).get(i);

						// add the padding the required number of times
						if (currentPackingList.size() < maxLaColumns) {
							requiredPadding = maxLaColumns - currentPackingList.size();
							for (int j = 0; j < requiredPadding; j++) {
								currentPackingList.add(this.getDataStore().getPaddingPoint());
							}
						}
					} else if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
						currentPackingList = this.packedMixOutput.get(identifier).get(i);

						// add the padding the required number of times
						if (currentPackingList.size() < maxLcBTLColumns) {
							requiredPadding = maxLcBTLColumns - currentPackingList.size();
							for (int j = 0; j < requiredPadding; j++) {
								currentPackingList.add(this.getDataStore().getPaddingPoint());
							}
						}
					}
				}

				laColumns.clear();
				lcBTLColumns.clear();
			}
		}

		logger.debug("Finished padding the packed plaintext candidate identifiers");
		resultsLogger.info("Finished padding the packed plaintext candidate identifiers");
	}

	/**
	 * Verifies the mixnet output
	 * 
	 * @return true if the mixnet output values have been verified successfully
	 */
	public boolean verifyMixOutput() {

		logger.info("Starting the verification of the Mixnet output values");

		boolean foundMatch = false;

		List<List<ECPoint>> mixOutputPlaintexts = null;
		List<List<ECPoint>> mixOutputBallots = null;

		// loop over each ballot race identifier (will be one for each different
		// racetype district combination
		for (RaceIdentifier identifier : this.packedMixOutput.keySet()) {

			// get the current set of packed plaintext ids
			mixOutputBallots = this.packedMixOutput.get(identifier);

			// get the corresponding mix input
			mixOutputPlaintexts = this.getDataStore().getMixOutput().get(identifier);

			if (mixOutputPlaintexts != null) {

				// loop over each packing
				for (List<ECPoint> packings : mixOutputPlaintexts) {
					// loop over each mix input ballot and try to find a match
					for (List<ECPoint> ballot : mixOutputBallots) {
						if (eCPointListsAreEqual(packings, ballot)) {
							logger.info("Successfully found packing for: {}", identifier);
							foundMatch = true;
						}
					}

					// if no match can be found then packing has been
					// unsuccessful
					if (!foundMatch) {
						logger.error("Unable to verify Mixnet output vote packing process: Could not find packing for: {}", identifier);
						resultsLogger.error("Unable to verify output input vote packing process: Could not find packing for: {}", identifier);

						return false;
					}

					// reset the flag
					foundMatch = false;
				}
			} else {
				logger.error("Unable to verify Mixnet output vote packing process: {}", identifier);
				resultsLogger.error("Unable to verify Mixnet output vote packing process: {}", identifier);
				return false;
			}
		}

		logger.debug("Successfully verified the Mixnet output values were correct packings of each of the packed reordered plaintext candidate identifiers");
		resultsLogger.info("Successfully verified the Mixnet output values were correct packings of each of the packed reordered plaintext candidate identifiers");

		// if all packed ciphers have a match then we get here
		return true;
	}

	/**
	 * Checks whether two lists are equal
	 * 
	 * @param first
	 * @param second
	 * @return true if the two lists of ECPoints are equal
	 */
	private boolean eCPointListsAreEqual(List<ECPoint> first, List<ECPoint> second) {
		// check sizes
		if (first.size() != second.size()) {
			return false;
		}
		// check each element
		for (int i = 0; i < first.size(); i++) {
			if (!first.get(i).equals(second.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Provides one of the two main verification checks for vote packing. We
	 * need to ensure that the reduced ballots which have been packed and then
	 * appropriately packed match exactly what was actually input to the mixnet
	 * 
	 * @return true if there is a mix input for each of the packed ciphers
	 */
	public boolean verifyMixInput() {

		logger.info("Starting the verification of the Mixnet input values");

		boolean foundEncryption = false;

		List<List<ElGamalECPoint>> mixInputCiphers = null;
		Map<String, List<ElGamalECPoint>> ballotCiphers = null;

		// loop over each ballot race identifier (will be one for each different
		// racetype district combination
		for (BallotRaceIdentifier identifier : this.packedCiphers.keySet()) {

			// get the current set of packed ciphers
			ballotCiphers = this.packedCiphers.get(identifier);

			// get the corresponding mix input
			mixInputCiphers = getMixInputUsingBallotIdentifier(identifier);

			if (mixInputCiphers != null) {

				// loop over each packed ballot
				for (String serialNo : ballotCiphers.keySet()) {

					// loop over each mix input ballot and try to find a match
					for (List<ElGamalECPoint> ballot : mixInputCiphers) {
						if (elGamalECPointListsAreEqual(ballotCiphers.get(serialNo), ballot)) {
							logger.info("Successfully found packing for: {}: {}", identifier, serialNo);
							foundEncryption = true;
						}
					}

					// if no match can be found then packing has been
					// unsuccessful
					if (!foundEncryption) {
						logger.error("Unable to verify Mixnet input vote packing process: Could not find packing for: {}: {}", identifier, serialNo);
						resultsLogger.error("Unable to verify Mixnet input vote packing process: Could not find packing for: {}: {}", identifier, serialNo);

						return false;
					}

					// reset the flag
					foundEncryption = false;
				}
			} else {
				logger.error("Unable to verify Mixnet input vote packing process: {}", identifier);
				resultsLogger.error("Unable to verify Mixnet input vote packing process: {}", identifier);
				return false;
			}
		}

		logger.debug("Successfully verified the Mixnet input values were correct packings of each of the votes cast");
		resultsLogger.info("Successfully verified the Mixnet input values were correct packings of each of the votes cast");

		return true;
	}

	/**
	 * Checks whether two lists of ElGamalECPoint objects are equal
	 * 
	 * @param first
	 * @param second
	 * @return true if all elements in two lists of ElGamalECPoint objects are
	 *         equal
	 */
	private boolean elGamalECPointListsAreEqual(List<ElGamalECPoint> first, List<ElGamalECPoint> second) {
		// check sizes
		if (first.size() != second.size()) {
			return false;
		}
		// check each element
		for (int i = 0; i < first.size(); i++) {
			if (!first.get(i).equals(second.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Provides the ability to check whether a specified set of mix input values
	 * match a specific set of reordered and packed ciphers
	 * 
	 * @param identifier
	 * @return true if the provided identifier exists in the mix input
	 */
	private List<List<ElGamalECPoint>> getMixInputUsingBallotIdentifier(BallotRaceIdentifier identifier) {

		RaceMap raceMap = null;

		for (RaceIdentifier raceIdentifier : this.getDataStore().getMixInput().keySet()) {

			if (this.getDataStore().hasRaceMap()) {
				raceMap = this.getDataStore().getRaceMap().getRaceMap(raceIdentifier.getRaceId());

				if (identifier.getDistrict().equals(raceMap.getDistrict())) {
					if (identifier.getRaceType().equals(raceIdentifier.getRaceType())) {
						return this.getDataStore().getMixInput().get(raceIdentifier);
					}
				}
			} else {

				if (identifier.getDistrict().equals(raceIdentifier.getDistrict())) {
					if (identifier.getRaceType().equals(raceIdentifier.getRaceType())) {
						return this.getDataStore().getMixInput().get(raceIdentifier);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Reorders and organises the mix output also collecting the relevant
	 * plaintext ids at the same time
	 * 
	 * @return true if the mixoutput values were reordered successfully
	 */
	public boolean reorderMixOutput() {

		logger.info("Starting the process of reordering the plaintext candidate identifiers using the Mixnet output preferences");

		MixOutput output = null;
		DistrictConfig districtConfig = null;

		Map<RaceType, List<ECPoint>> plaintextIds = null;
		SortedMap<Integer, ECPoint> preferencesMap = null;
		List<SortedMap<Integer, ECPoint>> mixOutputPrefs = null;

		RaceMap raceMap = null;

		int index = 0;

		String districtName = null;

		// loop over the mixout
		for (RaceIdentifier identifier : this.getDataStore().getMixOutput().keySet()) {

			if (this.getDataStore().hasRaceMap()) {
				raceMap = this.getDataStore().getRaceMap().getRaceMap(identifier.getRaceId());

				// get the corresponding district config
				districtName = raceMap.getDistrict();
			} else {
				districtName = identifier.getDistrict();
			}

			districtConfig = this.getDataStore().getDistrictConfigByName(districtName);

			if (districtConfig == null) {
				logger.error("Unable to reorder the Mixnet output data - could not get district configuration data for district: {}", districtName);
				resultsLogger.error("Unable to reorder the Mixnet output data - could not get district configuration data for district: {}", districtName);
				return false;
			}

			output = this.getDataStore().getMixOutputPreferences().get(identifier);

			// get the encrypted ids for that district
			plaintextIds = this.getPlaintextIdsForDistrict(districtConfig);

			mixOutputPrefs = new ArrayList<SortedMap<Integer, ECPoint>>();

			// loop over the csv preferences - each one corresponds to a
			// ballot
			for (CSVPreferences csvPrefs : output.getBallotPreferences()) {

				preferencesMap = new TreeMap<Integer, ECPoint>();

				index = 0;

				// loop over preferences for each ballot
				for (String preference : csvPrefs.getPreferences()) {

					// if not blank then add it
					if (!preference.equals(com.vvote.messages.fields.MessageFields.VoteMessage.PREFERENCE_IS_BLANK) && preference.length() > 0) {
						preferencesMap.put(Integer.parseInt(preference), plaintextIds.get(identifier.getRaceType()).get(index));
					}

					index++;
				}

				mixOutputPrefs.add(preferencesMap);
			}

			this.reorderedMixOutput.put(identifier, mixOutputPrefs);
		}

		logger.debug("Successfully reordered the plaintext candidate identifiers using the Mixnet output preferences");
		resultsLogger.info("Successfully reordered the plaintext candidate identifiers using the Mixnet output preferences");

		return true;
	}

	/**
	 * Packs together the reordered mix output data
	 * 
	 * @return true if the packing took place correctly
	 */
	public boolean packMixOutput() {

		logger.debug("Starting the process of packing the reordered plaintext candidate identifiers");

		boolean isLaPacked = true;
		boolean isLcBTLPacked = true;

		int laPackingSize = -1;
		int lcBTLPackingSize = -1;

		// check whether la race is packed
		if (this.getDataStore().getVotePackingConfig().getUseDirect().get(RaceType.LA)) {
			isLaPacked = false;
		}

		if (isLaPacked) {
			// get la race packing size
			laPackingSize = this.getDataStore().getVotePackingConfig().getLaPacking();
		}

		// check whether lc btl race is packed
		if (this.getDataStore().getVotePackingConfig().getUseDirect().get(RaceType.LC_BTL)) {
			isLcBTLPacked = false;
		}

		if (isLcBTLPacked) {
			// get lc btl race packing size
			lcBTLPackingSize = this.getDataStore().getVotePackingConfig().getLcBTLPacking();
		}

		List<SortedMap<Integer, ECPoint>> mixOutputPrefs = null;

		List<List<ECPoint>> packings = null;

		// loop over mixnet outputs
		for (RaceIdentifier identifier : this.reorderedMixOutput.keySet()) {

			// get the list of ballots per identifier
			mixOutputPrefs = this.reorderedMixOutput.get(identifier);

			// if race type is la and the la race is packed
			if (identifier.getRaceType().equals(RaceType.LA) && isLaPacked) {

				// put the la packings
				packings = new ArrayList<List<ECPoint>>();
				for (int i = 0; i < mixOutputPrefs.size(); i++) {

					packings.add(this.packPlaintexts(mixOutputPrefs.get(i), laPackingSize));
				}
			}

			// if race type is lc btl and the la race is packed
			if (identifier.getRaceType().equals(RaceType.LC_BTL) && isLcBTLPacked) {

				// put the lc btl packings
				packings = new ArrayList<List<ECPoint>>();
				for (int i = 0; i < mixOutputPrefs.size(); i++) {

					packings.add(this.packPlaintexts(mixOutputPrefs.get(i), lcBTLPackingSize));
				}
			}

			// if race type is lc atl
			if (identifier.getRaceType().equals(RaceType.LC_ATL)) {

				// put the lc atl ciphers directly
				packings = new ArrayList<List<ECPoint>>();
				for (int i = 0; i < mixOutputPrefs.size(); i++) {
					List<ECPoint> atlList = new ArrayList<ECPoint>();

					int key = mixOutputPrefs.get(i).firstKey();
					atlList.add(mixOutputPrefs.get(i).get(key).multiply(BigInteger.ONE));
					packings.add(atlList);
				}
			}

			this.packedMixOutput.put(identifier, packings);
		}

		logger.debug("Finished packing the reordered plaintext candidate identifiers");
		resultsLogger.info("Finished packing the reordered plaintext candidate identifiers");

		return true;
	}

	/**
	 * Packs plaintext ids together using the provided packing size
	 * 
	 * @param preferences
	 * @param packingSize
	 * @return a list of packed plaintext ids
	 */
	private List<ECPoint> packPlaintexts(SortedMap<Integer, ECPoint> preferences, int packingSize) {
		int packingPreference = 0;
		ECPoint currentId = null;
		ECPoint currentPacking = null;
		List<ECPoint> currentPackedList = null;

		currentPackedList = new ArrayList<ECPoint>();

		// loop over preferences
		for (Integer pref : preferences.keySet()) {

			// increment the packing preference which ranges from 1 to packing
			// size
			packingPreference++;

			// gets the specific cipher
			currentId = preferences.get(pref);

			// multiply by the packing preference number (NOT the actual
			// preference number)
			currentId = currentId.multiply(BigInteger.valueOf(packingPreference));

			// either store this current cipher
			if (currentPacking == null) {
				currentPacking = currentId;
			} else {
				// or add to the previous packing
				currentPacking = currentPacking.add(currentId);
			}

			// packing preference needs to be reset if it equals the maximum
			// packing size
			if (packingPreference == packingSize) {
				packingPreference = 0;

				currentPackedList.add(currentPacking);
				currentPacking = null;
			}
		}

		if (currentPacking != null) {
			currentPackedList.add(currentPacking);
		}

		return currentPackedList;
	}

	/**
	 * Helper method for getting the base encrypted ids for a specified district
	 * 
	 * @param districtConfig
	 * @return the plaintext ids for a specified district
	 */
	private Map<RaceType, List<ECPoint>> getPlaintextIdsForDistrict(DistrictConfig districtConfig) {
		List<ECPoint> laPlaintextIds = null;
		List<ECPoint> lcATLPlaintextIds = null;
		List<ECPoint> lcBTLPlaintextIds = null;

		Map<RaceType, List<ECPoint>> plaintextIds = new HashMap<RaceType, List<ECPoint>>();

		int startingIndex = 0;

		logger.debug("Getting plaintext ids for district: {}", districtConfig);

		// get the la race plaintext ids for the district
		laPlaintextIds = new ArrayList<ECPoint>();
		for (int i = startingIndex; i < districtConfig.getLaSize(); i++) {
			laPlaintextIds.add(this.getDataStore().getPlaintextIds().get(i));
		}

		// get the offset to the start of the lc atl race
		startingIndex = this.genericBallotSizes.get(RaceType.LA);

		logger.debug("Getting plaintext ids for district: {} - starting index: {}", districtConfig, startingIndex);

		// get the lc atl race plaintext ids for the district
		lcATLPlaintextIds = new ArrayList<ECPoint>();
		for (int i = startingIndex; i < startingIndex + districtConfig.getLcATLSize(); i++) {
			lcATLPlaintextIds.add(this.getDataStore().getPlaintextIds().get(i));
		}

		// get the offset to the start of the lc btl race
		startingIndex = this.genericBallotSizes.get(RaceType.LA) + this.genericBallotSizes.get(RaceType.LC_ATL);

		logger.debug("Getting plaintext ids for district: {} - starting index: {}", districtConfig, startingIndex);

		// get the lc btl race plaintext ids for the district
		lcBTLPlaintextIds = new ArrayList<ECPoint>();
		for (int i = startingIndex; i < startingIndex + districtConfig.getLcBTLSize(); i++) {
			lcBTLPlaintextIds.add(this.getDataStore().getPlaintextIds().get(i));
		}

		// add the ids and return
		plaintextIds.put(RaceType.LA, laPlaintextIds);
		plaintextIds.put(RaceType.LC_ATL, lcATLPlaintextIds);
		plaintextIds.put(RaceType.LC_BTL, lcBTLPlaintextIds);

		return plaintextIds;
	}

	/**
	 * Packs together the reordered ballots
	 * 
	 * @return true if the packing took place correctly
	 */
	public boolean packReorderedBallots() {

		logger.info("Starting the vote packing process");

		boolean isLaPacked = true;
		boolean isLcBTLPacked = true;

		int laPackingSize = -1;
		int lcBTLPackingSize = -1;

		// check whether la race is packed
		if (this.getDataStore().getVotePackingConfig().getUseDirect().get(RaceType.LA)) {
			isLaPacked = false;
		}

		if (isLaPacked) {
			// get la race packing size
			laPackingSize = this.getDataStore().getVotePackingConfig().getLaPacking();
		}

		// check whether lc btl race is packed
		if (this.getDataStore().getVotePackingConfig().getUseDirect().get(RaceType.LC_BTL)) {
			isLcBTLPacked = false;
		}

		if (isLcBTLPacked) {
			// get lc btl race packing size
			lcBTLPackingSize = this.getDataStore().getVotePackingConfig().getLcBTLPacking();
		}

		Map<String, List<ElGamalECPoint>> racePacking = null;

		SortedMap<Integer, ElGamalECPoint> preferences = null;

		// loop over reordered ballots
		for (BallotRaceIdentifier identifier : this.reorderedBallots.keySet()) {

			racePacking = new HashMap<String, List<ElGamalECPoint>>();

			for (String serialNo : this.reorderedBallots.get(identifier).keySet()) {

				if (identifier.getRaceType().equals(RaceType.LA)) {
					// if the la race is packed
					if (isLaPacked) {
						// get the la preferences
						preferences = this.reorderedBallots.get(identifier).get(serialNo);

						// put the packed ciphers into the map
						racePacking.put(serialNo, this.packCiphers(preferences, laPackingSize));
					} else {
						// if not packed
						List<ElGamalECPoint> laList = new ArrayList<ElGamalECPoint>();

						// just add each of the preferences without packing them
						for (ElGamalECPoint point : this.reorderedBallots.get(identifier).get(serialNo).values()) {
							laList.add(point);
						}

						racePacking.put(serialNo, laList);
					}
				}

				// if the specific vote is atl
				if (identifier.getRaceType().equals(RaceType.LC_ATL)) {
					List<ElGamalECPoint> atlList = new ArrayList<ElGamalECPoint>();

					// add the single preference
					int key = this.reorderedBallots.get(identifier).get(serialNo).firstKey();
					atlList.add(this.reorderedBallots.get(identifier).get(serialNo).get(key));

					racePacking.put(serialNo, atlList);
				} else if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
					// else the vote is btl
					// if the lc btl race is packed
					if (isLcBTLPacked) {
						// get the la preferences
						preferences = this.reorderedBallots.get(identifier).get(serialNo);

						// put the packed ciphers into the map
						racePacking.put(serialNo, this.packCiphers(preferences, lcBTLPackingSize));
					} else {
						// if not packed
						List<ElGamalECPoint> btlList = new ArrayList<ElGamalECPoint>();

						// just add each of the preferences without packing them
						for (ElGamalECPoint point : this.reorderedBallots.get(identifier).get(serialNo).values()) {
							btlList.add(point);
						}

						racePacking.put(serialNo, btlList);
					}
				}
			}

			this.packedCiphers.put(identifier, racePacking);
		}

		logger.debug("Finished packing reordered reduced ballots");
		resultsLogger.info("Finished packing reordered reduced ballots");

		return true;
	}

	/**
	 * Pads the packed ciphers to all be the same length - the same number of
	 * columns as is required by the mixnet
	 */
	private void padPackedCiphers() {

		logger.info("Starting the process of padding the packed ciphertexts");

		List<ElGamalECPoint> currentPackingList = null;

		int maxLaColumns = 0;
		List<Integer> laColumns = new ArrayList<Integer>();

		int maxLcBTLColumns = 0;
		List<Integer> lcBTLColumns = new ArrayList<Integer>();

		int requiredPadding = 0;

		// loop over the packed ciphers again with the aim of padding all
		// columns to be the same length
		for (BallotRaceIdentifier identifier : this.packedCiphers.keySet()) {

			if (!identifier.getRaceType().equals(RaceType.LC_ATL)) {
				// loop over packed ciphers with the aim of getting the la and
				// lc btl maximum columns
				for (String serialNo : this.packedCiphers.get(identifier).keySet()) {

					if (identifier.getRaceType().equals(RaceType.LA)) {
						currentPackingList = this.packedCiphers.get(identifier).get(serialNo);
						laColumns.add(currentPackingList.size());
					}
					if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
						currentPackingList = this.packedCiphers.get(identifier).get(serialNo);
						lcBTLColumns.add(currentPackingList.size());
					}
				}

				// get the maximum columns
				if (identifier.getRaceType().equals(RaceType.LA)) {
					maxLaColumns = Collections.max(laColumns);
				} else if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
					maxLcBTLColumns = Collections.max(lcBTLColumns);
				}

				for (String serialNo : this.packedCiphers.get(identifier).keySet()) {

					if (identifier.getRaceType().equals(RaceType.LA)) {
						currentPackingList = this.packedCiphers.get(identifier).get(serialNo);

						// add the padding the required number of times
						if (currentPackingList.size() < maxLaColumns) {
							requiredPadding = maxLaColumns - currentPackingList.size();
							for (int i = 0; i < requiredPadding; i++) {
								currentPackingList.add(this.getDataStore().getEncryptedPaddingPoint());
							}
						}
					} else if (identifier.getRaceType().equals(RaceType.LC_BTL)) {
						currentPackingList = this.packedCiphers.get(identifier).get(serialNo);

						// add the padding the required number of times
						if (currentPackingList.size() < maxLcBTLColumns) {
							requiredPadding = maxLcBTLColumns - currentPackingList.size();
							for (int i = 0; i < requiredPadding; i++) {
								currentPackingList.add(this.getDataStore().getEncryptedPaddingPoint());
							}
						}
					}
				}

				laColumns.clear();
				lcBTLColumns.clear();
			}
		}

		logger.debug("Finished padding the packed ciphertexts");
		resultsLogger.info("Finished padding the packed ciphertexts");
	}

	/**
	 * Helper method for packing ciphers together
	 * 
	 * @param preferences
	 * 
	 * @param packingSize
	 * @return a list of packed ciphers
	 */
	private List<ElGamalECPoint> packCiphers(SortedMap<Integer, ElGamalECPoint> preferences, int packingSize) {

		int packingPreference = 0;
		ElGamalECPoint currentCipher = null;
		ElGamalECPoint currentPacking = null;
		List<ElGamalECPoint> currentPackedList = null;

		currentPackedList = new ArrayList<ElGamalECPoint>();

		// loop over preferences
		for (Integer pref : preferences.keySet()) {

			// increment the packing preference which ranges from 1 to packing
			// size
			packingPreference++;

			// gets the specific cipher
			currentCipher = preferences.get(pref);

			// multiply by the packing preference number (NOT the actual
			// preference number)
			currentCipher.multiply(BigInteger.valueOf(packingPreference));

			// either store this current cipher
			if (currentPacking == null) {
				currentPacking = currentCipher;
			} else {
				// or add to the previous packing
				currentPacking.add(currentCipher);
			}

			// packing preference needs to be reset if it equals the maximum
			// packing size
			if (packingPreference == packingSize) {
				packingPreference = 0;

				currentPackedList.add(currentPacking);
				currentPacking = null;
			}
		}

		if (currentPacking != null) {
			currentPackedList.add(currentPacking);
		}

		return currentPackedList;
	}

	/**
	 * Reorders the reduced ballots by preference order
	 * 
	 * @return true if the reduced ballots were reordered successfully
	 * @throws MixDataException
	 * @throws DistrictConfigurationException
	 */
	public boolean reorderReducedBallots() throws MixDataException, DistrictConfigurationException {

		logger.info("Starting the process of reordering reduced ballots");

		VoteMessage voteMessage = null;

		ReducedBallot currentReducedBallot = null;

		RacePreferences laPreferences = null;
		RacePreferences lcATLPreferences = null;
		RacePreferences lcBTLPreferences = null;

		Map<String, SortedMap<Integer, ElGamalECPoint>> ballotPreferencesMap = null;

		SortedMap<Integer, ElGamalECPoint> preferencesMap = null;

		int index = 0;

		BallotRaceIdentifier currentIdentifier = null;

		DistrictConfig config = null;

		// loop over serial numbers for all ballots
		for (String serialNumber : this.getDataStore().getVotingProcesses().keySet()) {

			index = 0;

			// get the correct vote message
			voteMessage = this.getDataStore().getVotingProcesses().get(serialNumber).getVoteMessage();

			config = this.getDataStore().getDistrictConfigByName(voteMessage.getDistrict());

			// get the reduced ballot
			currentReducedBallot = this.reducedBallots.get(serialNumber);

			// get the preferences
			laPreferences = voteMessage.getRaces().getLaPreferences();
			lcATLPreferences = voteMessage.getRaces().getLcATLPreferences();
			lcBTLPreferences = voteMessage.getRaces().getLcBTLPreferences();

			if (laPreferences.isUsed()) {

				// use tree map and place preference as the key so ordering is
				// implicit
				preferencesMap = new TreeMap<Integer, ElGamalECPoint>();
				ballotPreferencesMap = new HashMap<String, SortedMap<Integer, ElGamalECPoint>>();

				// loop over la preferences
				for (String preference : laPreferences.getPreferencesArray()) {
					if (!preference.equals(com.vvote.messages.fields.MessageFields.VoteMessage.PREFERENCE_IS_BLANK)) {
						// handle la preferences
						preferencesMap.put(Integer.parseInt(preference), currentReducedBallot.getReducedCipher(index));
					}
					index++;
				}

				ballotPreferencesMap.put(serialNumber, preferencesMap);

				currentIdentifier = new BallotRaceIdentifier(RaceType.LA, currentReducedBallot.getDistrict());

				if (this.reorderedBallots.containsKey(currentIdentifier)) {
					this.reorderedBallots.get(currentIdentifier).putAll(ballotPreferencesMap);
				} else {
					this.reorderedBallots.put(currentIdentifier, ballotPreferencesMap);
				}
			}

			preferencesMap = new TreeMap<Integer, ElGamalECPoint>();
			ballotPreferencesMap = new HashMap<String, SortedMap<Integer, ElGamalECPoint>>();

			index = config.getLaSize();

			// check whether vote is an ATL or BTL ballot
			if (voteMessage.getRaces().isATL()) {

				// loop over lc ATL preferences
				for (String preference : lcATLPreferences.getPreferencesArray()) {
					if (!preference.equals(com.vvote.messages.fields.MessageFields.VoteMessage.PREFERENCE_IS_BLANK)) {
						// handle lc atl preferences
						preferencesMap.put(Integer.parseInt(preference), currentReducedBallot.getReducedCipher(index));
						// there will only be one so just break here
						break;
					}
					index++;
				}

				ballotPreferencesMap.put(serialNumber, preferencesMap);

				currentIdentifier = new BallotRaceIdentifier(RaceType.LC_ATL, currentReducedBallot.getDistrict());

				if (this.reorderedBallots.containsKey(currentIdentifier)) {
					this.reorderedBallots.get(currentIdentifier).putAll(ballotPreferencesMap);
				} else {
					this.reorderedBallots.put(currentIdentifier, ballotPreferencesMap);
				}

			} else if (lcBTLPreferences.isUsed()) {

				index = config.getLaSize() + config.getLcATLSize();

				// loop over lc BTL preferences
				for (String preference : lcBTLPreferences.getPreferencesArray()) {
					if (!preference.equals(com.vvote.messages.fields.MessageFields.VoteMessage.PREFERENCE_IS_BLANK)) {
						// handle lc btl preferences
						preferencesMap.put(Integer.parseInt(preference), currentReducedBallot.getReducedCipher(index));
					}
					index++;
				}

				ballotPreferencesMap.put(serialNumber, preferencesMap);

				currentIdentifier = new BallotRaceIdentifier(RaceType.LC_BTL, currentReducedBallot.getDistrict());

				if (this.reorderedBallots.containsKey(currentIdentifier)) {
					this.reorderedBallots.get(currentIdentifier).putAll(ballotPreferencesMap);
				} else {
					this.reorderedBallots.put(currentIdentifier, ballotPreferencesMap);
				}
			}
		}

		logger.debug("Successfully reordered reduced ballots");
		resultsLogger.info("Successfully reordered reduced ballots");

		return true;
	}

	/**
	 * Verifies that each of the reduced ballots has the required number of
	 * candidates for its race
	 * 
	 * @return true if each of the reduced ballots has the required number of
	 *         candidates
	 */
	public boolean verifyBallotReductions() {

		logger.info("Verifying that the ballots have been reduced successfully");

		VotingProcess currentVote = null;
		DistrictConfig districtConfig = null;

		ReducedBallot currentReducedBallot = null;

		boolean isValid = true;
		// loop over serial numbers and verify the reductions have been
		// worked
		// out correctly
		for (String serialNumber : this.getDataStore().getVotingProcesses().keySet()) {
			currentVote = this.getDataStore().getVotingProcesses().get(serialNumber);
			currentReducedBallot = this.reducedBallots.get(serialNumber);

			districtConfig = this.getDataStore().getDistrictConfigByName(currentVote.getVoteMessage().getDistrict());

			if (districtConfig == null) {
				logger.error("Unable to verify ballot reductions - could not get district configuration data for district: {}", currentVote.getVoteMessage().getDistrict());
				resultsLogger.error("Unable to verify ballot reductions - could not get district configuration data for district: {}", currentVote.getVoteMessage().getDistrict());
				return false;
			}

			// check that all reduced ballots are now of the correct size
			// for
			// their district
			if (currentReducedBallot.getReducedCiphers().size() != districtConfig.getNumberOfCandidates()) {
				logger.error("Reduced ballot: {} does not have the required number of candidates: {}", serialNumber, districtConfig.getNumberOfCandidates());
				isValid = false;
			}

			// check that the reduction was carried out correctly
			if (!currentReducedBallot.isValid()) {
				logger.error("Reduced ballot with serial number: {} is not valid - verifying the reductions was not successful", currentReducedBallot.getSerialNo());
				resultsLogger.error("Reduced ballot with serial number: {} is not valid - verifying the reductions was not successful", currentReducedBallot.getSerialNo());
				isValid = false;
			}
		}

		if (isValid) {
			logger.debug("Verified each reduced ballot is the right length for its district and race");
			resultsLogger.info("Verified each reduced ballot is the right length for its district and race");
		} else{
			logger.debug("Could not successfully verify each of the reduced ballots");
			resultsLogger.info("Could not successfully verify each of the reduced ballots");
		}

		return isValid;
	}

	/**
	 * Reduces the generic ballots (<code>CommittedBallot</code>) using the
	 * ballot reductions within <code>PODMessage</code> objects
	 * 
	 * @return true if the ballots were reduced correctly
	 */
	public boolean reduceBallots() {

		logger.info("Verifying that ballots can be reduced successfully");

		VotingProcess currentVote = null;

		DistrictConfig districtConfig = null;

		ReducedBallot currentReducedBallot = null;

		CommittedBallot currentCommittedBallot = null;

		List<ElGamalECPoint> baseEncryptedIds = this.getDataStore().getBaseEncryptedIds();
		ECPoint publicKey = this.getDataStore().getPublicKey();

		try {

			// loop over each vote process
			for (String serialNumber : this.getDataStore().getVotingProcesses().keySet()) {

				// get current vote process
				currentVote = this.getDataStore().getVotingProcesses().get(serialNumber);

				// get generated and committed ballot
				currentCommittedBallot = this.getCommittedBallot(serialNumber);

				if (currentCommittedBallot == null) {
					logger.error("Unable to reduce ballots for ballot: {}", serialNumber);
					resultsLogger.error("Unable to reduce ballots for ballot: " + serialNumber);
					return false;
				}

				// get the corresponding district config
				districtConfig = this.getDataStore().getDistrictConfigByName(currentVote.getVoteMessage().getDistrict());

				if (districtConfig == null) {
					logger.error("Unable to reduce ballots - could not get district configuration data for district: {}", currentVote.getVoteMessage().getDistrict());
					resultsLogger.error("Unable to reduce ballots - could not get district configuration data for district: {}", currentVote.getVoteMessage().getDistrict());
					return false;
				}

				// create a new reduced ballot
				currentReducedBallot = new ReducedBallot(currentCommittedBallot, currentVote.getPodMessage(), baseEncryptedIds, publicKey, this.genericBallotSizes, districtConfig);
				
				this.reducedBallots.put(serialNumber, currentReducedBallot);
			}
		} catch (VotePackingException e) {
			logger.error("Unable to reduce ballots", e);
			resultsLogger.error("Unable to reduce ballots", e);
			return false;
		}

		logger.debug("Successfully verified ballot reductions");
		resultsLogger.info("Successfully verified ballot reductions");

		return true;
	}

	/**
	 * Get the committed ballot for the provided serial number
	 * 
	 * @param serialNumber
	 * @return the committed ballot for the provided serial number
	 */
	private CommittedBallot getCommittedBallot(String serialNumber) {

		BallotGenCommit commit = null;

		// loops over all commitment identifiers
		for (CommitIdentifier identifier : this.getDataStore().getGeneratedCiphers().keySet()) {

			// get the commitment
			commit = this.getDataStore().getGeneratedCiphers().get(identifier);

			// checks whether the serial number appears in the committed ballots
			if (commit.getCommittedBallotsSerialNumbers().contains(serialNumber)) {
				return commit.getCommittedBallot(serialNumber);
			}
		}

		return null;
	}
}
