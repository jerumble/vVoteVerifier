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
package com.vvote.verifier.commits;

import it.unisa.dia.gas.jpbc.Element;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.PublicWBBConstants;
import com.vvote.commits.FinalCommitment;
import com.vvote.messages.typed.TypedJSONMessage;
import com.vvote.messages.typed.file.FileMessage;
import com.vvote.messages.types.MessageType;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifier.Verifier;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;
import com.vvote.verifierlibrary.exceptions.FileHashException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.crypto.CryptoUtils;
import com.vvote.verifierlibrary.utils.crypto.bls.BLSUtils;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides verification of the signatures over commitments made to the public
 * WBB
 * 
 * @author James Rumble
 * 
 */
public class CommitmentVerifier extends Verifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitmentVerifier.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Constructor for a <code>CommitmentVerifier</code>
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws VerifierException
	 */
	public CommitmentVerifier(CommitmentDataStore dataStore, CommitmentVerifierSpec spec) throws VerifierException {
		super(dataStore, spec);

		logger.info("Setting up the Commitment Verifier");
	}

	/**
	 * Constructor for a CommitmentVerifier from string representations of the
	 * spec objects
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws SpecException
	 * @throws ComponentSpecException
	 * @throws DataStoreException
	 * @throws VerifierException
	 */
	public CommitmentVerifier(String spec, String basePath, boolean useExtraCommits) throws VerifierException, DataStoreException, ComponentSpecException, SpecException {
		this(new CommitmentDataStore(new CommitmentVerifierSpec(spec), basePath, useExtraCommits), new CommitmentVerifierSpec(spec));
	}

	@Override
	public boolean doVerification() {

		boolean verified = true;

		if (!this.getDataStore().isUseExtraCommits()) {
			logger.info("Starting Commitment verification");

			FinalCommitment commitment = null;

			String outerExtracted = null;

			String currentCommitTime = null;

			MessageDigest hashDigest = null;
			MessageDigest jointSigDigest = null;
			byte[] calculatedJointSig = null;
			byte[] hash = null;
			byte[] jointSig = null;

			try {

				for (String identifier : this.getDataStore().getFinalCommitments().keySet()) {

					logger.info("Starting the verification of commitment with identifier: {}", identifier);

					hashDigest = MessageDigest.getInstance(PublicWBBConstants.PUBLIC_WBB_DIGEST);

					commitment = this.getDataStore().getFinalCommitments().get(identifier);

					currentCommitTime = commitment.getSignature().getSignatureMessage().getCommitTime();

					outerExtracted = IOUtils.extractZipFile(commitment.getAttachment().getFilePath());

					for (TypedJSONMessage message : commitment.getFileMessage().getJsonMessages()) {
						
						hashDigest.update(message.getInternalSignableContent().getBytes());

						if (message.getType() == MessageType.BALLOT_AUDIT_COMMIT || message.getType() == MessageType.MIX_RANDOM_COMMIT || message.getType() == MessageType.BALLOT_GEN_COMMIT
								|| message.getType() == MessageType.FILE_COMMIT) {
							File file = new File(outerExtracted, ((FileMessage) message).getFileName());
							logger.info("Adding hash of file to intermediate hash: {}", file.getName());
							CryptoUtils.hashFile(file, hashDigest);
						}
					}

					// Get the hash value
					hash = hashDigest.digest();
					
					String hashString = Utils.byteToBase64String(hash);

					jointSigDigest = MessageDigest.getInstance(PublicWBBConstants.PUBLIC_WBB_DIGEST);

					logger.info("Adding Commit String to signature: {}", PublicWBBConstants.FINAL_COMMIT_MESSAGE_TYPE);
					jointSigDigest.update(PublicWBBConstants.FINAL_COMMIT_MESSAGE_TYPE.getBytes());
					logger.info("Adding Commit Time to signature: {}", currentCommitTime);
					jointSigDigest.update(currentCommitTime.getBytes());
					logger.info("Adding hash to signature: {}", hashString);
					jointSigDigest.update(hash);

					String description = commitment.getSignature().getDescription();

					if (description != null) {
						logger.info("Adding description to signature: {}", description);
						jointSigDigest.update(description.getBytes());
					}

					calculatedJointSig = jointSigDigest.digest();

					jointSig = Utils.decodeBase64Data(commitment.getSignature().getSignatureMessage().getJointSig());

					Element wbbSignature = BLSUtils.getSignatureElement(jointSig);

					logger.info("Checking the joint signature for the commitment with identifier: {} using the WBB public key and the privately signed joint signature", identifier);

					if (!BLSUtils.verifyBLSSignature(calculatedJointSig, wbbSignature, this.getDataStore().getCertificatesFile().getWbbCert())) {
						resultsLogger.error("Verification of the joint signature for the commitment with identifier: {} failed. Check that the data was successfully downloaded.", identifier);
						resultsLogger.error("Expected signature: {}, but calculated signature: {}", commitment.getSignature().getSignatureMessage().getJointSig(),
								Utils.byteToBase64String(calculatedJointSig));
						if (description != null) {
							resultsLogger.error("Elements used in the signature: {}, {}, {}, {}", PublicWBBConstants.FINAL_COMMIT_MESSAGE_TYPE, currentCommitTime, Utils.byteToBase64String(hash),
									description);
						} else {
							resultsLogger.error("Elements used in the signature: {}, {}, {}", PublicWBBConstants.FINAL_COMMIT_MESSAGE_TYPE, currentCommitTime, Utils.byteToBase64String(hash));
						}
						verified = false;
					} else {
						resultsLogger.info("Successfully verified the joint signature for the commitment with identifier: {}", identifier);
					}
				}
			} catch (NoSuchAlgorithmException e) {
				logger.error("Unable to continue verification.", e);
				resultsLogger.error("Unable to continue verification.", e);
				return false;
			} catch (JSONException e) {
				logger.error("Unable to continue verification.", e);
				resultsLogger.error("Unable to continue verification.", e);
				return false;
			} catch (FileHashException e) {
				logger.error("Unable to continue verification.", e);
				resultsLogger.error("Unable to continue verification.", e);
				return false;
			} catch (IOException e) {
				logger.error("Unable to continue verification.", e);
				resultsLogger.error("Unable to continue verification.", e);
				return false;
			} catch (BLSSignatureException e) {
				logger.error("Unable to continue verification.", e);
				resultsLogger.error("Unable to continue verification.", e);
				return false;
			}

			if (verified) {
				resultsLogger.info("Successfully verified the joint signatures for all the commitments provided");
			} else {
				resultsLogger.info("Could not successfully verify the joint signatures for the commitments provided");
			}
		} else {
			resultsLogger.info("Extra commits folder is being used so we cannot verify signatures");
		}

		return verified;
	}

}
