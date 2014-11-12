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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.verifier.DataStore;
import com.vvote.verifier.exceptions.DataStoreException;

/**
 * Provides a concrete data store for a commitment verifier
 * 
 * @author James Rumble
 * 
 */
public class CommitmentDataStore extends DataStore {
	
	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitmentDataStore.class);

	/**
	 * Constructor for a commitment data store
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * @throws DataStoreException
	 */
	public CommitmentDataStore(CommitmentVerifierSpec spec, String basePath, boolean useExtraCommits) throws DataStoreException {
		super(spec, basePath, useExtraCommits);
	}
	
	@Override
	public boolean readData() {

		logger.info("Reading in Election data to verify the Public WBB Commit process");

		boolean result = true;

		if (!this.hasReadData()) {
			result = super.readData();
		}

		return result;
	}

	@Override
	public CommitmentVerifierSpec getSpec() {
		if (super.getSpec() instanceof CommitmentVerifierSpec) {
			return (CommitmentVerifierSpec) super.getSpec();
		}
		return null;
	}
}
