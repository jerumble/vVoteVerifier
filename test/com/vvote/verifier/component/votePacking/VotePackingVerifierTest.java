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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * The class <code>VotePackingVerifierTest</code> contains tests for the class
 * <code>{@link VotePackingVerifier}</code>.
 * 
 * @author James Rumble
 */
public class VotePackingVerifierTest {

	/**
	 * Valid spec file
	 */
	private final static String specFile = "./spec_files/votePackingSpec.json";

	/**
	 * base path for commits folder
	 */
	private final static String basePath = "./res/vvote_results";

	/**
	 * The <code>VotePackingVerifier</code> used throughout the testing
	 */
	private static VotePackingVerifier vpv = null;

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
	public void setup() throws ComponentVerifierException, ComponentSpecException, ComponentDataStoreException, FileNotFoundException, IOException, VerifierException, DataStoreException,
			SpecException {
		VotePackingVerifierSpec spec = new VotePackingVerifierSpec(IOUtils.readStringFromFile(specFile));
		spec.validateSchema();

		VotePackingDataStore dataStore = new VotePackingDataStore(spec, basePath, false);
		dataStore.readData();

		vpv = new VotePackingVerifier(dataStore, spec);

		assertNotNull(vpv);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		vpv = null;
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in spec form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVotePackingVerifierConstruction_1() throws Exception {
		VotePackingVerifierSpec spec = new VotePackingVerifierSpec(IOUtils.readStringFromFile(specFile));
		VotePackingDataStore dataStore = new VotePackingDataStore(spec, basePath, false);

		VotePackingVerifier bgv = new VotePackingVerifier(dataStore, spec);

		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in JSONObject form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVotePackingVerifierConstruction_2() throws Exception {
		String spec = IOUtils.readStringFromFile(specFile);

		VotePackingVerifier bgv = new VotePackingVerifier(new JSONObject(spec), basePath, false);
		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier can be created successfully
	 * using a spec in string form
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVotePackingVerifierConstruction_3() throws Exception {
		String spec = IOUtils.readStringFromFile(specFile);

		VotePackingVerifier bgv = new VotePackingVerifier(spec, basePath, false);
		assertNotNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier needs a valid spec and config
	 * file
	 * 
	 * @throws Exception
	 */
	@Test(expected = ComponentSpecException.class)
	public void testVotePackingVerifierConstruction_5() throws Exception {
		String spec = null;
		String basePath = null;

		VotePackingVerifier bgv = new VotePackingVerifier(spec, basePath, false);
		assertNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier needs a valid spec file
	 * 
	 * @throws Exception
	 */
	@Test(expected = ComponentSpecException.class)
	public void testVotePackingVerifierConstruction_6() throws Exception {

		VotePackingVerifier bgv = new VotePackingVerifier(new JSONObject(), basePath, false);
		assertNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier needs a valid spec file
	 * 
	 * @throws Exception
	 */
	@Test(expected = ComponentVerifierException.class)
	public void testVotePackingVerifierConstruction_7() throws Exception {

		VotePackingVerifier bgv = new VotePackingVerifier((VotePackingDataStore) null, (VotePackingVerifierSpec) null);
		assertNull(bgv);
	}

	/**
	 * Verify that a ballot generation verifier must have a data store passed to
	 * it
	 * 
	 * @throws Exception
	 */
	@Test(expected = ComponentVerifierException.class)
	public void testVotePackingVerifierConstruction_8() throws Exception {
		VotePackingVerifierSpec spec = new VotePackingVerifierSpec(IOUtils.readStringFromFile(specFile));

		VotePackingVerifier bgv = new VotePackingVerifier(null, spec);

		assertNotNull(bgv);
	}

	/**
	 * Verify the number of ballots to audit matches the number of ballots
	 * provided
	 */
	@Test
	public void testVerification() {
		assertTrue(vpv.doVerification());
	}
}