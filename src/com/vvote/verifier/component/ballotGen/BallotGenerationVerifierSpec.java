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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.JSONSchema;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.component.ComponentSpec;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.fields.VerifierFields;
import com.vvote.verifierlibrary.json.JSONSchemaStore;

/**
 * Provides a concrete implementation of the spec file for the ballot generation
 * verifier
 * 
 * @author James Rumble
 * 
 */
public final class BallotGenerationVerifierSpec extends ComponentSpec {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenerationVerifierSpec.class);

	/**
	 * Constructor for a ballot generation spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException 
	 */
	public BallotGenerationVerifierSpec(JSONObject spec) throws ComponentSpecException, SpecException {
		super(spec);
		logger.debug("Reading in Ballot Generation specific Election Information");
	}

	/**
	 * Constructor for a ballot generation spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException 
	 */
	public BallotGenerationVerifierSpec(String spec) throws ComponentSpecException, SpecException {
		super(spec);
		logger.debug("Reading in Ballot Generation specific Election Information");
	}

	/**
	 * Getter for the name of the audit data file
	 * 
	 * @return spec.getString(AUDIT_DATA_FILE)
	 */
	public String getAuditDataFile() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.BallotGenerationVerifierSpec.AUDIT_DATA_FILE)) {
				try {
					return this.getSpec().getString(VerifierFields.BallotGenerationVerifierSpec.AUDIT_DATA_FILE);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the AUDIT_DATA_FILE file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the name of the ballot submit response message
	 * 
	 * @return spec.getString(BALLOT_SUBMIT_RESPONSE)
	 */
	public String getBallotSubmitResponse() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.BallotGenerationVerifierSpec.BALLOT_SUBMIT_RESPONSE)) {
				try {
					return this.getSpec().getString(VerifierFields.BallotGenerationVerifierSpec.BALLOT_SUBMIT_RESPONSE);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the BALLOT_SUBMIT_RESPONSE file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the name of the ciphers data file
	 * 
	 * @return spec.getString(COMMIT_DATA_FILE)
	 */
	public String getCommitDataFile() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.BallotGenerationVerifierSpec.COMMIT_DATA_FILE)) {
				try {
					return this.getSpec().getString(VerifierFields.BallotGenerationVerifierSpec.COMMIT_DATA_FILE);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the COMMIT_DATA_FILE file from the spec object");
				}
			}
		}
		return null;
	}

	@Override
	public final String getSchemaLocation() {
		return JSONSchemaStore.getSchema(JSONSchema.BALLOT_GEN_SCHEMA);
	}
}
