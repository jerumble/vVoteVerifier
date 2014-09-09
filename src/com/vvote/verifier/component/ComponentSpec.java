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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.Spec;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.fields.VerifierFields;

/**
 * Provides an abstract representation of a spec object
 * 
 * @author James Rumble
 * 
 */
public abstract class ComponentSpec extends Spec {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ComponentSpec.class);

	/**
	 * Constructor for component spec object
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException
	 */
	public ComponentSpec(JSONObject spec) throws ComponentSpecException, SpecException {
		this(spec.toString());
	}

	/**
	 * Constructor for a component spec object
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException
	 */
	public ComponentSpec(String spec) throws ComponentSpecException, SpecException {
		super(spec);
		logger.debug("Reading in Election Information relevant for each verifier");
	}

	/**
	 * Getter for the location of the base encrypted candidate ids file
	 * 
	 * @return spec.getString(BASE_ENCRYPTED_CANDIDATE_IDS)
	 */
	public String getBaseEncryptedCandidateIds() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.BASE_ENCRYPTED_CANDIDATE_IDS)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.BASE_ENCRYPTED_CANDIDATE_IDS);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the BASE_ENCRYPTED_CANDIDATE_IDS file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the location of the district config
	 * 
	 * @return spec.getString(DISTRICT_CONFIG)
	 */
	public String getDistrictConfig() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.DISTRICT_CONFIG)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.DISTRICT_CONFIG);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the DISTRICT_CONFIG file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the location of the plaintext candidate ids file
	 * 
	 * @return spec.getString(PLAINTEXT_CANDIDATE_IDS)
	 */
	public String getPlaintextCandidateIds() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.PLAINTEXT_CANDIDATE_IDS)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.PLAINTEXT_CANDIDATE_IDS);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the PLAINTEXT_CANDIDATE_IDS file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the location of the public key
	 * 
	 * @return spec.getString(PUBLIC_KEY)
	 */
	public String getPublicKeyLocation() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.PUBLIC_KEY)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.PUBLIC_KEY);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the PUBLIC_KEY file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the name of the ciphers data file
	 * 
	 * @return spec.getString(CIPHERS_DATA_FILE)
	 */
	public String getCiphersDataFile() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.CIPHERS_DATA_FILE)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.CIPHERS_DATA_FILE);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the CIPHERS_DATA_FILE file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the location of the ballot gen config
	 * 
	 * @return spec.getString(BALLOT_GEN_CONFIG)
	 */
	public final String getBallotGenConfig() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.BALLOT_GEN_CONFIG)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.BALLOT_GEN_CONFIG);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the BALLOT_GEN_CONFIG file from the spec object");
				}
			}
		}
		return null;
	}
}
