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
 * Provides a concrete implementation of the spec file for the vote packing
 * verifier
 * 
 * @author James Rumble
 * 
 */
public class VotePackingVerifierSpec extends ComponentSpec {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VotePackingVerifierSpec.class);

	/**
	 * Constructor for a vote packing spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException 
	 */
	public VotePackingVerifierSpec(JSONObject spec) throws ComponentSpecException, SpecException {
		super(spec);
		logger.debug("Reading in Vote Packing specific Election Information");
	}

	/**
	 * Constructor for a vote packing spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException 
	 */
	public VotePackingVerifierSpec(String spec) throws ComponentSpecException, SpecException {
		super(spec);
		logger.debug("Reading in Vote Packing specific Election Information");
	}

	@Override
	public String getSchemaLocation() {
		return JSONSchemaStore.getSchema(JSONSchema.VOTE_PACKING_SCHEMA);
	}

	/**
	 * Getter for the location of the vote packing config
	 * 
	 * @return spec.getString(VOTE_PACKING_CONFIG)
	 */
	public final String getVotePackingConfig() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.VotePackingVerifierSpec.VOTE_PACKING_CONFIG)) {
				try {
					return this.getSpec().getString(VerifierFields.VotePackingVerifierSpec.VOTE_PACKING_CONFIG);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the VOTE_PACKING_CONFIG file from the spec object");
				}
			}
		}
		return null;
	}
	
	/**
	 * Getter for the location of the vote packing mix output folder
	 * 
	 * @return spec.getString(MIX_OUTPUT)
	 */
	public final String getMixOutputFolder() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.VotePackingVerifierSpec.MIX_OUTPUT)) {
				try {
					return this.getSpec().getString(VerifierFields.VotePackingVerifierSpec.MIX_OUTPUT);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the MIX_OUTPUT file from the spec object");
				}
			}
		}
		return null;
	}
	
	/**
	 * Getter for the location of the vote packing mix input folder
	 * 
	 * @return spec.getString(MIX_INPUT)
	 */
	public final String getMixInputFolder() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.VotePackingVerifierSpec.MIX_INPUT)) {
				try {
					return this.getSpec().getString(VerifierFields.VotePackingVerifierSpec.MIX_INPUT);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the MIX_INPUT file from the spec object");
				}
			}
		}
		return null;
	}
	
	/**
	 * Getter for the location of the race map
	 * 
	 * @return spec.getString(RACE_MAP)
	 */
	public final String getRaceMap() {
		if (this.getSpec() != null) {
			if (this.getSpec().has(VerifierFields.VotePackingVerifierSpec.RACE_MAP)) {
				try {
					return this.getSpec().getString(VerifierFields.VotePackingVerifierSpec.RACE_MAP);
				} catch (JSONException e) {

					logger.error("There was a problem when getting the RACE_MAP file from the spec object");
				}
			}
		}
		return null;
	}
}
