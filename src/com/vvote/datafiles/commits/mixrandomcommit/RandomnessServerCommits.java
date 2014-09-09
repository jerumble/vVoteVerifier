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
package com.vvote.datafiles.commits.mixrandomcommit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.MixCommitException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifierlibrary.utils.comparators.BallotSerialNumberComparator;

/**
 * Holds all the commitments to randomness values made by a particular mix
 * server to specific PoD Printers. Each of the mix servers has their own
 * MixCommit data which corresponds to the randomness commitments made by this
 * specific mix server to each of the POD printers with regard to the random
 * values they will use for re-encryption. Each RandomnessServerCommits object
 * represents a whole file of committed mix data
 * 
 * @author James Rumble
 * 
 */
public final class RandomnessServerCommits {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RandomnessServerCommits.class);

	/**
	 * Server name for the specific server making commitments to randomness
	 * values
	 */
	private final String serverName;

	/**
	 * File path for the specific server making commitments to randomness values
	 */
	private final String filePath;

	/**
	 * Randomness commitments. SerialNo : MixRandomnessCommit (holds serial no,
	 * randomness array)
	 */
	private final Map<String, MixCommitData> randomnessCommits;

	/**
	 * Constructor for a RandomnessServerCommits - takes in the server name and
	 * server file path and then reads in the commitments to randomness values
	 * itself
	 * 
	 * @param serverName
	 * @param filepath
	 * @throws MixCommitException
	 */
	public RandomnessServerCommits(String serverName, String filepath) throws MixCommitException {

		logger.debug("Creating a new RandomnessServerCommits object: {}, {}", serverName, filepath);

		this.serverName = serverName;
		this.filePath = filepath;

		this.randomnessCommits = new TreeMap<String, MixCommitData>(new BallotSerialNumberComparator());

		String line = null;
		MixCommitData mixRandomnessCommit = null;

		try (BufferedReader mixCommitReader = new BufferedReader(new FileReader(this.filePath))) {
			// loop through each line of each server file
			while ((line = mixCommitReader.readLine()) != null) {
				// create a separate mix randomness commit object for each
				// line of each server file
				mixRandomnessCommit = new MixCommitData(serverName, line);

				this.randomnessCommits.put(mixRandomnessCommit.getSerialNo(), null);

				mixRandomnessCommit = null;
			}
		} catch (MixCommitException e) {
			logger.error("Unable to create a RandomnessServerCommits. Error: {}", e);
			throw new MixCommitException("Unable to create a RandomnessServerCommits.", e);
		} catch (JSONException e) {
			logger.error("Unable to create a RandomnessServerCommits. Error: {}", e);
			throw new MixCommitException("Unable to create a RandomnessServerCommits.", e);
		} catch (FileNotFoundException e) {
			logger.error("Unable to create a RandomnessServerCommits. Error: {}", e);
			throw new MixCommitException("Unable to create a RandomnessServerCommits.", e);
		} catch (IOException e) {
			logger.error("Unable to create a RandomnessServerCommits. Error: {}", e);
			throw new MixCommitException("Unable to create a RandomnessServerCommits.", e);
		}
	}

	/**
	 * Getter for the file path
	 * 
	 * @return the file path for the Randomness server commitment
	 */
	public final String getFilePath() {
		return this.filePath;
	}

	/**
	 * Getter for a specific randomness commitment object
	 * 
	 * @param serialNo
	 * @return the mix randomness commit for the POD printer with the specified
	 *         serial number
	 */
	public final MixCommitData getMixRandomCommit(String serialNo) {

		logger.debug("Getting mix random commit: {}", serialNo);

		MixCommitData commit = null;

		if (this.randomnessCommits.containsKey(serialNo)) {
			commit = this.randomnessCommits.get(serialNo);

			if (commit == null) {

				logger.debug("Loading mix random commit data from file: {}", serialNo);

				try {
					this.loadCommit(serialNo);
				} catch (JSONException | MixCommitException | FileNotFoundException e) {
					logger.error("There was a problem reading the mix random commit data and getting the requested serial number: {}", serialNo);
					return null;
				}
			}

			return this.randomnessCommits.get(serialNo);
		}
		return null;
	}

	/**
	 * Loads a specific mix random commit from file
	 * 
	 * @param serialNo
	 * @throws MixCommitException
	 * @throws JSONException
	 * @throws FileNotFoundException
	 */
	private void loadCommit(String serialNo) throws JSONException, MixCommitException, FileNotFoundException {

		try (Scanner scanner = new Scanner(new File(this.filePath))) {

			String line = null;

			MixCommitData commit = null;

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.contains(serialNo)) {
					commit = new MixCommitData(this.serverName, line);

					this.randomnessCommits.put(commit.getSerialNo(), commit);
					break;
				}
			}

			scanner.close();
		}
	}

	/**
	 * Getter for an iterator over the MixRandomnessCommits
	 * 
	 * @return an unmodifiable version of the randomness commitments
	 */
	public final Set<String> getRandomnessCommitSerialNumbers() {
		return Collections.unmodifiableSet(this.randomnessCommits.keySet());
	}

	/**
	 * Getter for the server name
	 * 
	 * @return the server name
	 */
	public final String getServerName() {
		return this.serverName;
	}

	/**
	 * Checks whether the current <code>RandomnessServerCommits</code> object
	 * has the random commits for the provided serial number
	 * 
	 * @param serialNo
	 * @return true if the current <code>RandomnessServerCommits</code> object
	 *         has the random commits for the provided serial number
	 */
	public final boolean hasMixRandomCommit(String serialNo) {
		return this.randomnessCommits.keySet().contains(serialNo);
	}

	@Override
	public String toString() {
		return "RandomnessServerCommits [serverName=" + this.serverName + ", filePath=" + this.filePath + ", randomnessCommits=" + this.randomnessCommits + "]";
	}
}
