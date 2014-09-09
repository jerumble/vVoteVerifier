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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.verifier.exceptions.VVoteVerifierException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * The class <code>VVoteVerifierTest</code> contains tests for the class
 * <code>{@link VVoteVerifier}</code>.
 */
public class VVoteVerifierTest {

	/**
	 * base path for commits folder
	 */
	private final static String basePath = "./res/sample_commits";

	/**
	 * The <code>BallotGenerationVerifier</code> used throughout the testing
	 */
	private static VVoteVerifier verifier = null;

	/**
	 * @throws VVoteVerifierException
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Before
	public void setup() throws VVoteVerifierException, FileNotFoundException, IOException {
		
		String spec = IOUtils.readStringFromFile("./spec_files/verifierSpecFile.json");
		
		VVoteVerifierSpec verifierSpec = new VVoteVerifierSpec(spec);
		
		verifier = new VVoteVerifier(verifierSpec, basePath, false);

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
	 * Run the VVoteVerifier(String) constructor test.
	 *
	 * @throws Exception
	 */
	@Test
	public void testVVoteVerifier_1() throws Exception {
		String spec = IOUtils.readStringFromFile("./spec_files/verifierSpecFile.json");
		
		VVoteVerifierSpec verifierSpec = new VVoteVerifierSpec(spec);
		
		VVoteVerifier result = new VVoteVerifier(verifierSpec, basePath, false);

		assertNotNull(result);
	}

	/**
	 * Run the do verification method for a vvote Verifier
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDoVerification() throws Exception {
		assertTrue(verifier.doVerification());
	}
}