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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.ComponentVerifierException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * The class <code>CommitmentVerifierTest</code> contains tests for the class <code>{@link CommitmentVerifier}</code>.
 */
public class CommitmentVerifierTest {
	/**
	 * Valid spec file
	 */
	private final static String specFile = "./spec_files/commitmentSpec.json";

	/**
	 * base path for commits folder
	 */
	private final static String basePath = "./res/sample_commits";

	/**
	 * The <code>CommitmentVerifier</code> used throughout the testing
	 */
	private static CommitmentVerifier verifier = null;

	/**
	 * @throws ComponentVerifierException
	 * @throws ComponentSpecException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ComponentDataStoreException 
	 * @throws VerifierException 
	 * @throws DataStoreException 
	 * @throws SpecException
	 */
	@Before
	public void setup() throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, FileNotFoundException, IOException, VerifierException, DataStoreException, SpecException {
		CommitmentVerifierSpec spec = new CommitmentVerifierSpec(IOUtils.readStringFromFile(specFile));
		spec.validateSchema();
		
		CommitmentDataStore dataStore = new CommitmentDataStore(spec, basePath, false);
		dataStore.readData();
		
		verifier = new CommitmentVerifier(dataStore, spec);

		assertNotNull(verifier);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		verifier = null;
	}

	/**
	 * Verify the number of ballots to audit matches the number of ballots
	 * provided
	 */
	@Test
	public void testVerification() {
		assertTrue(verifier.doVerification());
	}
}