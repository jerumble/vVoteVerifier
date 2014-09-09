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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.ConfigFileConstants;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifier.exceptions.ConfigException;

/**
 * Contains a config for parameters defined prior to ballot generation These
 * parameters are used for verification of the ballot generation process
 * 
 * @author James Rumble
 * 
 */
public class BallotGenerationConfig {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BallotGenerationConfig.class);

	/**
	 * the size of the LA race
	 */
	private final int laSize;

	/**
	 * the size of the LC ATL race
	 */
	private final int lcATLSize;

	/**
	 * the size of the LC BTL race
	 */
	private final int lcBTLSize;

	/**
	 * The number of ballots to audit
	 */
	private final int ballotsToAudit;

	/**
	 * The number of ballots to generate
	 */
	private final int ballotsToGenerate;

	/**
	 * The location of the ballots output folder
	 */
	private final String ballotOutputFolder;

	/**
	 * The filename for the ballots output
	 */
	private final String ballotListFile;

	/**
	 * The filename for the ballots database
	 */
	private final String ballotDBFile;

	/**
	 * Provides the total number of candidates involved in the ballot generation
	 */
	private final int totalCandidates;

	/**
	 * Constructor for a BallotGenerationConfig object from a JSONObject
	 * 
	 * @param config
	 *            The config in JSON format
	 * @throws ConfigException
	 */
	public BallotGenerationConfig(JSONObject config) throws ConfigException {

		logger.debug("Reading in Ballot Generation specific configuration data");

		if (config == null) {
			logger.error("Cannot successfully create a BallotGenerationConfig");
			throw new ConfigException("Cannot successfully create a BallotGenerationConfig");
		}

		if (config.length() == 0) {
			logger.error("Cannot successfully create a BallotGenerationConfig");
			throw new ConfigException("Cannot successfully create a BallotGenerationConfig");
		}

		// get the number of candidates running in each race
		JSONArray races;
		try {
			if (config.has(ConfigFileConstants.BallotGenerationConfig.RACES)) {
				races = config.getJSONArray(ConfigFileConstants.BallotGenerationConfig.RACES);

				if (races.length() != 3) {
					logger.error("Ballot Generation Config file must contain 3 races");
					throw new ConfigException("Ballot Generation Config file must contain 3 races");
				}

				int race = 0;
				if (races.getJSONObject(race).getString(ConfigFileConstants.BallotGenerationConfig.ID).equals(ConfigFileConstants.BallotGenerationConfig.LA)) {
					this.laSize = races.getJSONObject(race).getInt(ConfigFileConstants.BallotGenerationConfig.CANDIDATES);
				} else {
					logger.error("Error in Ballot Generation Config file for LA Size");
					throw new ConfigException("Error in Ballot Generation Config file for LA Size");
				}

				race = 1;
				if (races.getJSONObject(race).getString(ConfigFileConstants.BallotGenerationConfig.ID).equals(ConfigFileConstants.BallotGenerationConfig.LC_ATL)) {
					this.lcATLSize = races.getJSONObject(race).getInt(ConfigFileConstants.BallotGenerationConfig.CANDIDATES);
				} else {
					logger.error("Error in Ballot Generation Config file for LC_ATL Size");
					throw new ConfigException("Error in Ballot Generation Config file for LC_ATL Size");
				}

				race = 2;
				if (races.getJSONObject(race).getString(ConfigFileConstants.BallotGenerationConfig.ID).equals(ConfigFileConstants.BallotGenerationConfig.LC_BTL)) {
					this.lcBTLSize = races.getJSONObject(race).getInt(ConfigFileConstants.BallotGenerationConfig.CANDIDATES);
				} else {
					logger.error("Error in Ballot Generation Config file for LC_BTL Size");
					throw new ConfigException("Error in Ballot Generation Config file for LC_BTL Size");
				}
			} else {
				logger.error("Cannot successfully create a BallotGenerationConfig - must contain races");
				throw new ConfigException("Cannot successfully create a BallotGenerationConfig - must contain races");
			}

			// get the number of ballots to audit
			if (config.has(ConfigFileConstants.BallotGenerationConfig.BALLOTS_TO_AUDIT)) {
				this.ballotsToAudit = config.getInt(ConfigFileConstants.BallotGenerationConfig.BALLOTS_TO_AUDIT);

				if (this.ballotsToAudit < 0) {
					logger.error("Ballots to audit must be positive");
					throw new ConfigException("Ballots to audit must be positive");
				}
			} else {
				logger.error("Cannot successfully create a BallotGenerationConfig - must contain the number of ballots to audit");
				throw new ConfigException("Cannot successfully create a BallotGenerationConfig - must contain the number of ballots to audit");
			}

			// get the number of ballots to generate
			if (config.has(ConfigFileConstants.BallotGenerationConfig.BALLOTs_TO_GENERATE)) {
				this.ballotsToGenerate = config.getInt(ConfigFileConstants.BallotGenerationConfig.BALLOTs_TO_GENERATE);

				if (this.ballotsToGenerate <= 0) {
					logger.error("Ballots to generate must be positive");
					throw new ConfigException("Ballots to generate must be positive");
				}
			} else {
				logger.error("Cannot successfully create a BallotGenerationConfig - must contain the number of ballots to generate");
				throw new ConfigException("Cannot successfully create a BallotGenerationConfig - must contain the number of ballots to generate");
			}

			// get the location for the output ballots file
			if (config.has(ConfigFileConstants.BallotGenerationConfig.BALLOT_OUTPUT_FOLDER)) {
				this.ballotOutputFolder = config.getString(ConfigFileConstants.BallotGenerationConfig.BALLOT_OUTPUT_FOLDER);
			} else {
				this.ballotOutputFolder = null;
			}

			// get the filename for the output ballots file
			if (config.has(ConfigFileConstants.BallotGenerationConfig.BALLOT_LIST)) {
				this.ballotListFile = config.getString(ConfigFileConstants.BallotGenerationConfig.BALLOT_LIST);
			} else {
				this.ballotListFile = null;
			}

			// get the filename for the output ballots database
			if (config.has(ConfigFileConstants.BallotGenerationConfig.BALLOT_DB)) {
				this.ballotDBFile = config.getString(ConfigFileConstants.BallotGenerationConfig.BALLOT_DB);
			} else {
				this.ballotDBFile = null;
			}

			if (this.ballotsToAudit > this.ballotsToGenerate) {
				logger.error("The number of ballots to audit cannot be larger than the number to generate");
				throw new ConfigException("The number of ballots to audit cannot be larger than the number to generate");
			}

		} catch (JSONException e) {
			logger.error("Cannot successfully create a BallotGenerationConfig");
			throw new ConfigException("Cannot successfully create a BallotGenerationConfig");
		}

		this.totalCandidates = this.laSize + this.lcATLSize + this.lcBTLSize;

		logger.debug("Successfully finished setting up ballot generation configuration parameters");
	}

	/**
	 * Constructor for a BallotGenerationConfig object from a string spec
	 * 
	 * @param config
	 *            The configuration in string format
	 * @throws JSONException
	 * @throws ConfigException
	 */
	public BallotGenerationConfig(String config) throws ConfigException, JSONException {
		this(new JSONObject(config));
	}

	/**
	 * Getter for the filename of the ballots output database
	 * 
	 * @return the filename of the ballots output database
	 */
	public String getBallotDBFile() {
		return this.ballotDBFile;
	}

	/**
	 * Getter for the filename of the ballots output file
	 * 
	 * @return the filename of the ballots output file
	 */
	public String getBallotListFile() {
		return this.ballotListFile;
	}

	/**
	 * Getter for the location of the ballots output file
	 * 
	 * @return the location of the ballots output file
	 */
	public String getBallotOutputFolder() {
		return this.ballotOutputFolder;
	}

	/**
	 * Getter for the number of ballots to audit
	 * 
	 * @return the number of ballots to audit
	 */
	public int getBallotsToAudit() {
		return this.ballotsToAudit;
	}

	/**
	 * Getter for the number of ballots to generate
	 * 
	 * @return the number of ballots to generate
	 */
	public int getBallotsToGenerate() {
		return this.ballotsToGenerate;
	}

	/**
	 * Getter for the number of candidates in the LA race
	 * 
	 * @return the LA size
	 */
	public int getLASize() {
		return this.laSize;
	}

	/**
	 * Getter for the number of candidates in the LA ATL race
	 * 
	 * @return the LA ATL size
	 */
	public int getLcATLSize() {
		return this.lcATLSize;
	}

	/**
	 * Getter for the number of candidates in the LA BTL race
	 * 
	 * @return the LA BTL size
	 */
	public int getLcBTLSize() {
		return this.lcBTLSize;
	}

	/**
	 * Gets the total number of candidates involved in the election
	 * 
	 * @return totalCandidates
	 */
	public int getNumberOfCandidates() {
		return this.totalCandidates;
	}
}
