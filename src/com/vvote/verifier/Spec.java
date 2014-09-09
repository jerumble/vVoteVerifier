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

import com.vvote.exceptions.JSONSchemaException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.fields.VerifierFields;
import com.vvote.verifierlibrary.json.JSONSchemaStore;
import com.vvote.verifierlibrary.json.JSONUtility;

/**
 * Provides an abstract representation of a spec object
 * 
 * @author James Rumble
 * 
 */
public abstract class Spec {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Spec.class);

	/**
	 * The JSON representation of the spec
	 */
	private final JSONObject spec;
	
	/**
	 * A list of the schemas involved in the system
	 */
	private final JSONSchemaStore schemaStore;

	/**
	 * Constructor for spec object
	 * 
	 * @param spec
	 * @throws SpecException
	 */
	public Spec(JSONObject spec) throws SpecException {
		this(spec.toString());
	}

	/**
	 * Constructor for a spec object
	 * 
	 * @param spec
	 * @throws SpecException
	 */
	public Spec(String spec) throws SpecException {

		logger.debug("Reading in Election Commitment specific Information");

		if (spec == null) {
			throw new SpecException("Spec configuration file must be provided");
		}

		if (spec.length() == 0) {
			throw new SpecException("Spec configuration file must be provided");
		}

		try {
			this.spec = new JSONObject(spec);
		} catch (JSONException e) {
			logger.error("Unable to create a Spec. Error: {}", e);
			throw new SpecException("Unable to create a Spec.", e);
		}
		
		try {
			this.schemaStore = JSONSchemaStore.getInstance();
		} catch (JSONSchemaException e) {
			logger.error("Unable to create a Spec.", e);
			throw new SpecException("Unable to create a Spec.", e);
		}
	}

	/**
	 * Validates the spec file
	 * 
	 * @return true if the spec file can be successfully verified
	 */
	public boolean validateSchema() {
		// validate the schema
		try {
			if (JSONUtility.validateSchema(this.getSchemaLocation(), this.spec.toString())) {
				return true;
			}
		} catch (JSONSchemaException e) {
			logger.error("Cannot verify the spec file with the schema provided");
			return false;
		}
		logger.error("Input JSON does not comply with the provided Schema. The specification file is invalid.");
		return false;
	}

	/**
	 * Getter for the location of the final commitment folder
	 * 
	 * @return spec.getString(FINAL_COMMITS_FOLDER)
	 */
	public String getFinalCommitsFolder() {
		if (this.spec != null) {
			if (this.spec.has(VerifierFields.Spec.FINAL_COMMITS_FOLDER)) {
				try {
					return this.spec.getString(VerifierFields.Spec.FINAL_COMMITS_FOLDER);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the final commits folder from the spec object");
				}
			}
		}
		return null;
	}
	
	/**
	 * Getter for the location of the extra commitment folder
	 * 
	 * @return spec.getString(EXTRA_COMMITS_FOLDER)
	 */
	public String getExtraCommitsFolder() {
		if (this.spec != null) {
			if (this.spec.has(VerifierFields.Spec.EXTRA_COMMITS_FOLDER)) {
				try {
					return this.spec.getString(VerifierFields.Spec.EXTRA_COMMITS_FOLDER);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the extra commits folder from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Getter for the certs file
	 * 
	 * @return spec.getString(CERTS)
	 */
	public String getCertsFile() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.ComponentSpec.CERTS)) {
				try {
					return this.getSpec().getString(VerifierFields.ComponentSpec.CERTS);
				} catch (JSONException e) {
					logger.error("There was a problem when getting the CERTS file from the spec object");
				}
			}
		}
		return null;
	}

	/**
	 * Simply provides the location of the schema to use to validate the spec
	 * file
	 * 
	 * @return schema location
	 */
	public abstract String getSchemaLocation();

	/**
	 * Getter for the spec in json format
	 * 
	 * @return spec
	 */
	public JSONObject getSpec() {
		return this.spec;
	}
	
	/**
	 * Getter for the schema store
	 * 
	 * @return schemaStore
	 */
	public JSONSchemaStore getSchemaStore() {
		return this.schemaStore;
	}
}
