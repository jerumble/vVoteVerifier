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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.RaceMapException;
import com.vvote.datafiles.fields.DataFileFields;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation of a single Race mapping
 * 
 * @author James Rumble
 * 
 */
public class RaceMap {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(RaceMap.class);

	/**
	 * identifier - is unique
	 */
	private final String identifier;

	/**
	 * Name of the race
	 */
	private final String raceName;

	/**
	 * identifier of the race
	 */
	private final String RaceId;

	/**
	 * name of the district
	 */
	private final String district;

	/**
	 * Constructor for a single Race mapping
	 * 
	 * @param json
	 * @throws RaceMapException
	 */
	public RaceMap(JSONObject json) throws RaceMapException {

		// check that the JSON message is not null
		if (json != null) {

			try {
				// get and set the identifier
				if (json.has(DataFileFields.RaceMap.ID)) {
					this.identifier = json.getString(DataFileFields.RaceMap.ID);
				} else {
					logger.error("The identifier must be provided for a RaceMap");
					throw new RaceMapException("The identifier must be provided for a RaceMap");
				}
				// get and set the raceName
				if (json.has(DataFileFields.RaceMap.RACE_NAME)) {
					this.raceName = json.getString(DataFileFields.RaceMap.RACE_NAME);
				} else {
					logger.error("The raceName must be provided for a RaceMap");
					throw new RaceMapException("The raceName must be provided for a RaceMap");
				}
				// get and set the RaceId
				if (json.has(DataFileFields.RaceMap.RACE_ID)) {
					this.RaceId = json.getString(DataFileFields.RaceMap.RACE_ID);
				} else {
					logger.error("The RaceId must be provided for a RaceMap");
					throw new RaceMapException("The RaceId must be provided for a RaceMap");
				}
				// get and set the district
				if (json.has(DataFileFields.RaceMap.DISTRICT)) {

					String fullDistrict = json.getString(DataFileFields.RaceMap.DISTRICT);

					this.district = fullDistrict.substring(0, fullDistrict.lastIndexOf(" "));
				} else {
					logger.error("The district must be provided for a RaceMap");
					throw new RaceMapException("The district must be provided for a RaceMap");
				}
			} catch (JSONException e) {
				logger.error("Unable to create a RaceMap. Error: {}", e);
				throw new RaceMapException("Unable to create a RaceMap.", e);
			}
		} else {
			logger.error("A RaceMap object must be a valid JSON message");
			throw new RaceMapException("A RaceMap object must be a valid JSON message");
		}
	}

	/**
	 * Getter for the identifier
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Getter for the raceName
	 * 
	 * @return raceName
	 */
	public String getRaceName() {
		return this.raceName;
	}

	/**
	 * Getter for the RaceId
	 * 
	 * @return RaceId
	 */
	public String getRaceId() {
		return this.RaceId;
	}

	/**
	 * Getter for the district
	 * 
	 * @return district
	 */
	public String getDistrict() {
		return this.district;
	}
}
