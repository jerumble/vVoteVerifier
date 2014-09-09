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
package com.vvote.commits;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vvote.commits.exceptions.CommitAttachmentInitException;
import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.commits.exceptions.CommitFileMessageInitException;
import com.vvote.commits.exceptions.CommitSignatureInitException;
import com.vvote.commits.exceptions.FinalCommitInitException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.json.JSONSchemaStore;

/**
 * The class <code>FinalCommitmentTest</code> contains tests for the class
 * <code>{@link FinalCommitment}</code>.
 */
public class FinalCommitmentTest {

	/**
	 * Valid json file
	 */
	private static final String jsonFile = "./testdata/commitments/ballotGen/1403161200000.json";

	/**
	 * Valid signature file
	 */
	private static final String signatureFile = "./testdata/commitments/ballotGen/1403161200000_signature.json";

	/**
	 * Valid attachment file
	 */
	private static final String attachmentFile = "./testdata/commitments/ballotGen/1403161200000_attachments.zip";

	/**
	 * JSONSchemaStore object
	 */
	private static JSONSchemaStore schemaStore = null;

	/**
	 * The <code>FinalCommitment</code> used throughout the testing
	 */
	private static FinalCommitment finalCommitment = null;

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 *             if the initialization fails for some reason
	 */
	@Before
	public void setUp() throws Exception {
		schemaStore = JSONSchemaStore.getInstance();
		finalCommitment = this.constructFinalCommitment(jsonFile, attachmentFile, signatureFile);
	}

	/**
	 * Perform post-test clean-up.
	 * 
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 */
	@After
	public void tearDown() throws Exception {
		finalCommitment = null;
	}

	/**
	 * Helper method to construct a final commitment
	 * 
	 * @param fileFilename
	 * @param attachmentFilename
	 * @param signaureFilename
	 * @return a final commitment
	 * @throws CommitFileMessageInitException
	 * @throws CommitFileInitException
	 * @throws CommitAttachmentInitException
	 * @throws CommitSignatureInitException
	 * @throws FinalCommitInitException
	 */
	public FinalCommitment constructFinalCommitment(String fileFilename, String attachmentFilename, String signaureFilename) throws CommitFileMessageInitException, CommitFileInitException,
			CommitAttachmentInitException, CommitSignatureInitException, FinalCommitInitException {

		CommitFileMessage fileMessage = new CommitFileMessage(fileFilename);
		CommitAttachment attachmentMessage = new CommitAttachment(attachmentFilename);
		CommitSignature signatureMessage = new CommitSignature(signaureFilename);

		assertNotNull(fileMessage);
		assertNotNull(attachmentMessage);
		assertNotNull(signatureMessage);

		return new FinalCommitment(fileMessage, attachmentMessage, signatureMessage);
	}

	/**
	 * Test creation of a final commitment containing ballot generation data
	 * 
	 * @throws FinalCommitInitException
	 * @throws CommitSignatureInitException
	 * @throws CommitAttachmentInitException
	 * @throws CommitFileInitException
	 * @throws CommitFileMessageInitException
	 */
	@Test
	public void testFinalCommitment() throws CommitFileMessageInitException, CommitFileInitException, CommitAttachmentInitException, CommitSignatureInitException, FinalCommitInitException {
		final String fileFilename = "./testdata/commitments/ballotGen/1403161200000.json";
		final String attachmentFilename = "./testdata/commitments/ballotGen/1403161200000_attachments.zip";
		final String signaureFilename = "./testdata/commitments/ballotGen/1403161200000_signature.json";

		FinalCommitment finalCommitment = this.constructFinalCommitment(fileFilename, attachmentFilename, signaureFilename);

		assertNotNull(finalCommitment);
	}

	/**
	 * Test creation of a final commitment containing vote data
	 * 
	 * @throws JSONException
	 * @throws JSONIOException
	 * @throws FinalCommitInitException
	 * @throws CommitSignatureInitException
	 * @throws CommitAttachmentInitException
	 * @throws CommitFileInitException
	 * @throws CommitFileMessageInitException
	 */
	@Test
	public void testFinalCommitment_1() throws JSONException, JSONIOException, CommitFileMessageInitException, CommitFileInitException, CommitAttachmentInitException, CommitSignatureInitException,
			FinalCommitInitException {
		final String fileFilename = "./testdata/commitments/votes/1403247600000.json";
		final String attachmentFilename = "./testdata/commitments/votes/1403247600000_attachments.zip";
		final String signaureFilename = "./testdata/commitments/votes/1403247600000_signature.json";

		FinalCommitment finalCommitment = this.constructFinalCommitment(fileFilename, attachmentFilename, signaureFilename);

		assertNotNull(finalCommitment);
	}

	/**
	 * Test invalid creation of a final commitment
	 * 
	 * @throws JSONException
	 * @throws JSONIOException
	 * @throws FinalCommitInitException
	 * @throws CommitSignatureInitException
	 * @throws CommitAttachmentInitException
	 * @throws CommitFileInitException
	 * @throws CommitFileMessageInitException
	 */
	@Test
	public void testFinalCommitment_2() throws JSONException, JSONIOException, CommitFileMessageInitException, CommitFileInitException, CommitAttachmentInitException, CommitSignatureInitException,
			FinalCommitInitException {
		final String fileFilename = "./testdata/commitments/ballotGen/1403161200000.json";
		final String attachmentFilename = "./testdata/commitments/votes/1403247600000_attachments.zip";

		FinalCommitment finalCommitment = new FinalCommitment(new CommitAttachment(attachmentFilename));
		finalCommitment.setFileMessage(new CommitFileMessage(fileFilename));

		assertNotNull(finalCommitment);
		assertNull(finalCommitment.getFileMessage());

		final String fileFilenameValid = "./testdata/commitments/votes/1403247600000.json";
		finalCommitment.setFileMessage(new CommitFileMessage(fileFilenameValid));
		assertNotNull(finalCommitment.getFileMessage());
	}

	/**
	 * Run the get Attachment method
	 */
	@Test
	public void testGetAttachment() {
		assertNotNull(finalCommitment.getAttachment());
	}

	/**
	 * Run the get file message method
	 */
	@Test
	public void testGetFileMessage() {
		assertNotNull(finalCommitment.getFileMessage());
	}

	/**
	 * Run the get signature method
	 */
	@Test
	public void testGetSignature() {
		assertNotNull(finalCommitment.getSignature());
	}

	/**
	 * Getter for the JSONSchema store object
	 * 
	 * @return schemaStore
	 */
	public static JSONSchemaStore getSchemaStore() {
		return schemaStore;
	}
}
