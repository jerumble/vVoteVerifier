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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.ConfigException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * The class <code>BallotGenerationConfigTest</code> contains tests for
 * the class <code>{@link BallotGenerationConfig}</code>.
 */
public class BallotGenerationConfigTest {

	/**
	 * Valid config file
	 */
	private static final String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf.json";

	/**
	 * The <code>BallotGenerationConfig</code> used throughout the testing
	 */
	private static BallotGenerationConfig config = null;

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 */
	@Before
	public void setUp() throws Exception {
		config = new BallotGenerationConfig(IOUtils.readStringFromFile(configFile));
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 */
	@After
	public void tearDown() throws Exception {
		config = null;
	}

	/**
	 * Run the BallotGenerationConfig(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationConfig_1() throws Exception {
		BallotGenerationConfig result = new BallotGenerationConfig(IOUtils.readStringFromFile(configFile));

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationConfig(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationConfig_2() throws Exception {
		BallotGenerationConfig result = new BallotGenerationConfig(IOUtils.readJSONObjectFromFile(configFile));

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationConfig(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_3() throws Exception {
		JSONObject config = new JSONObject();

		BallotGenerationConfig result = new BallotGenerationConfig(config);

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationConfig(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_4() throws Exception {
		BallotGenerationConfig result = new BallotGenerationConfig((JSONObject) null);

		assertNotNull(result);
	}

	/**
	 * Run the BallotGenerationConfig(JSONObject) constructor test.
	 * 
	 * @throws Exception
	 */
	@Test(expected = JSONException.class)
	public void testBallotGenerationConfig_5() throws Exception {
		BallotGenerationConfig result = new BallotGenerationConfig("");

		assertNotNull(result);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig invalid
	 * JSONObject - missing final }
	 * 
	 * @throws Exception
	 */
	@Test(expected = JSONException.class)
	public void testBallotGenerationConfig_6() throws Exception {

		// invalid JSONObject - missing final }
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf1.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig missing
	 * ballotToAudit identifier
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_7() throws Exception {

		// missing ballotToAudit identifier
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf2.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig invalid
	 * ballotToAudit -1
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_8() throws Exception {

		// invalid ballotToAudit -1
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf3.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig invalid
	 * ballotToGenerate -1
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_9() throws Exception {

		// invalid ballotToGenerate -1
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf4.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig missing
	 * lc_btl candidates
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_10() throws Exception {

		// missing lc_btl candidates
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf5.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig missing
	 * races
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_11() throws Exception {

		// missing races
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf6.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test for an invalid construction of a BallotGenerationConfig number of
	 * ballots to audit must be less than those to generate
	 * 
	 * @throws Exception
	 */
	@Test(expected = ConfigException.class)
	public void testBallotGenerationConfig_12() throws Exception {

		// number of ballots to audit must be less than those to generate
		String configFile = "./testdata/ballotGeneration/configs/ballot_gen_conf7.json";

		String config = IOUtils.readStringFromFile(configFile);

		BallotGenerationConfig bgc = new BallotGenerationConfig(config);
		assertNull(bgc);
	}

	/**
	 * Test getter for number of ballots to audit
	 */
	@Test
	public void testGetBallotsToAudit() {
		assertNotNull(config.getBallotsToAudit());
		assertTrue(config.getBallotsToAudit() > 0);
	}

	/**
	 * Test to ensure the number of ballots to audit is valid
	 */
	@Test
	public void testGetBallotsToAudit_2() {
		assertNotNull(config.getBallotsToAudit());
		assertNotNull(config.getBallotsToGenerate());
		assertTrue(config.getBallotsToAudit() > 0);
		assertTrue(config.getBallotsToGenerate() > 0);

		assertTrue(config.getBallotsToAudit() < config.getBallotsToGenerate());
	}

	/**
	 * Test getter for the number of ballots to generate
	 */
	@Test
	public void testGetBallotsToGenerate() {
		assertNotNull(config.getBallotsToGenerate());
		assertTrue(config.getBallotsToGenerate() > 0);
	}

	/**
	 * Tests getter for la size
	 */
	@Test
	public void testGetLASize() {
		assertNotNull(config.getLASize());
		assertTrue(config.getLASize() > 0);
	}

	/**
	 * Test getter for lc atl size
	 */
	@Test
	public void testGetLcATLSize() {
		assertNotNull(config.getLcATLSize());
		assertTrue(config.getLcATLSize() > 0);
	}

	/**
	 * Test getter for lc btl size
	 */
	@Test
	public void testGetLcBTLSize() {
		assertNotNull(config.getLcBTLSize());
		assertTrue(config.getLcBTLSize() > 0);
	}
	
	/**
	 * Test getter for the total number of candidates
	 */
	@Test
	public void testGetNumberOfCandidates() {
		assertNotNull(config.getNumberOfCandidates());
		assertTrue(config.getNumberOfCandidates() > 0);
		
		int size = config.getLASize() + config.getLcATLSize() + config.getLcBTLSize();
		
		assertTrue(size == config.getNumberOfCandidates());
	}
}
