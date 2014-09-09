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
package com.vvote.verifierlibrary.utils.crypto;

import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.crypto.Commitment;
import org.bouncycastle.crypto.commitments.HashCommitter;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.CryptoConstants;
import com.vvote.verifierlibrary.exceptions.CommitException;
import com.vvote.verifierlibrary.exceptions.FileHashException;
import com.vvote.verifierlibrary.utils.Utils;

/**
 * Provides a utility class to provide cryptographic operations such as carrying
 * out hash commitment checks
 * 
 * @author James Rumble
 * 
 */
public class CryptoUtils {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

	/**
	 * Flag to identify whether the security provider has been added
	 */
	private static boolean providerAdded;

	/**
	 * Loads in the CryptoOpenSSL library
	 */
	static {
		System.loadLibrary(CryptoConstants.CRYPTO_OPENSSL_LIBRARY);
	}

	/**
	 * Perform a verification on a hash commitment using bouncy castle. This
	 * implementation uses the HashCommitter from bouncy castle and the
	 * isRevealed function
	 * 
	 * @param commitment
	 * @param witness
	 * @param randomValue
	 * @return whether the commitment check is successful
	 * @throws CommitException
	 */
	private static boolean bouncyCastleVerifyHashCommitment(byte[] commitment, byte[] witness, byte[] randomValue) throws CommitException {

		logger.debug("Verifying hash commitment using Bouncy castle implementation");

		// initialise a hash committer
		HashCommitter hashCommitter = new HashCommitter(new SHA256Digest(), new SecureRandom(witness));

		MessageDigest md = null;

		try {
			logger.debug("Initialising message digest");
			// initialise the message digest
			md = MessageDigest.getInstance(CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM);

			// ensure the random value is the correct length
			if (randomValue.length > CryptoConstants.Commitments.RANDOM_VALUE_MAXIMUM_LENGTH) {
				logger.debug("Hashing random value to the correct length");
				md.reset();
				randomValue = md.digest(randomValue);
			}

			// initialise a new Commitment
			Commitment comm = new Commitment(witness, commitment);

			// check whether the given random value opens the commitment
			if (!hashCommitter.isRevealed(comm, randomValue)) {
				logger.error("Bouncy castle hash commitment verification failed");
				return false;
			}

		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not initialise the message digest with the specified algorithm: {}", CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM, e);
			throw new CommitException("Could not initialise the message digest with the specified algorithm: " + CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM, e);
		}

		return true;
	}

	/**
	 * Hashes a file and returns the hash
	 * 
	 * @param file
	 * @param digest
	 * @return the hash of the provided file
	 * @throws FileHashException
	 */
	public static int hashFile(File file, MessageDigest digest) throws FileHashException {

		logger.debug("Performing a hash on the file: {}", file.getPath());

		int filesize = 0;
		int readSize = 0;

		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("Unable to hash the file and add it into the digest", e);
			throw new FileHashException("Unable to hash the file and add it into the digest", e);
		}
		byte[] bytesIn = new byte[1024];

		try (DigestInputStream dis = new DigestInputStream(bis, digest)) {
			while ((readSize = dis.read(bytesIn)) != -1) {
				filesize += readSize;
			}
		} catch (IOException e) {
			logger.error("Unable to hash the file and add it into the digest", e);
			throw new FileHashException("Unable to hash the file and add it into the digest", e);
		}

		return filesize;
	}

	/**
	 * Adds the BouncyCastle Security Provider. May be called multiple times
	 */
	public static void initProvider() {
		if (!providerAdded) {
			logger.debug("Adding BouncyCastle provider");
			Security.addProvider(new BouncyCastleProvider());
			providerAdded = true;

			PairingFactory.getInstance().setUsePBCWhenPossible(true);
		}
	}

	/**
	 * Perform a verification on a hash commitment using java security. Manually
	 * performs the concatenation of the two parts of the commitment and checks
	 * the produced hash against the given commitment value
	 * 
	 * @param commitment
	 * @param witness
	 * @param randomValue
	 * @return whether the commitment check is successful
	 * @throws CommitException
	 */
	private static boolean javaSecurityVerifyHashCommitment(byte[] commitment, byte[] witness, byte[] randomValue) throws CommitException {

		logger.debug("Verifying hash commitment using Java security implementation");

		MessageDigest md = null;

		try {
			logger.debug("Initialising message digest");
			// initialise the message digest
			md = MessageDigest.getInstance(CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM);

			// ensure the random value is the correct length
			if (randomValue.length > CryptoConstants.Commitments.RANDOM_VALUE_MAXIMUM_LENGTH) {
				logger.debug("Hashing random value to the correct length");
				md.reset();

				randomValue = md.digest(randomValue);
			}

			// add the witness value to the digest
			md.update(witness);
			// concatenate the random value with the witness value and perform a
			// hash
			byte[] combinedHash = md.digest(randomValue);

			// check whether the combined hash and the given commitment are
			// equal
			if (!Arrays.equals(combinedHash, commitment)) {
				logger.error("Combined hash and commitment value do not match");
				return false;
			}

		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not initialise the message digest with the specified algorithm: {}", CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM, e);
			throw new CommitException("Could not initialise the message digest with the specified algorithm: " + CryptoConstants.Commitments.COMMITMENT_HASH_ALGORITHM, e);
		}

		return true;
	}

	/**
	 * Declares the 'native' jni function which will call the
	 * openSSLVerifyHashCommitment function from the CryptoOpenSSL library
	 * 
	 * @param commitment
	 * @param witness
	 * @param randomValue
	 * @return whether the commitment check is successful
	 */
	private native static boolean openSSLVerifyHashCommitment(String commitment, String witness, String randomValue);

	/**
	 * Carries out a hash commitment check on input bytes using the string
	 * implementation of the method so we can use all three hash commitment
	 * check functions. This is not the most efficient implementation to use
	 * 
	 * @param commitmentByte
	 * @param witnessByte
	 * @param randomValueByte
	 * @return whether the commitment check is successful
	 * @throws CommitException
	 */
	public static boolean verifyHashCommitment(byte[] commitmentByte, byte[] witnessByte, byte[] randomValueByte) throws CommitException {

		// convert the inputs from bytes to hex strings before checking the hash
		// commitment
		String commitment = Utils.byteToHexString(commitmentByte);
		String witness = Utils.byteToHexString(witnessByte);
		String randomValue = Utils.byteToHexString(randomValueByte);

		return verifyHashCommitment(commitment, witness, randomValue);
	}

	/**
	 * This is the main function to be called for computing a hash commitment
	 * check. This calls each of the implementations: Java.security,
	 * bouncycastle and openssl
	 * 
	 * @param commitment
	 * @param witness
	 * @param randomValue
	 * @return whether the commitment check is successful
	 * @throws CommitException
	 */
	public static boolean verifyHashCommitment(String commitment, String witness, String randomValue) throws CommitException {

		logger.debug("Verifying hash commitment on commitment: {}, witness: {}, random value: {}", commitment, witness, randomValue);

		byte[] commitmentByte = null;
		byte[] witnessByte = null;
		byte[] randomValueByte = null;

		// get the byte equivalent of the hex strings
		commitmentByte = Utils.decodeHexData(commitment);
		witnessByte = Utils.decodeHexData(witness);
		randomValueByte = Utils.decodeHexData(randomValue);

		logger.debug("Verifying hash commitment: Bouncy castle implementation");
		// verify using bouncy castle
		if (!bouncyCastleVerifyHashCommitment(commitmentByte, witnessByte, randomValueByte)) {
			return false;
		}

		logger.debug("Verifying hash commitment: Java Security implementation");
		// verify using java security
		if (!javaSecurityVerifyHashCommitment(commitmentByte, witnessByte, randomValueByte)) {
			return false;
		}

		logger.debug("Verifying hash commitment: OpenSSL implementation");
		// verify using openssl
		if (!openSSLVerifyHashCommitment(commitment, witness, randomValue)) {
			return false;
		}

		return true;
	}

	/**
	 * Stops external creation
	 */
	private CryptoUtils() {
		return;
	}
}
