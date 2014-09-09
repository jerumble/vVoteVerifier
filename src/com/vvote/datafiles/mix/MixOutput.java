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
package com.vvote.datafiles.mix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a representation for the output from the mixnet. The mixnet will
 * shuffle and decrypt ciphers passed to it and will produce csv files
 * containing the preferences for each ballot
 * 
 * @author James Rumble
 * 
 */
public class MixOutput {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(MixOutput.class);

	/**
	 * The filepath provided
	 */
	private final String filePath;

	/**
	 * the output preferences
	 */
	private final List<CSVPreferences> ballotPreferences;

	/**
	 * The identifier for the mixnet output preferences
	 */
	private final RaceIdentifier identifier;

	/**
	 * Constructor for a <code>MixOutput</code> object
	 * 
	 * @param filepath
	 * @param hasRaceMap 
	 * @throws MixDataException
	 */
	public MixOutput(String filepath, boolean hasRaceMap) throws MixDataException {
		
		if(filepath == null){
			logger.error("Filename provided must be a valid file");
			throw new MixDataException("Filename provided must be a valid file");
		}
		
		if (filepath.length() == 0) {
			logger.error("Filename provided must be a valid file");
			throw new MixDataException("Filename provided must be a valid file");
		}

		this.filePath = filepath;

		List<List<String>> ballots;
		try {
			ballots = IOUtils.readCSVFromFile(this.filePath);
		} catch (FileNotFoundException e) {
			logger.error("Unable to create MixOutput object: {}", filepath);
			throw new MixDataException("Unable to create MixOutput object: " + filepath);
		} catch (IOException e) {
			logger.error("Unable to create MixOutput object: {}", filepath);
			throw new MixDataException("Unable to create MixOutput object: " + filepath);
		}

		if (ballots == null) {
			logger.error("Valid preferences must be provided");
			throw new MixDataException("Valid preferences must be provided");
		}
		
		this.ballotPreferences = new ArrayList<CSVPreferences>();
		
		for (List<String> prefs : ballots) {
			this.ballotPreferences.add(new CSVPreferences(prefs));
		}

		this.identifier = Utils.getRaceIdentifierFromCSVFileName(filepath, hasRaceMap);
	}

	/**
	 * Getter for the identifier of the mix output
	 * @return identifier
	 */
	public RaceIdentifier getIdentifier() {
		return this.identifier;
	}
	
	/**
	 * Getter for the list of ballot preferences
	 * @return ballotPreferences
	 */
	public List<CSVPreferences> getBallotPreferences() {
		return this.ballotPreferences;
	}
}
