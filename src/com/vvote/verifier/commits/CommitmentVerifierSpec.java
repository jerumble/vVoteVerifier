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

import com.vvote.JSONSchema;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.Spec;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifierlibrary.json.JSONSchemaStore;

/**
 * Provides a concrete representation of a spec object
 * 
 * @author James Rumble
 * 
 */
public class CommitmentVerifierSpec extends Spec {

	/**
	 * Constructor for a commitment spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException
	 */
	public CommitmentVerifierSpec(JSONObject spec) throws ComponentSpecException, SpecException {
		super(spec);
	}

	/**
	 * Constructor for a commitment spec
	 * 
	 * @param spec
	 * @throws ComponentSpecException
	 * @throws SpecException
	 */
	public CommitmentVerifierSpec(String spec) throws ComponentSpecException, SpecException {
		super(spec);
	}

	@Override
	public String getSchemaLocation() {
		return JSONSchemaStore.getSchema(JSONSchema.COMMITMENT_SCHEMA);
	}
}
