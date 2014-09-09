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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.ConfigFileConstants;
import com.vvote.messages.typed.vote.RaceType;
import com.vvote.verifier.exceptions.ConfigException;

/**
 * Contains a config for parameters defined for the vote packing process. These
 * parameters are used for verification of vote packing
 * 
 * @author James Rumble
 * 
 */
public class VotePackingConfig {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(VotePackingConfig.class);

	/**
	 * The name of the curve being used for vote packing
	 */
	private final String curve;

	/**
	 * The name of the padding file
	 */
	private final String paddingFile;

	/**
	 * The size of the la race line length
	 */
	private final int laLineLength;

	/**
	 * The size of the la packing
	 */
	private final int laPacking;

	/**
	 * The size of the lc btl race line length
	 */
	private final int lcBTLLineLength;

	/**
	 * The size of the lc btl packing
	 */
	private final int lcBTLPacking;

	/**
	 * The name of the candidates table folder
	 */
	private final String candidateTablesFolder;

	/**
	 * Contains a list of the races which are not packed
	 */
	private final Map<RaceType, Boolean> useDirect;

	/**
	 * Constructor for a VotePackingConfig object from a String
	 * 
	 * @param configLocation
	 *            The filename or filepath of the config in string format
	 * @throws ConfigException
	 */
	public VotePackingConfig(String configLocation) throws ConfigException {
		logger.debug("Reading in Vote Packing specific configuration data");

		if (configLocation == null) {
			logger.error("Cannot successfully create a VotePackingConfig");
			throw new ConfigException("Cannot successfully create a VotePackingConfig");
		}

		if (configLocation.length() == 0) {
			logger.error("Cannot successfully create a VotePackingConfig");
			throw new ConfigException("Cannot successfully create a VotePackingConfig");
		}

		try {
			Configuration config = new PropertiesConfiguration(configLocation);

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.CURVE)) {
				this.curve = config.getString(ConfigFileConstants.VotePackingConfig.CURVE);
			} else {
				logger.error("Cannot successfully create a VotePackingConfig - must contain the name of the curve used");
				throw new ConfigException("Cannot successfully create a VotePackingConfig - must contain the name of the curve used");
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.PADDING_FILE)) {
				this.paddingFile = config.getString(ConfigFileConstants.VotePackingConfig.PADDING_FILE);
			} else {
				logger.error("Cannot successfully create a VotePackingConfig - must contain the name of the padding file");
				throw new ConfigException("Cannot successfully create a VotePackingConfig - must contain the name of the padding file");
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.TABLE_LA_LINE_LENGTH)) {
				this.laLineLength = config.getInt(ConfigFileConstants.VotePackingConfig.TABLE_LA_LINE_LENGTH);
			} else {
				this.laLineLength = -1;
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.TABLE_LA_PACKING)) {
				this.laPacking = config.getInt(ConfigFileConstants.VotePackingConfig.TABLE_LA_PACKING);
			} else {
				this.laPacking = -1;
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.TABLE_BTL_LINE_LENGTH)) {
				this.lcBTLLineLength = config.getInt(ConfigFileConstants.VotePackingConfig.TABLE_BTL_LINE_LENGTH);
			} else {
				this.lcBTLLineLength = -1;
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.TABLE_BTL_PACKING)) {
				this.lcBTLPacking = config.getInt(ConfigFileConstants.VotePackingConfig.TABLE_BTL_PACKING);
			} else {
				this.lcBTLPacking = -1;
			}

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.CANDIDATE_TABLES)) {
				this.candidateTablesFolder = config.getString(ConfigFileConstants.VotePackingConfig.CANDIDATE_TABLES);
			} else {
				logger.error("Cannot successfully create a VotePackingConfig - must contain the name of the candidates table");
				throw new ConfigException("Cannot successfully create a VotePackingConfig - must contain the candidates table");
			}

			this.useDirect = new HashMap<RaceType, Boolean>();
			this.useDirect.put(RaceType.LA, false);
			this.useDirect.put(RaceType.LC_ATL, false);
			this.useDirect.put(RaceType.LC_BTL, false);

			if (config.containsKey(ConfigFileConstants.VotePackingConfig.USE_DIRECT)) {
				String[] directlyUsed = config.getStringArray(ConfigFileConstants.VotePackingConfig.USE_DIRECT);

				for (String race : directlyUsed) {
					RaceType raceType = RaceType.fromString(race);

					if (raceType != null) {
						this.useDirect.remove(raceType);
						this.useDirect.put(raceType, true);
					} else {
						logger.error("Cannot successfully create a VotePackingConfig - misformed use direct race type");
						throw new ConfigException("Cannot successfully create a VotePackingConfig - misformed use direct race type");
					}
				}
			}

		} catch (ConfigurationException e) {
			logger.error("Cannot successfully create a VotePackingConfig", e);
			throw new ConfigException("Cannot successfully create a VotePackingConfig", e);
		}
	}

	/**
	 * Getter for the name of the curve used for packing
	 * 
	 * @return curve
	 */
	public String getCurve() {
		return this.curve;
	}

	/**
	 * Getter for the name of the padding file
	 * 
	 * @return paddingFile
	 */
	public String getPaddingFile() {
		return this.paddingFile;
	}

	/**
	 * Getter for the size of the la line length
	 * 
	 * @return laLineLength
	 */
	public int getLaLineLength() {
		return this.laLineLength;
	}

	/**
	 * Getter for the size of the la packing
	 * 
	 * @return laPacking
	 */
	public int getLaPacking() {
		return this.laPacking;
	}

	/**
	 * Getter for the size of the lc btl line length
	 * 
	 * @return lcBTLLineLength
	 */
	public int getLcBTLLineLength() {
		return this.lcBTLLineLength;
	}

	/**
	 * Getter for the size of the lc btl packing
	 * 
	 * @return lcBTLPacking
	 */
	public int getLcBTLPacking() {
		return this.lcBTLPacking;
	}

	/**
	 * Getter for the name of the candidates tables folder
	 * 
	 * @return candidateTablesFolder
	 */
	public String getCandidateTablesFolder() {
		return this.candidateTablesFolder;
	}

	/**
	 * Getter for the map for which races are used directly
	 * 
	 * @return useDirect
	 */
	public Map<RaceType, Boolean> getUseDirect() {
		return this.useDirect;
	}

	/**
	 * Getter for whether a specific race is packed or not
	 * 
	 * @param type
	 * @return whether the specified RaceType is packed
	 */
	public boolean isPacked(RaceType type) {
		return !this.useDirect.get(type);
	}
}
