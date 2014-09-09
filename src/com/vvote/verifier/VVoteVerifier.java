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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.verifier.exceptions.VVoteVerifierException;
import com.vvote.verifier.fields.VerifierFields;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a high level Verifier to run which will call all other verifiers. It
 * uses a spec file to determine which other verifiers to call and run.
 * 
 * @author rumble
 * 
 */
public class VVoteVerifier implements IVerifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VVoteVerifier.class);

	/**
	 * Provides logging for the actual results produced in the verifier
	 */
	private static final Logger resultsLogger = LoggerFactory.getLogger("results");

	/**
	 * Provides the main entrance path to the system
	 * 
	 * @param args
	 * @throws VVoteVerifierException
	 */
	public static void main(String[] args) throws VVoteVerifierException {

		System.out.println("vVoteVerifier  Copyright (C) 2014 James Rumble");
		System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details see license.txt");
		System.out.println("This is free software, and you are welcome to redistribute it");
		System.out.println("under certain conditions; for details see license.txt");

		logger.debug("Started vVote Verifier");
		resultsLogger.info("Started vVote Verifier");

		// check for input path provided
		if (args.length == 1) {

			String basePath = args[0];

			logger.debug("Base Path provided: {}", basePath);
			resultsLogger.info("Base Path provided: {}", basePath);

			VVoteVerifier verifier = null;

			// create spec
			String spec = null;
			try {
				spec = IOUtils.readStringFromFile(VerifierFields.VVoteVerifier.SPEC_FILE);
			} catch (FileNotFoundException e) {
				logger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				resultsLogger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				System.exit(1);
			} catch (IOException e) {
				logger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				resultsLogger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				System.exit(1);
			}

			VVoteVerifierSpec verifierSpec = null;

			if (spec != null) {
				verifierSpec = new VVoteVerifierSpec(spec);
			} else {
				logger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				resultsLogger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				System.exit(1);
			}

			// check whether extra commits or final commits is used
			if (verifierSpec != null) {
				File commitsLocation = new File(IOUtils.findFile(verifierSpec.getFinalCommitsFolder(), basePath));

				boolean isEmpty = commitsLocation.list(filterVersionControlFiles()).length == 0;

				if (isEmpty) {
					commitsLocation = new File(IOUtils.findFile(verifierSpec.getExtraCommitsFolder(), basePath));
					isEmpty = commitsLocation.list(filterVersionControlFiles()).length == 0;

					if (!isEmpty) {
						logger.info("Using Extra Commits folder");
						verifier = new VVoteVerifier(verifierSpec, basePath, true);
					} else {
						System.exit(1);
					}
				} else {
					logger.info("Using Final Commits folder");
					verifier = new VVoteVerifier(verifierSpec, basePath, false);
				}

				if (verifier != null) {
					verifier.doVerification();
				} else {
					logger.error("There was a problem carrying out verification using the path provided: {}", basePath);
					resultsLogger.error("There was a problem carrying out verification using the path provided: {}", basePath);
					System.exit(1);
				}
			} else {
				logger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				resultsLogger.error("There was a problem carrying out verification using the path provided: {}", basePath);
				System.exit(1);
			}
		} else {

			logger.error("The base path folder location must be provided. Please provide the path to the election data");
			resultsLogger.error("The base path folder location must be provided. Please provide the path to the election data");
			System.exit(1);
		}
	}

	/**
	 * A list of all verifiers in use in the system
	 */
	private Map<String, Verifier> verifiers = null;

	/**
	 * Constructor for a VVoteVerifier object
	 * 
	 * @param spec
	 * @param basePath
	 * @param useExtraCommits
	 * 
	 * @throws VVoteVerifierException
	 */
	public VVoteVerifier(VVoteVerifierSpec spec, String basePath, boolean useExtraCommits) throws VVoteVerifierException {

		logger.info("Setting up the vVote Verifier");

		this.verifiers = new HashMap<String, Verifier>();
		try {

			logger.info("Setting up the verifiers");

			Set<VerifierDetails> verifierDetails = spec.getVerifierDetails();

			String currentSpec = null;

			for (VerifierDetails verifier : verifierDetails) {

				Class<?> cls = null;
				cls = Class.forName(verifier.getVerifierClass());

				Constructor<?> cons = null;

				cons = cls.getConstructor(String.class, String.class, boolean.class);

				currentSpec = IOUtils.readStringFromFile(verifier.getVerifierSpecFile());

				this.verifiers.put(verifier.getVerifierName(), (Verifier) cons.newInstance(currentSpec, basePath, useExtraCommits));
			}
		} catch (InstantiationException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (IllegalAccessException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (IllegalArgumentException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (InvocationTargetException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (ClassNotFoundException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (NoSuchMethodException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (SecurityException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (FileNotFoundException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		} catch (IOException e) {
			logger.error("Unable to create VVoteVerifier", e);
			throw new VVoteVerifierException("Unable to create VVoteVerifier", e);
		}
	}

	@Override
	public boolean doVerification() {

		logger.info("Starting vVote Verifier");
		resultsLogger.info("Starting vVote Verifier");

		boolean verified = true;

		for (Entry<String, Verifier> verifier : this.verifiers.entrySet()) {
			resultsLogger.info("Doing verification on: {}", verifier.getKey());

			verifier.getValue().getSpec().validateSchema();
			verifier.getValue().getDataStore().readData();

			if (!verifier.getValue().doVerification()) {
				verified = false;
			}
		}

		if (verified) {
			logger.debug("Full vVote system Verification was carried out successfully");
			resultsLogger.info("Full vVote system Verification was carried out successfully");
		} else {
			logger.debug("Full vVote system Verification was not carried out successfully - please check the logs");
			resultsLogger.error("Full vVote system Verification was not carried out successfully - please check the logs");
		}

		return verified;
	}

	/**
	 * Returns a filename filters which checks for svn files
	 * 
	 * @return a filename filter simply removing svn files
	 */
	private static FilenameFilter filterVersionControlFiles() {
		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.contains(".svn") || name.contains(".git")) {
					return false;
				}
				return true;
			}
		};
	}
}
