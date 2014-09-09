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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.VVoteVerifierException;
import com.vvote.verifier.fields.VerifierFields;

/**
 * Provides a specification for the main <code>VVoteVerifier</code>
 * 
 * @author James Rumble
 * 
 */
public class VVoteVerifierSpec {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VVoteVerifierSpec.class);

	/**
	 * The JSON representation of the spec
	 */
	private final JSONObject spec;

	/**
	 * Details for each verifier
	 */
	private Set<VerifierDetails> verifierDetails = null;

	/**
	 * Constructor for a vvote verifier spec object
	 * 
	 * @param json
	 * @throws VVoteVerifierException
	 */
	public VVoteVerifierSpec(JSONObject json) throws VVoteVerifierException {
		this(json.toString());
	}

	/**
	 * Constructor for a vvote verifier spec object
	 * 
	 * @param spec
	 * @throws VVoteVerifierException
	 */
	public VVoteVerifierSpec(String spec) throws VVoteVerifierException {
		if (spec == null) {
			logger.error("Spec configuration file must be provided");
			throw new VVoteVerifierException("Spec configuration file must be provided");
		}

		this.verifierDetails = new HashSet<VerifierDetails>();

		try {
			this.spec = new JSONObject(spec);

			JSONArray verifierList = this.spec.getJSONArray(VerifierFields.VVoteVerifierSpec.VERIFIER_DETAILS);

			for (int i = 0; i < verifierList.length(); i++) {
				JSONObject verifierDetails = verifierList.getJSONObject(i);
				this.verifierDetails.add(new VerifierDetails(verifierDetails.getString(VerifierFields.VVoteVerifierSpec.VERIFIER_CLASS), verifierDetails
						.getString(VerifierFields.VVoteVerifierSpec.VERIFIER_NAME), verifierDetails.getString(VerifierFields.VVoteVerifierSpec.VERIFIER_SPEC_FILE)));
			}
		} catch (JSONException e) {
			logger.error("Unable to create VVoteVerifierSpec object", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifierSpec object", e);
		}
	}

	/**
	 * Getter for the verifier details list
	 * 
	 * @return verifierDetails
	 */
	public Set<VerifierDetails> getVerifierDetails() {
		return this.verifierDetails;
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
}
