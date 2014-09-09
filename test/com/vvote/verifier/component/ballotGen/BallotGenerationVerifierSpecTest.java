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

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * The class <code>BallotGenerationVerifierSpecTest</code> contains tests for
 * the class <code>{@link BallotGenerationVerifierSpec}</code>.
 */
public class BallotGenerationVerifierSpecTest {

	/**
	 * Valid spec file
	 */
	private final static String specFile = "./testdata/ballotGeneration/spec_files/ballotGenSpec.json";

	/**
	 * The <code>BallotGenerationConfig</code> used throughout the testing
	 */
	private static BallotGenerationVerifierSpec spec = null;

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 */
	@Before
	public void setUp() throws Exception {
		spec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 */
	@After
	public void tearDown() throws Exception {
		spec = null;
	}

	/**
	 * Run the BallotGenerationVerifierSpec(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierSpec_1() throws Exception {

		BallotGenerationVerifierSpec result = new BallotGenerationVerifierSpec((String) null);

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationVerifierSpec(String) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierSpec_2() throws Exception {
		String spec = "";

		BallotGenerationVerifierSpec result = new BallotGenerationVerifierSpec(spec);

		assertNotNull(result);
	}

	/**
	 * Verify that a ballot generation spec can be created successfully using
	 * json object
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierSpecConstruction_3() throws Exception {
		JSONObject jsonspec = new JSONObject(IOUtils.readStringFromFile(specFile));
		BallotGenerationVerifierSpec spec = new BallotGenerationVerifierSpec(jsonspec);

		assertNotNull(spec);
	}

	/**
	 * Test that a spec object can be constructed correctly from a valid spec
	 * file
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierSpecConstruction_4() throws Exception {
		BallotGenerationVerifierSpec verifierSpec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));
		assertNotNull(verifierSpec);
	}

	/**
	 * Run the BallotGenerationVerifierSpec(String) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierSpec_5() throws Exception {
		BallotGenerationVerifierSpec result = new BallotGenerationVerifierSpec(new JSONObject());

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationVerifierSpec(String) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierSpec_6() throws Exception {

		// invalid JSONObject - missing final }
		String specFile = "./testdata/ballotGeneration/spec_files/ballotGenSpec1.json";

		BallotGenerationVerifierSpec result = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationVerifierSpec(String) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierSpec_7() throws Exception {

		// invalid JSONObject - missing audit file name
		String specFile = "./testdata/ballotGeneration/spec_files/ballotGenSpec2.json";

		BallotGenerationVerifierSpec result = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));

		assertNotNull(result);
	}

	/**
	 * Run the String getAuditDataFile() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetAuditDataFile_1() throws Exception {
		String result = spec.getAuditDataFile();
		assertNotNull(result);
	}

	/**
	 * Run the String getBallotGenConfig() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetBallotGenConfig_1() throws Exception {
		String result = spec.getBallotGenConfig();
		assertNotNull(result);
	}

	/**
	 * Run the String getBallotSubmitResponse() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetBallotSubmitResponse_1() throws Exception {
		String result = spec.getBallotSubmitResponse();
		assertNotNull(result);
	}

	/**
	 * Run the String getCiphersDataFile() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCiphersDataFile_1() throws Exception {
		String result = spec.getCiphersDataFile();
		assertNotNull(result);
	}

	/**
	 * Run the String getCommitDataFile() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCommitDataFile_1() throws Exception {
		String result = spec.getCommitDataFile();
		assertNotNull(result);
	}

	/**
	 * Run the String getSchemaLocation() method test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetSchemaLocation_1() throws Exception {
		String result = spec.getSchemaLocation();
		assertNotNull(result);
	}
}