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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.ComponentDataStoreException;
import com.vvote.verifier.exceptions.ComponentSpecException;
import com.vvote.verifier.exceptions.ComponentVerifierException;
import com.vvote.verifier.exceptions.DataStoreException;
import com.vvote.verifier.exceptions.SpecException;
import com.vvote.verifier.exceptions.VerifierException;
import com.vvote.verifierlibrary.exceptions.CommitException;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Test class for <code>BallotGenerationVerifier</code>
 * 
 * @author James Rumble
 * 
 */
public class BallotGenerationVerifierTest {

	/**
	 * Valid spec file
	 */
	private final static String specFile = "./spec_files/ballotGenSpec.json";

	/**
	 * base path for commits folder
	 */
	private final static String basePath = "/media/james/samsung/documents/s3_vvote_results";
	//private final static String basePath = "./res/sample_commits";

	/**
	 * The <code>BallotGenerationVerifier</code> used throughout the testing
	 */
	private static BallotGenerationVerifier bgv = null;

	/**
	 * Helper method to get a random number in the range provided
	 * 
	 * @param min
	 * @param max
	 * @return a random number between min and max
	 */
	private static int getRandomNumber(int min, int max) {
		return (min + (int) (Math.random() * ((max - min) + 1)));
	}

	/**
	 * @throws ComponentVerifierException
	 * @throws ComponentSpecException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ComponentDataStoreException
	 * @throws VerifierException
	 * @throws SpecException
	 * @throws DataStoreException
	 */
	@Before
	public void setup() throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, FileNotFoundException, IOException, VerifierException, SpecException,
			DataStoreException {
		BallotGenerationVerifierSpec spec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));
		spec.validateSchema();

		BallotGenDataStore dataStore = new BallotGenDataStore(spec, basePath, false);
		dataStore.readData();

		bgv = new BallotGenerationVerifier(dataStore, spec);

		assertNotNull(bgv);

	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		bgv = null;
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in spec form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierConstruction_1() throws Exception {
		BallotGenerationVerifierSpec spec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));
		BallotGenDataStore dataStore = new BallotGenDataStore(spec, basePath, false);

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(dataStore, spec);

		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in JSONObject form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierConstruction_2() throws Exception {
		String spec = IOUtils.readStringFromFile(specFile);

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(new JSONObject(spec), basePath, false);
		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in string form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierConstruction_3() throws Exception {
		String spec = IOUtils.readStringFromFile(specFile);

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(spec, basePath, false);
		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier needs a valid spec and config
	 * file
	 * 
	 * @throws Exception
	 */
	@Test(expected = SpecException.class)
	public void testBallotGenerationVerifierConstruction_5() throws Exception {
		String spec = null;
		String basePath = null;

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(spec, basePath, false);
		assertNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier needs a valid spec file
	 * 
	 * @throws Exception
	 */
	@Test(expected = VerifierException.class)
	public void testBallotGenerationVerifierConstruction_6() throws Exception {

		BallotGenerationVerifier bgv = new BallotGenerationVerifier((BallotGenDataStore) null, (BallotGenerationVerifierSpec) null);
		assertNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier must have a data store passed to
	 * it
	 * 
	 * @throws Exception
	 */
	@Test(expected = VerifierException.class)
	public void testBallotGenerationVerifierConstruction_7() throws Exception {
		BallotGenerationVerifierSpec spec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(null, spec);

		assertNotNull(bgv);
	}
	
	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in spec form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBallotGenerationVerifierConstruction_8() throws Exception {
		BallotGenerationVerifierSpec spec = new BallotGenerationVerifierSpec(IOUtils.readStringFromFile(specFile));
		BallotGenDataStore dataStore = new BallotGenDataStore(spec, basePath, true);

		BallotGenerationVerifier bgv = new BallotGenerationVerifier(dataStore, spec);

		assertNotNull(bgv);
	}

	/**
	 * Verify that the randomness data can be combined successfully
	 * 
	 */
	@Test
	public void testCombineRandomness() {
		assertTrue(bgv.combineRandomnessValues());
	}

	/**
	 * Verify that the randomness data can be combined successfully for a
	 * specific serial number
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCombineRandomnessSerialNumber() throws Exception {

		List<String> serialNumbers = new ArrayList<String>(bgv.getDataStore().getAudittedBallotsSerialNumbers());

		// generate random index into the audited ballots store
		int rnd = BallotGenerationVerifierTest.getRandomNumber(0, serialNumbers.size() - 1);

		assertTrue(rnd < serialNumbers.size());

		String serialNumber = serialNumbers.get(rnd);

		assertTrue(bgv.combineRandomnessValues(serialNumber));
	}
	
	/**
	 * Verify the fiat shamir calculation - this will verify that the correct
	 * portion of ballots have been chosen for auditing
	 */
	@Test
	public void testFiatShamirCalculation() {
		assertTrue(bgv.verifyFiatShamirCalculation());
	}

	/**
	 * Verify the number of ballots to audit matches the number of ballots
	 * provided
	 */
	@Test
	public void testNumberOfBallotsToAudit() {
		assertTrue(bgv.verifyNumberOfBallotsToAudit());
	}

	/**
	 * Verify the number of ballots to audit matches the number of ballots
	 * provided
	 */
	@Test
	public void testVerification() {
		assertTrue(bgv.doVerification());
	}

	/**
	 * Verify that the verifications can be carried out successfully for a
	 * specific serial number
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVerificationsSerialNumber() throws Exception {

		List<String> serialNumbers = new ArrayList<String>(bgv.getDataStore().getAudittedBallotsSerialNumbers());

		// generate random index into the audited ballots store
		int rnd = BallotGenerationVerifierTest.getRandomNumber(0, serialNumbers.size() - 1);

		assertTrue(rnd < serialNumbers.size());

		String serialNumber = serialNumbers.get(rnd);

		assertTrue(bgv.doVerification(serialNumber));
	}

	/**
	 * Verifies that the plaintext ids when re-encrypted with a fixed randomness
	 * value of 1 matches the base encrypted candidate ids
	 * 
	 */
	@Test
	public void testVerifyBaseEncryptedCandidateIds() {
		assertTrue(bgv.verifyBaseCandidateIds());
	}

	/**
	 * Verify that the encryptions can be carried out successfully
	 * 
	 * @throws CommitException
	 * 
	 */
	@Test
	public void testverifyEncryptions() throws CommitException {
		assertTrue(bgv.verifyRandomness());
		assertTrue(bgv.combineRandomnessValues());
		assertTrue(bgv.verifyEncryptions());
	}

	/**
	 * Verify that the encryptions can be carried out successfully for a
	 * specific serial number
	 * 
	 * @throws Exception
	 */
	@Test
	public void testverifyEncryptionsSerialNumber() throws Exception {

		List<String> serialNumbers = new ArrayList<String>(bgv.getDataStore().getAudittedBallotsSerialNumbers());

		// generate random index into the audited ballots store
		int rnd = BallotGenerationVerifierTest.getRandomNumber(0, serialNumbers.size() - 1);

		assertTrue(rnd < serialNumbers.size());

		String serialNumber = serialNumbers.get(rnd);

		assertTrue(bgv.combineRandomnessValues(serialNumber));
		assertTrue(bgv.verifyEncryptions(serialNumber));
	}

	/**
	 * Verify the number of randomness values received by the Mix servers
	 * matches the required number
	 */
	@Test
	public void testVerifyNumberOfRandomnessValuesReceivedByPODPrinters() {
		assertTrue(bgv.verifyNumberOfRandomnessValuesReceivedByPODPrinters());
	}

	/**
	 * Verify the randomness data stored by the PoD Printer is consistent with
	 * the commitments made by the Mix servers
	 * 
	 * @throws CommitException
	 * 
	 */
	@Test
	public void testVerifyRandomness() throws CommitException {
		assertTrue(bgv.verifyRandomness());
	}

	/**
	 * Verify the randomness data stored by the PoD Printer is consistent with
	 * the commitments made by the Mix servers
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVerifyRandomnessSerialNumber() throws Exception {

		List<String> serialNumbers = new ArrayList<String>(bgv.getDataStore().getAudittedBallotsSerialNumbers());

		// generate random index into the audited ballots store
		int rnd = BallotGenerationVerifierTest.getRandomNumber(0, serialNumbers.size() - 1);

		assertTrue(rnd < serialNumbers.size());

		String serialNumber = serialNumbers.get(rnd);

		assertTrue(bgv.verifyRandomness(serialNumber));
	}
}
