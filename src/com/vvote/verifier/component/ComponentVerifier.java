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

import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.ec.ElGamalECPoint;
import com.vvote.verifier.Verifier;
import com.vvote.verifier.exceptions.ComponentVerifierException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.utils.crypto.CryptoUtils;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;

/**
 * Provides an abstract representation of a component verifier which handles the
 * verifier of a specific component of the vVote system
 * 
 * @author James Rumble
 * 
 */
public abstract class ComponentVerifier extends Verifier {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ComponentVerifier.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Constructor for a component verifier object
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws ComponentVerifierException
	 * @throws VerifierException
	 */
	public ComponentVerifier(ComponentDataStore dataStore, ComponentSpec spec) throws ComponentVerifierException, VerifierException {
		super(dataStore, spec);

		logger.debug("Creating component verifier");

		CryptoUtils.initProvider();
	}

	/**
	 * All Verifiers will provide a doVerification implementation which will
	 * carry out all necessary verification steps.
	 * 
	 * @return true if the verification of its component has been carried out
	 *         successfully
	 */
	@Override
	public boolean doVerification() {

		logger.info("Starting Verification relevant for each verifier");

		boolean verified = super.doVerification();

		if (!this.verifyBaseCandidateIds()) {
			verified = false;
		}

		return verified;
	}

	@Override
	public ComponentDataStore getDataStore() {
		if (super.getDataStore() instanceof ComponentDataStore) {
			return (ComponentDataStore) super.getDataStore();
		}
		return null;
	}

	@Override
	public ComponentSpec getSpec() {
		if (super.getSpec() instanceof ComponentSpec) {
			return (ComponentSpec) super.getSpec();
		}
		return null;
	}

	/**
	 * Verify that the base encrypted ids are actually the plaintext ids
	 * encrypted with a fixed randomness value of 1
	 * 
	 * @return true if the plaintext ids and base encryped candidate ids match
	 */
	public boolean verifyBaseCandidateIds() {

		logger.info("Starting Verification of the base candidate ids");

		if (this.getDataStore().getPlaintextIds() == null || this.getDataStore().getBaseEncryptedIds() == null) {
			logger.error("Check that both the plaintext ids and base encrypted candidate ids are present");
			resultsLogger.error("Check that both the plaintext ids and base encrypted candidate ids are present");
			return false;
		}

		// check that the number of ids (plaintext/base encrypted) match
		if (this.getDataStore().getPlaintextIds().size() != this.getDataStore().getBaseEncryptedIds().size()) {
			logger.error("The size of the lists of plaintext ids and base encrypted candidate ids do not match");
			resultsLogger.error("The size of the lists of plaintext ids and base encrypted candidate ids do not match");
			return false;
		}

		final ECPoint publicKey = this.getDataStore().getPublicKey();

		ECPoint currentPlaintextId = null;
		ElGamalECPoint encryptedId = null;
		ElGamalECPoint currentBaseEncryptedId = null;

		// Encrypt each plaintext id with fixed randomness value of 1 and get a
		// candidate encrypted id to compare to the stored base encrypted
		// ciphers
		for (int i = 0; i < this.getDataStore().getPlaintextIds().size(); i++) {

			currentPlaintextId = this.getDataStore().getPlaintextIds().get(i);
			currentBaseEncryptedId = this.getDataStore().getBaseEncryptedIds().get(i);
			encryptedId = ECUtils.encrypt(currentPlaintextId, publicKey, BigInteger.ONE);

			// compare the two ElGamalECPoints
			if (!encryptedId.equals(currentBaseEncryptedId)) {
				logger.error("Plaintext ids and base candidate ids do not match at index: {}, (plaintext id: {}, base encrypted id: {})", i, currentPlaintextId.toString(), currentBaseEncryptedId.toString());
				resultsLogger.error("Plaintext ids and base candidate ids do not match at index: {}, (plaintext id: {}, base encrypted id: {})", i, currentPlaintextId.toString(), currentBaseEncryptedId.toString());
				return false;
			}
		}
		
		logger.debug("Successfully verified that the plaintext ids and base candidate ids match");
		resultsLogger.info("Successfully verified that the plaintext ids and base candidate ids match");

		return true;
	}
}
