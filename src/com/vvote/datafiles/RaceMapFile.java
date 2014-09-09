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
package com.vvote.datafiles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.RaceMapException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation for the race map file race_map.json
 * 
 * @author James Rumble
 * 
 */
public class RaceMapFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RaceMapFile.class);

	/**
	 * Holds each race map
	 */
	private Map<String, RaceMap> raceMap = null;

	/**
	 * Constructor for the RaceMapFile
	 * 
	 * @param filepath
	 * @throws RaceMapException
	 */
	public RaceMapFile(String filepath) throws RaceMapException {

		this.raceMap = new HashMap<String, RaceMap>();

		String line = null;

		RaceMap currentRaceMap = null;

		try (BufferedReader raceMapReader = new BufferedReader(new FileReader(filepath))) {
			// loop over each line of the race map file
			while ((line = raceMapReader.readLine()) != null) {

				currentRaceMap = new RaceMap(new JSONObject(line));
				this.raceMap.put(currentRaceMap.getIdentifier(), currentRaceMap);
			}
		} catch (FileNotFoundException e) {
			logger.error("Unable to create RaceMapFile representation", e);
			throw new RaceMapException("Unable to create RaceMapFile representation", e);
		} catch (IOException e) {
			logger.error("Unable to create RaceMapFile representation", e);
			throw new RaceMapException("Unable to create RaceMapFile representation", e);
		} catch (RaceMapException e) {
			logger.error("Unable to create RaceMapFile representation", e);
			throw new RaceMapException("Unable to create RaceMapFile representation", e);
		} catch (JSONException e) {
			logger.error("Unable to create RaceMapFile representation", e);
			throw new RaceMapException("Unable to create RaceMapFile representation", e);
		}
	}

	/**
	 * Returns a specific race map
	 * 
	 * @param identifier
	 * @return a specific race map with the provided identifier
	 */
	public RaceMap getRaceMap(String identifier) {
		if (this.raceMap.containsKey(identifier)) {
			return this.raceMap.get(identifier);
		}
		return null;
	}
}
