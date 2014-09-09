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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.utils.crypto.CryptoUtils;

/**
 * Provides an abstract representation of a verifier
 * 
 * @author James Rumble
 * 
 */
public abstract class Verifier implements IVerifier {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Verifier.class);

	/**
	 * Each verifier will have its own component specification object
	 */
	private final Spec spec;

	/**
	 * Each verifier will have its own data store object
	 */
	private final DataStore dataStore;

	/**
	 * Constructor for a verifier object
	 * 
	 * @param dataStore
	 * @param spec
	 * @throws VerifierException
	 */
	public Verifier(DataStore dataStore, Spec spec) throws VerifierException {

		logger.debug("Creating component verifier");

		CryptoUtils.initProvider();

		if (dataStore == null) {
			logger.error("A DataStore object must be provided to a verifier");
			throw new VerifierException("A DataStore object must be provided to a verifier");
		}

		if (spec == null) {
			logger.error("A Spec object must be provided to a verifier");
			throw new VerifierException("A Spec object must be provided to a verifier");
		}

		this.dataStore = dataStore;
		this.spec = spec;
		
		if(!this.dataStore.hasReadData()){
			this.dataStore.readData();
		}
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
		if (!this.dataStore.hasReadData()) {
			if (!this.dataStore.readData()) {
				logger.error("Could not read in the data successfully");
				return false;
			}
		}

		if (!this.spec.validateSchema()) {
			logger.error("Could not read validate the provided spec using the schema file");
			return false;
		}

		return true;
	}

	/**
	 * Getter for the spec object
	 * 
	 * @return spec
	 */
	public Spec getSpec() {
		return this.spec;
	}

	/**
	 * Getter for the data store object
	 * 
	 * @return dataStore
	 */
	public DataStore getDataStore() {
		return this.dataStore;
	}
}
