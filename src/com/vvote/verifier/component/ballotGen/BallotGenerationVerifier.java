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

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.cert.CertException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.CryptoConstants;
import com.vvote.JSONConstants;
import com.vvote.commits.CommitIdentifier;
import com.vvote.datafiles.commits.auditcommit.BallotAuditCommit;
import com.vvote.datafiles.commits.auditcommit.BallotGenerationRandomness;
import com.vvote.datafiles.commits.auditcommit.OpenedRandomnessCommitments;
import com.vvote.datafiles.commits.auditcommit.RandomnessPair;
import com.vvote.datafiles.commits.auditcommit.WBBSignature;
import com.vvote.datafiles.commits.gencommit.CommittedBallot;
import com.vvote.datafiles.commits.mixrandomcommit.MixCommitData;
import com.vvote.datafiles.commits.mixrandomcommit.MixRandomCommit;
import com.vvote.datafiles.commits.mixrandomcommit.RandomnessServerCommits;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.ec.ElGamalECPointComparator;
import com.vvote.ec.IndexedElGamalECPoint;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.component.ComponentVerifier;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.ComponentVerifierException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;
import com.vvote.verifierlibrary.exceptions.CommitException;
import com.vvote.verifierlibrary.exceptions.FileHashException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.comparators.BallotSerialNumberComparator;
import com.vvote.verifierlibrary.utils.crypto.CryptoUtils;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;
import com.vvote.verifierlibrary.utils.crypto.bls.BLSCombiner;

/**
 * BallotGenerationVerifier is used for carrying out validation and verification
 * of the ballot generation process carried out by each client.
 * BallotGenerationVerifier is important to ensure that each PoD Printer has
 * performed honestly. PoD Printers may influence the produced generic ballot
 * ciphers and must therefore be audited to ensure they have acted honestly.
 * 
 * The Ballot Generation confirmation checking process provides an assumption
 * that each PoD Printer has acted honestly by auditing a suitably large number
 * of randomly and unpredictably chosen ballots, which if shown to be correct,
 * provide confidence in the accuracy of those generic ballots not checked which
 * can then be used for voting purposes with some kind of an assurance that they
 * have been reliably and honestly produced by each of the audited PoD Printers.
 * 
 * @author James Rumble
 * 
 */
public class BallotGenerationVerifier extends ComponentVerifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenerationVerifier.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Create a SHA 256 message digest array of a specified size
	 * 
	 * @param size
	 * @return an array list of initialised message digests
	 */
	private static ArrayList<MessageDigest> createMessageDigestArray(int size) {
		ArrayList<MessageDigest> digestArray = new ArrayList<MessageDigest>();

		for (int i = 0; i < size; i++) {
			try {
				digestArray.add(MessageDigest.getInstance("SHA-256"));
			} catch (NoSuchAlgorithmException e) {
				logger.error("No such algorithm: {}", e);
				return null;
			}
		}

		return digestArray;
	}

	/**
	 * Gets the permutation for a list of IndexedElGamalECPoint objects. Gets a
	 * permutation to represent the candidate id ordering after the
	 * re-encryptions and sorting has taken place
	 * 
	 * @param combinedBallotCiphers
	 * @return the permutation constructed
	 */
	private static String getPermutation(List<IndexedElGamalECPoint> combinedBallotCiphers) {
		StringBuilder builder = new StringBuilder();

		boolean isFirst = true;
		for (IndexedElGamalECPoint currentPoint : combinedBallotCiphers) {
			if (!isFirst) {
				builder.append(JSONConstants.PREFERENCE_SEPARATOR);
			} else {
				isFirst = false;
			}
			builder.append(currentPoint.getIndex());
		}
		builder.append(JSONConstants.RACE_SEPARATOR);
		return builder.toString();
	}

	/**
	 * Holds the combined randomness values. Holds a map of serialNo : a list of
	 * combined randomness values
	 */
	private Map<String, List<MessageDigest>> combinedRandomness = null;

	/**
	 * Constructor for a ballot generation verifier component
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws ComponentVerifierException
	 * @throws VerifierException
	 */
	public BallotGenerationVerifier(BallotGenDataStore dataStore, BallotGenerationVerifierSpec spec) throws ComponentVerifierException, VerifierException {
		super(dataStore, spec);

		logger.info("Setting up the Ballot Generation Verifier");

		CryptoUtils.initProvider();

		ECUtils.changeCurve(CryptoConstants.BallotGenerationVerifier.CURVE_NAME);

		this.combinedRandomness = new HashMap<String, List<MessageDigest>>();
	}

	/**
	 * Constructor for a BallotGenerationVerifier from JSONObject
	 * representations of the spec objects
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws VerifierException
	 * @throws SpecException
	 */
	public BallotGenerationVerifier(BallotGenDataStore dataStore, JSONObject spec) throws ComponentVerifierException, ComponentSpecException, VerifierException, SpecException {
		this(dataStore, new BallotGenerationVerifierSpec(spec));
	}

	/**
	 * Constructor for a BallotGenerationVerifier from string representations of
	 * the spec objects
	 * 
	 * @param dataStore
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws VerifierException
	 * @throws SpecException
	 */
	public BallotGenerationVerifier(BallotGenDataStore dataStore, String spec) throws ComponentVerifierException, ComponentSpecException, VerifierException, SpecException {
		this(dataStore, new BallotGenerationVerifierSpec(spec));
	}

	/**
	 * Constructor for a BallotGenerationVerifier from string representations of
	 * the spec objects
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws ComponentDataStoreException
	 * @throws VerifierException
	 * @throws SpecException
	 * @throws DataStoreException
	 */
	public BallotGenerationVerifier(String spec, String basePath, boolean useExtraCommits) throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, VerifierException,
			DataStoreException, SpecException {
		this(new BallotGenDataStore(new BallotGenerationVerifierSpec(spec), basePath, useExtraCommits), new BallotGenerationVerifierSpec(spec));
	}

	/**
	 * Constructor for a BallotGenerationVerifier from JSONObject
	 * representations of the spec objects
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws ComponentSpecException
	 * @throws ComponentVerifierException
	 * @throws ComponentDataStoreException
	 * @throws VerifierException
	 * @throws SpecException
	 * @throws DataStoreException
	 */
	public BallotGenerationVerifier(JSONObject spec, String basePath, boolean useExtraCommits) throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException,
			VerifierException, DataStoreException, SpecException {
		this(new BallotGenDataStore(spec, basePath, useExtraCommits), new BallotGenerationVerifierSpec(spec));
	}

	/**
	 * Combine the randomness values received by each of the PoD Printers from
	 * each of the mix servers resulting in a single randomness value produced
	 * from the combination of the randomness values from each of the mix
	 * servers
	 * 
	 * @return true if the combined randomness values are calculated correctly
	 */
	public boolean combineRandomnessValues() {

		logger.debug("Combining randomness values");

		this.combinedRandomness = new HashMap<String, List<MessageDigest>>();

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			// loop over the ballots to audit
			for (String serialNumber : auditCommit.getRandomnessCommitments().keySet()) {

				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (!this.combineRandomnessValues(currentBallotRandomness, identifier)) {
					return false;
				}
			}
		}

		logger.debug("Successfully combined randomness values");
		resultsLogger.info("Successfully combined randomness values");

		return true;
	}

	/**
	 * Carries out the specific combination of randomness values for the ballot
	 * specified using the serial number. Combines the randomness values
	 * received to construct the specific ballot from each of the mix servers
	 * resulting in a single randomness value which was actually used to
	 * construct the ballot itself
	 * 
	 * @param currentBallotRandomness
	 * 
	 * @param identifier
	 * @return true if the combined randomness values are calculated correctly
	 */
	public boolean combineRandomnessValues(BallotGenerationRandomness currentBallotRandomness, CommitIdentifier identifier) {
		int randomnessValues = this.getDataStore().getNumberOfRandomnessValuesExpected();
		String currentRandomnessValue = null;

		final String serialNo = currentBallotRandomness.getSerialNo();

		logger.debug("Combining randomness values for client: '{}', serialNo: '{}'", identifier, serialNo);

		// produce message digest array/storage for combined
		// randomness values for each ballot to be audited
		this.getCombinedRandomness().put(serialNo, createMessageDigestArray(randomnessValues));

		// loop over each randomness value - which represents each candidate
		// id
		for (int i = 0; i < randomnessValues; i++) {

			// for each randomness value received for the current candidate
			// id
			for (OpenedRandomnessCommitments podOpenedRandomness : currentBallotRandomness.getOpenedRandomnessValues()) {

				// get the current randomness value (used for encryption)
				// from the current mix server
				currentRandomnessValue = podOpenedRandomness.getRandomnessPair(i).getRandomnessValue();

				logger.debug("Combining randomness values for client: '{}', serialNo: '{}' - randomness value: '{}'", identifier, serialNo, currentRandomnessValue);

				// update the randomness value for the correct candidate id
				// at the correct serial number
				this.getCombinedRandomness().get(serialNo).get(i).update(Utils.decodeHexData(currentRandomnessValue));
			}
		}

		logger.debug("Successfully combined randomness values for client: '{}', serialNo: '{}'", identifier, serialNo);

		return true;
	}

	/**
	 * Combines the randomness values for a specific ballot
	 * 
	 * @param serialNumber
	 * @return true if the randomness values are combined successfully
	 */
	public boolean combineRandomnessValues(String serialNumber) {

		if (!this.isAuditBallot(serialNumber)) {
			logger.info("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {
				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (this.combineRandomnessValues(currentBallotRandomness, identifier)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean doVerification() {

		logger.info("Starting Ballot Generation Verification");
		resultsLogger.info("Starting Ballot Generation verification");

		boolean verified = super.doVerification();

		try {
			if (!this.verifyNumberOfRandomnessValuesReceivedByPODPrinters()) {
				verified = false;
			}

			if (!this.verifyNumberOfBallotsToAudit()) {
				verified = false;
			}

			if (!this.verifyFiatShamirCalculation()) {
				verified = false;
			}

			BallotAuditCommit auditCommit = null;

			BallotGenerationRandomness currentBallotRandomness = null;

			logger.info("Starting the verification of each ballot chosen for Ballot Generation Auditing");

			// loop over each printer to audit
			for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
				auditCommit = this.getDataStore().getAuditData().get(identifier);

				logger.debug("Starting the verification of Public WBB commitment with identifier: {} for outer attachment file: {}, inner attachment file: {}", identifier, auditCommit.getAttachmentFilePath(), auditCommit.getMessage().getFileName());
				resultsLogger.info("Starting the verification of Public WBB commitment with identifier: {} for outer attachment file: {}, inner attachment file: {}", identifier, auditCommit.getAttachmentFilePath(), auditCommit.getMessage().getFileName());

				// loop over the ballots to audit
				for (String serialNumber : auditCommit.getRandomnessCommitments().keySet()) {

					currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

					if (!this.verifyRandomness(currentBallotRandomness, identifier)) {
						verified = false;
					}

					if (!this.combineRandomnessValues(currentBallotRandomness, identifier)) {
						verified = false;
					}

					if (!this.verifyEncryptions(currentBallotRandomness, identifier)) {
						verified = false;
					}
				}
			}
		} catch (CommitException e) {
			logger.error("Unable to continue verification.", e);
			resultsLogger.error("Unable to continue verification.", e);
			return false;
		}

		if (verified) {
			logger.debug("Ballot Generation Verification was carried out successfully");
			resultsLogger.info("Ballot Generation Verification was carried out successfully");
		} else {
			logger.debug("Ballot Generation Verification was not carried out successfully. The data provided needs to be checked in addition to the logs");
			resultsLogger.info("Ballot Generation Verification was not carried out successfully. The data provided needs to be checked in addition to the logs");
		}

		return verified;
	}

	/**
	 * Carries out the verification for a single ballot with the provided serial
	 * number
	 * 
	 * @param serialNumber
	 * @return true if the verification was successful for the ballot with the
	 *         provided serial number
	 */
	public boolean doVerification(String serialNumber) {

		logger.info("Starting Ballot Generation Verification for ballot with serial number: {}", serialNumber);

		if (!this.isAuditBallot(serialNumber)) {
			logger.error("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			resultsLogger.error("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		boolean verified = super.doVerification();

		try {
			if (!this.verifyNumberOfRandomnessValuesReceivedByPODPrinters(serialNumber)) {
				verified = false;
			}

			if (!this.verifyNumberOfRandomnessValuesCommittedToByMixServers(serialNumber)) {
				verified = false;
			}

			BallotAuditCommit auditCommit = null;

			// loop over each printer to audit
			for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
				auditCommit = this.getDataStore().getAuditData().get(identifier);

				if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {

					if (!this.verifyRandomness(serialNumber)) {
						verified = false;
					}

					if (!this.combineRandomnessValues(serialNumber)) {
						verified = false;
					}

					if (!this.verifyEncryptions(serialNumber)) {
						verified = false;
					}
				}
			}

		} catch (CommitException e) {
			logger.error("Unable to continue verification.", e);
			return false;
		}

		if (verified) {
			logger.debug("Ballot Generation Verification was carried out successfully for ballot with serial number: {}", serialNumber);
			resultsLogger.info("Ballot Generation Verification was carried out successfully for ballot with serial number: {}", serialNumber);
		} else {
			logger.debug("Ballot Generation Verification was carried out successfully for ballot with serial number: {}", serialNumber);
			resultsLogger.info("Ballot Generation Verification was not carried out successfully for ballot with serial number: {}", serialNumber);
		}

		return verified;
	}

	/**
	 * Getter for the combined randomness values - these are computed by
	 * performing hash computations on combined randomness values
	 * 
	 * @return the combined randomness values
	 */
	private Map<String, List<MessageDigest>> getCombinedRandomness() {
		return this.combinedRandomness;
	}

	@Override
	public BallotGenDataStore getDataStore() {
		if (super.getDataStore() instanceof BallotGenDataStore) {
			return (BallotGenDataStore) super.getDataStore();
		}
		return null;
	}

	@Override
	public BallotGenerationVerifierSpec getSpec() {
		if (super.getSpec() instanceof BallotGenerationVerifierSpec) {
			return (BallotGenerationVerifierSpec) super.getSpec();
		}
		return null;
	}

	/**
	 * Perform a re-encryption and sort on a sub section of the candidate ids
	 * using the counters passed in
	 * 
	 * @param combinedRandomness
	 * @param numberOfReencryptions
	 * @param currentRandomIndex
	 * @return a sorted and re-encrypted list of IndexedElGamalECPoint objects
	 */
	private List<IndexedElGamalECPoint> reencryptAndSort(List<MessageDigest> combinedRandomness, int numberOfReencryptions, int currentRandomIndex) {

		logger.debug("Performing re-encryption and sorting for subsection of candidate ids");
		logger.debug("Number of re-encryptions to carry out: '{}'", numberOfReencryptions);
		logger.debug("Starting index for where to start re-encryptions: '{}'", currentRandomIndex);

		List<IndexedElGamalECPoint> result = new ArrayList<IndexedElGamalECPoint>();

		for (int i = 0; i < numberOfReencryptions; i++) {

			logger.debug("Re-encrypting candidate: '{}'", i + currentRandomIndex);

			// current random value
			BigInteger randValue = new BigInteger(1, combinedRandomness.get(i + currentRandomIndex).digest());

			// current base candidate identifier
			ElGamalECPoint baseCandidateId = this.getDataStore().getBaseEncryptedIds().get(i + currentRandomIndex);

			// current re-encrypted base candidate identifier
			ElGamalECPoint reencryptedCandidateId = ECUtils.reencrypt(baseCandidateId, this.getDataStore().getPublicKey(), randValue);

			// indexed re-encrypted base candidate identifier storing the
			// original position
			IndexedElGamalECPoint indexedEC = new IndexedElGamalECPoint(reencryptedCandidateId, i);

			result.add(i, indexedEC);

		}

		logger.debug("Sorting re-encrypted candidate ids");
		Collections.sort(result, new ElGamalECPointComparator());

		return result;
	}

	/**
	 * Performs verification of the re-encryptions and sorting of the base
	 * encrypted candidate ids. A permutation string is also re-computed and
	 * used in a hash commitment check.
	 * 
	 * @return true if the re-encryption and sorting takes place successfully.
	 * @throws CommitException
	 */
	public boolean verifyEncryptions() throws CommitException {

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			// loop over the ballots to audit
			for (String serialNumber : auditCommit.getRandomnessCommitments().keySet()) {

				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (!this.verifyEncryptions(currentBallotRandomness, identifier)) {
					return false;
				}
			}
		}

		logger.debug("Re-encrypted and sorted base candidate ids match those commited to by the mix servers");
		resultsLogger.info("Re-encrypted and sorted base candidate ids match those commited to by the mix servers");

		return true;
	}

	/**
	 * Performs the actual re-encryption and sorting of the base encrypted
	 * candidate ids for a specific serial number. The committed permutation
	 * string is then also checked using the combined randomness values
	 * 
	 * @param currentBallotRandomness
	 * @param identifier
	 * @return true if the re-encryption and sorting takes place successfully
	 *         for the specific ballot
	 * @throws CommitException
	 */
	public boolean verifyEncryptions(BallotGenerationRandomness currentBallotRandomness, CommitIdentifier identifier) throws CommitException {

		final String serialNo = currentBallotRandomness.getSerialNo();

		logger.info("Starting Verification of the encryptions and construction of the generic ballots for ballot with serial number: {} for PoD Printer: {}", serialNo, identifier.getPrinterId());

		// stores the re-encryptions
		List<IndexedElGamalECPoint> laReencryptions = null;
		List<IndexedElGamalECPoint> lcATLReencryptions = null;
		List<IndexedElGamalECPoint> lcBTLReencryptions = null;

		// stores the appropriate sizes of each race
		int[] sizes = new int[] { this.getDataStore().getBallotGenerationConfig().getLASize(), this.getDataStore().getBallotGenerationConfig().getLcATLSize(),
				this.getDataStore().getBallotGenerationConfig().getLcBTLSize() };

		// stores the sections of sorted re-encrypted base encrypted ids
		List<IndexedElGamalECPoint> combinedBallotCiphers = null;

		// stores the current combined randomness values
		List<MessageDigest> currentRandomnessList = null;

		// stores the details for the current committed cipher ballot
		CommittedBallot currentBallot = null;

		int numberOfReencryptions = 0;
		int currentRandomIndex = 0;

		byte[] witness = null;
		byte[] randomnessValue = null;
		byte[] commit = null;

		logger.debug("Veriyfing re-encryptions for client: '{}'", serialNo);

		// get the combined randomness values for the current serial
		// number/client
		currentRandomnessList = this.getCombinedRandomness().get(serialNo);

		// perform re-encryption and sorting on the LA race
		numberOfReencryptions = sizes[0];
		laReencryptions = this.reencryptAndSort(currentRandomnessList, numberOfReencryptions, currentRandomIndex);

		// perform re-encryption and sorting on the LC ATL race
		currentRandomIndex += numberOfReencryptions;
		numberOfReencryptions = sizes[1];
		lcATLReencryptions = this.reencryptAndSort(currentRandomnessList, numberOfReencryptions, currentRandomIndex);

		// perform re-encryption and sorting on the LC BTL race
		currentRandomIndex += numberOfReencryptions;
		numberOfReencryptions = sizes[2];
		lcBTLReencryptions = this.reencryptAndSort(currentRandomnessList, numberOfReencryptions, currentRandomIndex);

		// combine all re-encrypted and sorted ids together
		combinedBallotCiphers = new ArrayList<IndexedElGamalECPoint>();
		combinedBallotCiphers.addAll(laReencryptions);
		combinedBallotCiphers.addAll(lcATLReencryptions);
		combinedBallotCiphers.addAll(lcBTLReencryptions);

		// get the permutation string for each set of re-encryptions
		StringBuilder permutationString = new StringBuilder();
		permutationString.append(getPermutation(laReencryptions));
		permutationString.append(getPermutation(lcATLReencryptions));
		permutationString.append(getPermutation(lcBTLReencryptions));

		// get the current committed cipher ballot
		currentBallot = this.getDataStore().getGeneratedCiphers().get(identifier).getCommittedBallot(serialNo);

		logger.debug("Current commited ballot: '{}'", currentBallot);

		// loop through the cipher texts and check they match the
		// corresponding combined ballot
		for (int i = 0; i < currentBallot.getCiphers().size(); i++) {
			logger.debug("Checking ballot with serial number: '{}' and index: '{}'", serialNo, i);

			if (!currentBallot.getCiphers().get(i).equals(combinedBallotCiphers.get(i))) {

				logger.error("Committed cipher and combined ballot cipher for serial number: '{}' with index: '{}' do not match", serialNo, i);
				resultsLogger.error("Committed cipher and combined ballot cipher for serial number: '{}' with index: '{}' do not match", serialNo, i);

				return false;
			}

			logger.debug("Check was successful for ballot with serial number: '{}' and index: '{}'", serialNo, i);
		}

		logger.debug("Performing hash commitment check on ballot with serial number: '{}'", serialNo);

		// perform a hash commitment check using the last randomness value
		witness = currentRandomnessList.get(currentRandomnessList.size() - 1).digest();
		randomnessValue = permutationString.toString().getBytes();
		commit = Utils.decodeBase64Data(currentBallot.getPermutation());

		// check the hash commitment
		if (!CryptoUtils.verifyHashCommitment(commit, witness, randomnessValue)) {
			logger.error("Hash commitment check was unsucessfull for combined ballot cipher with serial number: '{}'", serialNo);
			resultsLogger.error("Hash commitment check was unsucessfull for combined ballot cipher with serial number: '{}'", serialNo);

			return false;
		}

		logger.debug("Re-encryption and sorting was successful for ballot with serial number: '{}'. The generic ballot was generated successfully by PoD Printer: {}", serialNo,
				identifier.getPrinterId());
		resultsLogger.info("Re-encryption and sorting was successful for ballot with serial number: '{}'. The generic ballot was generated successfully by PoD Printer: {}", serialNo,
				identifier.getPrinterId());

		return true;
	}

	/**
	 * Verifies encryptions for a specific ballot with the provided serial
	 * number
	 * 
	 * @param serialNumber
	 * @return true if the encryptions were carried out successfully
	 * @throws CommitException
	 */
	public boolean verifyEncryptions(String serialNumber) throws CommitException {

		if (!this.isAuditBallot(serialNumber)) {
			logger.info("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {
				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (this.verifyEncryptions(currentBallotRandomness, identifier)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Verifies the fiat shamir calculation
	 * 
	 * @return true if the fiat shamir calculation matches that which is
	 *         included in the ballot submit response message for the current
	 *         commit
	 */
	public boolean verifyFiatShamirCalculation() {

		logger.info("Starting Verification of the Fiat Shamir signature which determines the ballots chosen for auditing by each PoD Printer");

		final int ballotsToGenerate = this.getDataStore().getBallotGenerationConfig().getBallotsToGenerate();
		final int ballotsToAudit = this.getDataStore().getBallotGenerationConfig().getBallotsToAudit();

		List<String> serialNumbers = null;

		BallotAuditCommit auditCommit = null;

		boolean verified = true;

		String currentBoothId = null;

		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {

			logger.info("Verifying the Fiat-Shamir signature for commitment with identifier: {}", identifier);

			serialNumbers = new ArrayList<String>(this.getDataStore().getGeneratedCiphers().get(identifier).getCommittedBallotsSerialNumbers());

			// need to sort the serial numbers to make sure they are in a
			// 'default'
			// state i.e. in order
			Collections.sort(serialNumbers, new BallotSerialNumberComparator());

			// check generation size
			if (serialNumbers.size() != ballotsToGenerate) {
				logger.error("The number of ballots generated ({}) doesn't match the number of ballots requested for generation ({})", serialNumbers.size(), ballotsToGenerate);
				resultsLogger.error("The number of ballots generated ({}) doesn't match the number of ballots requested for generation ({})", serialNumbers.size(), ballotsToGenerate);

				return false;
			}

			auditCommit = this.getDataStore().getAuditData().get(identifier);

			currentBoothId = auditCommit.getMessage().getBoothID();

			// verify sig is created properly:
			try {
				if (!verifySignatureMatches(identifier, auditCommit)) {
					verified = false;
				}
			} catch (NoSuchAlgorithmException | NoSuchProviderException | FileHashException e) {
				logger.error("The Fiat Shamir signature couldn't be calculated. Check the supplied data", e);
				resultsLogger.error("The Fiat Shamir signature couldn't be calculated. Check the supplied data", e);
				return false;
			}

			logger.info("Fiat shamir signature was recalculated and matches what was included in the BallotSubmitResponse message for printer: {}", currentBoothId);

			logger.info("Verifying that the ballots chosen for auditing were correctly chosen for commitment with identifier: {}", identifier);

			final byte[] fiatShamirSig = Utils.decodeBase64Data(auditCommit.getResponse().getFiatShamir());

			// use fiat shamir sig as the seed for the deterministic random bit
			// generator
			FixedSecureRandom fixedSecureRandom = new FixedSecureRandom(fiatShamirSig);

			SP800SecureRandomBuilder randomBuilder = new SP800SecureRandomBuilder(fixedSecureRandom, false);
			randomBuilder.setPersonalizationString(auditCommit.getResponse().getPeerID().getBytes());
			SP800SecureRandom sp800SecureRandom = randomBuilder.buildHash(new SHA256Digest(), null, false);

			Collections.shuffle(serialNumbers, sp800SecureRandom);

			ArrayList<String> serialNumbersToAudit = new ArrayList<String>();

			for (int i = 0; i < ballotsToAudit; i++) {
				serialNumbersToAudit.add(serialNumbers.get(i));
			}

			if (serialNumbersToAudit.size() != ballotsToAudit) {
				logger.error("The number of serial numbers calculated for auditing does not match the number of serial numbers requested for auditing for commitment with identifier: {}", identifier);
				resultsLogger.error("The number of serial numbers calculated for auditing does not match the number of serial numbers requested for auditing for commitment with identifier: {}",
						identifier);
				verified = false;
			}

			if (auditCommit.getRandomnessCommitments().keySet().size() != ballotsToAudit) {
				logger.error("The number of serial numbers included in the audit file doesn't match the number of serial numbers requested for auditing for commitment with identifier: {}", identifier);
				resultsLogger.error("The number of serial numbers included in the audit file doesn't match the number of serial numbers requested for auditing for commitment with identifier: {}",
						identifier);
				verified = false;
			}

			if (!auditCommit.getRandomnessCommitments().keySet().containsAll(serialNumbersToAudit)) {
				logger.error(
						"The serial numbers included in the audit file do not match the serial numbers requested for auditing calculated using the fiat shamir signature for commitment with identifier: {}",
						identifier);
				resultsLogger
						.error("The serial numbers included in the audit file do not match the serial numbers requested for auditing calculated using the fiat shamir signature for commitment with identifier: {}",
								identifier);
				verified = false;
			}

			logger.debug("Successfully verified that the serial numbers of ballots for auditing were correctly chosen using the Fiat shamir signature for commitment with identifier: {}", identifier);
			resultsLogger.info("Serial numbers for auditing were checked successfully using the Fiat shamir signature for commitment with identifier: {}", identifier);
		}

		logger.debug("Successfully verified the Fiat Shamir signatures were used to choose the required number of ballots for auditing");
		resultsLogger.info("Successfully verified the Fiat Shamir signatures were used to choose the required number of ballots for auditing");

		return verified;
	}

	/**
	 * Verifies the number of ballots to audit matches the number of ballots
	 * represented/stored
	 * 
	 * @return true if the number of ballots to audit matches the number of
	 *         ballots read in for auditing
	 */
	public boolean verifyNumberOfBallotsToAudit() {

		logger.info("Starting Verification that the number of ballots to required for auditing specified in the configuration file matches the number of ballots for auditing found");

		BallotAuditCommit auditCommit = null;

		final int ballotsToAudit = this.getDataStore().getBallotGenerationConfig().getBallotsToAudit();

		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (ballotsToAudit != auditCommit.getRandomnessCommitments().size()) {
				logger.error("The number of ballots required for auditing ({}) does not match the number of audit ballots found ({}) for printer: ({})", ballotsToAudit, auditCommit
						.getRandomnessCommitments().size(), identifier.getPrinterId());
				resultsLogger.error("The number of ballots required for auditing ({}) does not match the number of audit ballots found ({}) for printer: ({})", ballotsToAudit, auditCommit
						.getRandomnessCommitments().size(), identifier.getPrinterId());

				return false;
			}
		}

		logger.debug("Successfully verified that the number of ballots required for auditing were provided");
		resultsLogger.info("Successfully verified that the number of ballots required for auditing were provided");

		return true;
	}

	/**
	 * Verifies the number of randomness values committed to for each mix server
	 * 
	 * @param serialNumber
	 * 
	 * @return true if the number of randomness values committed to for each mix
	 *         server matches the expected number
	 */
	public boolean verifyNumberOfRandomnessValuesCommittedToByMixServers(String serialNumber) {

		logger.info("Starting Verification that the number of randomness values committed by the mix servers matches the number of candidates plus 1 for ballot with serial number: {}", serialNumber);

		if (!this.isAuditBallot(serialNumber)) {
			logger.info("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		Map<CommitIdentifier, List<MixRandomCommit>> serverMap = null;
		List<MixRandomCommit> commits = null;

		MixCommitData mixCommit = null;

		final int numberOfGenericCandidates = this.getDataStore().getNumberOfRandomnessValuesExpected();

		for (String serverName : this.getDataStore().getMixServerCommits().keySet()) {
			logger.debug("Checking randomness values sent by: {}", serverName);

			serverMap = this.getDataStore().getMixServerCommits().get(serverName);

			for (CommitIdentifier identifier : serverMap.keySet()) {
				logger.debug("Checking randomness values sent to: {}", identifier);

				commits = serverMap.get(identifier);

				for (MixRandomCommit currentCommit : commits) {

					logger.debug("Checking randomness in submission with id: {}", currentCommit.getMessage().getSubmissionId());

					if (currentCommit.getServerCommits().hasMixRandomCommit(serialNumber)) {
						mixCommit = currentCommit.getServerCommits().getMixRandomCommit(serialNumber);

						// check the number of randomness values received
						if (numberOfGenericCandidates != mixCommit.getNumberOfRandomnessValues()) {
							logger.error("The current number of randomness values received does not match the number of candidates plus 1. Server: '{}' for ballot: '{}'", serverName,
									mixCommit.getSerialNo());
							resultsLogger.error("The current number of randomness values received does not match the number of candidates plus 1. Server: '{}' for ballot: '{}'", serverName,
									mixCommit.getSerialNo());
							return false;
						}
					}
				}
			}
		}

		logger.debug("Successfully verified that the number of randomness values committed to by the mix servers matches the number of candidates plus 1 for ballot with serial number: {}",
				serialNumber);
		resultsLogger.info("Successfully verified that the number of randomness values committed to by the mix servers matches the number of candidates plus 1 for ballot with serial number: {}",
				serialNumber);

		return true;
	}

	/**
	 * Verifies the number of randomness values received by the PoD Printers
	 * 
	 * @return true if the number of randomness values received matches that
	 *         expected
	 */
	public boolean verifyNumberOfRandomnessValuesReceivedByPODPrinters() {

		logger.info("Starting Verification that the number of opened randomness commitments received by the PoD Printers matches the number of candidates plus 1");

		BallotAuditCommit auditCommit = null;
		BallotGenerationRandomness randomnessCommitment = null;

		boolean verified = true;

		final int numberOfCandidatesPlus1 = this.getDataStore().getNumberOfRandomnessValuesExpected();

		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			for (String serialNo : auditCommit.getRandomnessCommitments().keySet()) {
				randomnessCommitment = auditCommit.getRandomnessCommitments().get(serialNo);

				for (OpenedRandomnessCommitments currentOpenedRandomness : randomnessCommitment.getOpenedRandomnessValues()) {
					// check the number of randomness values received
					if (numberOfCandidatesPlus1 != currentOpenedRandomness.getNumRandomnessValues()) {
						logger.error("The current number of opened randomness values ({}) for ballot with serial number: {} from printer: '{}' does not match the number of candidates plus 1 ({})", currentOpenedRandomness.getNumRandomnessValues(), serialNo,
								currentOpenedRandomness.getPeerId(), numberOfCandidatesPlus1);
						resultsLogger.error("The current number of opened randomness values ({}) for ballot with serial number: {} from printer: '{}' does not match the number of candidates plus 1 ({})", currentOpenedRandomness.getNumRandomnessValues(), serialNo,
								currentOpenedRandomness.getPeerId(), numberOfCandidatesPlus1);
						verified = false;
					}
				}
			}
		}

		logger.debug("Successfully verified that the number of opened randomness commitments received by each PoD Printer matches the number of candidates plus 1");
		resultsLogger.info("Successfully verified that the number of opened randomness commitments received by each PoD Printer matches the number of candidates plus 1");

		return verified;
	}

	/**
	 * Verifies the number of randomness values received by the PoD Printers
	 * 
	 * @param serialNumber
	 * 
	 * @return true if the number of randomness values received matches that
	 *         expected
	 */
	public boolean verifyNumberOfRandomnessValuesReceivedByPODPrinters(String serialNumber) {

		logger.info("Starting Verification that the number of opened randomness commitments received by the PoD Printers matches the number of candidates plus 1 for ballot with serial number: {}",
				serialNumber);

		if (!this.isAuditBallot(serialNumber)) {
			logger.info("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		BallotAuditCommit auditCommit = null;
		BallotGenerationRandomness randomnessCommitment = null;

		final int numberOfCandidates = this.getDataStore().getNumberOfRandomnessValuesExpected();

		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {
				randomnessCommitment = auditCommit.getRandomnessCommitments().get(serialNumber);

				for (OpenedRandomnessCommitments currentOpenedRandomness : randomnessCommitment.getOpenedRandomnessValues()) {
					// check the number of randomness values received
					if (numberOfCandidates != currentOpenedRandomness.getNumRandomnessValues()) {
						logger.error("The current number of opened randomness values ({}) for ballot with serial number: {} from printer: '{}' does not match the number of candidates plus 1 ({})", currentOpenedRandomness.getNumRandomnessValues(), serialNumber,
								currentOpenedRandomness.getPeerId(), numberOfCandidates);
						resultsLogger.error("The current number of opened randomness values ({}) for ballot with serial number: {} from printer: '{}' does not match the number of candidates plus 1 ({})", currentOpenedRandomness.getNumRandomnessValues(), serialNumber,
								currentOpenedRandomness.getPeerId(), numberOfCandidates);
						return false;
					}
				}
			}
		}

		logger.debug("Successfully verified that the number of opened randomness commitments received by each PoD Printer matches the number of candidates plus 1 for ballot with serial number: {}",
				serialNumber);
		resultsLogger.info(
				"Successfully verified that the number of opened randomness commitments received by each PoD Printer matches the number of candidates plus 1 for ballot with serial number: {}",
				serialNumber);

		return true;
	}

	/**
	 * Carry out the verification on the randomness values
	 * 
	 * We verify the randomness data stored by the PoD Printer (Once they have
	 * opened their commitments) is consistent with those commitments made by
	 * the Mix Servers - this can only be done once the commitments to the
	 * randomness values have been opened by the PoD Printers which will only
	 * happen if the ballot is chosen for auditing. The same ballot cannot then
	 * be used for voting purposes
	 * 
	 * @return true if the randomness commitments made by the POD printers match
	 *         the re-computed randomness values
	 * @throws CommitException
	 */
	public boolean verifyRandomness() throws CommitException {

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			// loop over the ballots to audit
			for (String serialNumber : auditCommit.getRandomnessCommitments().keySet()) {

				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (!this.verifyRandomness(currentBallotRandomness, identifier)) {
					return false;
				}
			}
		}

		logger.debug("The opened commitments from the POD printers match the randomness commitments made by the mix servers");
		resultsLogger.info("The opened commitments from the POD printers match the randomness commitments made by the mix servers");

		return true;
	}

	/**
	 * Verifies that the randomness values stored by a specified POD Printer
	 * (using the serial number to reference a ballot created by them) - once it
	 * has opened its commitment is consistent with the commitments made by the
	 * mix servers.
	 * 
	 * @param currentBallotRandomness
	 * @param identifier
	 * @return true if the randomness values can be used to open the commitment
	 *         made by the mix servers on the public wbb
	 * @throws CommitException
	 */
	public boolean verifyRandomness(BallotGenerationRandomness currentBallotRandomness, CommitIdentifier identifier) throws CommitException {

		String currentPeerID = null;
		Map<CommitIdentifier, List<MixRandomCommit>> mixServerMap = null;
		MixCommitData currentRandomnessCommit = null;
		RandomnessPair currentRandomPair = null;

		final String serialNo = currentBallotRandomness.getSerialNo();

		logger.info("Starting Verification of the randomness values used by PoD Printer: {} for ballot with serial number: {} match the commitments made by the mix servers",
				identifier.getPrinterId(), serialNo);

		List<MixRandomCommit> currentServerCommits = null;
		RandomnessServerCommits currentServerRandomnessCommits = null;

		String commitment = null;
		String witness = null;
		String randomValue = null;

		boolean randomnessVerified = false;

		logger.debug("Veriyfing randomness values for ballot: '{}'", serialNo);

		// loop over the opened randomness commitments - each ballot to audit
		// may contain a number of OpenedRandomnessCommitments relating to
		// different mix servers (peers)
		for (OpenedRandomnessCommitments podOpenedRandomness : currentBallotRandomness.getOpenedRandomnessValues()) {

			randomnessVerified = false;

			// check that the serial numbers for each inner set match the
			// outer object - this should return positive
			logger.debug("Veriyfing the serial number for each of the inner opened commitments (for different peer ids) shares the same serial number");
			if (!podOpenedRandomness.getSerialNo().equals(serialNo)) {
				logger.error("The opened randomness commitments do not all share the same serial number as they should: first: '{}', second: '{}'", podOpenedRandomness.getSerialNo(), serialNo);
				resultsLogger.error("The opened randomness commitments do not all share the same serial number as they should: first: '{}', second: '{}'", podOpenedRandomness.getSerialNo(), serialNo);

				return false;
			}

			// get the current mix server id
			currentPeerID = podOpenedRandomness.getPeerId();

			logger.debug("Checking randomness for ballot: '{}', with mix server: '{}'", serialNo, currentPeerID);

			// get the current mix server commitments from the current peer
			// id - returns a representation of one of the files relating to
			// a peer/mix server
			mixServerMap = this.getDataStore().getMixServerCommits().get(currentPeerID);

			for (CommitIdentifier mixServerMapIdentifier : mixServerMap.keySet()) {
				if (mixServerMapIdentifier.getPrinterId().equals(identifier.getPrinterId())) {
					currentServerCommits = mixServerMap.get(mixServerMapIdentifier);
				}
			}

			if (currentServerCommits != null) {

				for (int j = 0; j < currentServerCommits.size(); j++) {

					if (!randomnessVerified) {

						MixRandomCommit currentCommit = currentServerCommits.get(j);

						currentServerRandomnessCommits = currentCommit.getServerCommits();

						// gets the representation for the actual line from the
						// file
						// using the serial number
						currentRandomnessCommit = currentServerRandomnessCommits.getMixRandomCommit(serialNo);

						// loop over the randomness pairings for each of the
						// sets of
						// opened randomness commitments for each of the PoD
						// Printers
						for (int i = 0; i < podOpenedRandomness.getNumRandomnessValues(); i++) {

							logger.debug("Verifying hash commitment for ballot: '{}', with mix server: '{}', randomness index: '{}'", serialNo, currentPeerID, i);

							// get the current pair
							currentRandomPair = podOpenedRandomness.getRandomnessPair(i);

							// get commitment from the current mix server file
							commitment = currentRandomnessCommit.getRandomnessValue(i);

							// get the witness and randomness value from the
							// current
							// pair
							witness = currentRandomPair.getWitness();
							randomValue = currentRandomPair.getRandomnessValue();

							// check each commitment
							if (!CryptoUtils.verifyHashCommitment(commitment, witness, randomValue)) {

								if (j < currentServerCommits.size() - 1 && currentServerCommits.size() > 1) {
									resultsLogger
											.warn("The commitment does not match the given witness and randomness value - The current Mix Server has multiple commitments so we will check the next commitment: {}",
													identifier);
									break;
								}

								logger.error("The commitment does not match the given witness and randomness values - Commitment with identifier: {}, commitment: {}, witness: {}, randomness: {}",
										identifier, commitment, witness, randomValue);
								resultsLogger.error(
										"The commitment does not match the given witness and randomness values - Commitment with identifier: {}, commitment: {}, witness: {}, randomness: {}",
										identifier, commitment, witness, randomValue);

								return false;
							}
						}
						randomnessVerified = true;
					}
				}
			} else {
				logger.error("Could not locate the Mix server commit data for the current audit: {}", identifier);
				resultsLogger.error("The commitment does not match the given witness and randomness values - Commitment: {}, witness: {}, randomness: {}", commitment, witness, randomValue);

				return false;
			}
			resultsLogger.info("Successfully verified that the randomness values for ballot: {} were provided by and committed to by mix server: {}", serialNo, currentPeerID);
		}

		logger.debug("Successfully verified that the randomness values for ballot: {} were provided by and committed to by the mix servers", serialNo);
		resultsLogger.info("Successfully verified that the randomness values for ballot: {} were provided by and committed to by the mix servers", serialNo);

		return true;
	}

	/**
	 * Verifies the randomness values for a single ballot with the provided
	 * serial number
	 * 
	 * @param serialNumber
	 * @return true if the randomness values are valid for the ballot with the
	 *         specific serial number
	 * @throws CommitException
	 */
	public boolean verifyRandomness(String serialNumber) throws CommitException {

		if (!this.isAuditBallot(serialNumber)) {
			logger.info("Ballot specified with serial number: {} was not chosen for auditing and it is therefore not possible to verify this ballot", serialNumber);
			return false;
		}

		BallotAuditCommit auditCommit = null;

		BallotGenerationRandomness currentBallotRandomness = null;

		// loop over each printer to audit
		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {
				currentBallotRandomness = auditCommit.getRandomnessCommitments().get(serialNumber);

				if (!this.verifyRandomness(currentBallotRandomness, identifier)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Verifies that the fiat shamir signatures match - one is looked up from
	 * the ballot submission response message and one is recalculated using
	 * publicly available data
	 * 
	 * @param identifier
	 * 
	 * @param auditCommit
	 * @return true if the signatures match
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws FileHashException
	 */
	private boolean verifySignatureMatches(CommitIdentifier identifier, BallotAuditCommit auditCommit) throws NoSuchAlgorithmException, NoSuchProviderException, FileHashException {

		final String ciphersFilePath = this.getDataStore().getGeneratedCiphers().get(identifier).getCiphersDataFilePath();

		final byte[] includedSig = Utils.decodeBase64Data(auditCommit.getResponse().getFiatShamir());

		MessageDigest fiatShamirDigest = MessageDigest.getInstance(CryptoConstants.FiatShamirSignature.FIAT_SHAMIR_HASH_ALGORITHM, CryptoConstants.FiatShamirSignature.FIAT_SHAMIR_PROVIDER);

		// add client id
		fiatShamirDigest.update(auditCommit.getResponse().getPeerID().getBytes());

		// add message id
		// message id of commit ciphers message BallotGenCommit
		fiatShamirDigest.update(auditCommit.getResponse().getSubmissionID().getBytes());

		// add commit time
		fiatShamirDigest.update(auditCommit.getMessage().getCommitTime().getBytes());

		// add ciphers file
		CryptoUtils.hashFile(new File(ciphersFilePath), fiatShamirDigest);

		// add combined sig
		String combinedSig = this.getCombinedSignature(auditCommit);
		fiatShamirDigest.update(Utils.decodeBase64Data(combinedSig));

		final byte[] fiatShamirSig = fiatShamirDigest.digest();

		// check whether the combined hash and the given commitment are
		// equal
		if (!Arrays.equals(fiatShamirSig, includedSig)) {
			logger.error("Calculated fiat shamir signature ({}) didn't match the one found in the ballot submit response message ({}) for PoD Printer: {} in commitment with identifier: {}",
					Utils.byteToBase64String(fiatShamirSig), auditCommit.getResponse().getFiatShamir(), auditCommit.getMessage().getBoothID(), identifier);
			resultsLogger.error("Calculated fiat shamir signature ({}) didn't match the one found in the ballot submit response message ({}) for PoD Printer: {} in commitment with identifier: {}",
					Utils.byteToBase64String(fiatShamirSig), auditCommit.getResponse().getFiatShamir(), auditCommit.getMessage().getBoothID(), identifier);
			return false;
		}

		return true;
	}

	/**
	 * Gets the combined signature for the audit commitment
	 * 
	 * @param auditCommit
	 * @return combined signature
	 */
	private String getCombinedSignature(BallotAuditCommit auditCommit) {
		try {
			BLSCombiner bls = new BLSCombiner(5, 4);

			int peerIndex = 0;

			for (WBBSignature sig : auditCommit.getResponse().getWbbSignatures()) {
				if (sig.isUsedAsPartOfThreshold()) {

					peerIndex = this.getDataStore().getCertificatesFile().getSequenceNumberForPeer(sig.getWBBID());

					bls.addShare(Utils.decodeBase64Data(sig.getWBBSig()), peerIndex);
				}
			}

			String combined = Utils.byteToBase64String(bls.combineSignatures().toBytes());

			return combined;
		} catch (BLSSignatureException e) {
			logger.error("Unable to get the combined signature for the current audit commitment: {}", auditCommit);
			return null;
		} catch (CertException e) {
			logger.error("Unable to get the combined signature for the current audit commitment: {}", auditCommit);
			return null;
		}
	}

	/**
	 * Helper method to determine whether a ballot with the specified serial
	 * number was chosen for auditing
	 * 
	 * @param serialNumber
	 * @return true if the ballot with the specified serial number was chosen
	 *         for auditing
	 */
	public boolean isAuditBallot(String serialNumber) {

		logger.info("Checking whether ballot with serial number: {} is a valid ballot which was chosen for auditing", serialNumber);

		BallotAuditCommit auditCommit = null;

		for (CommitIdentifier identifier : this.getDataStore().getAuditData().keySet()) {
			auditCommit = this.getDataStore().getAuditData().get(identifier);

			if (auditCommit.getRandomnessCommitments().keySet().contains(serialNumber)) {
				logger.info("Ballot with serial number: {} is a valid ballot which was chosen for auditing", serialNumber);
				return true;
			}
		}
		logger.info("Ballot with serial number: {} was not chosen for auditing", serialNumber);
		return false;
	}
}
