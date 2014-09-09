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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.messages.typed.vote.RaceType;

/**
 * Provides an identifier for a specific race constructed from a file name
 * including the race id and race name on top of the race type and race district
 * 
 * @author James Rumble
 * 
 */
public class RaceIdentifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(RaceIdentifier.class);

	/**
	 * Identifier for the race
	 */
	private final String raceId;

	/**
	 * Name of the race
	 */
	private final String raceName;

	/**
	 * Type of the race
	 */
	private final RaceType raceType;

	/**
	 * name of the district
	 */
	private final String district;

	/**
	 * Constructor for a <code>RaceIdentifier</code>
	 * 
	 * @param raceId
	 * @param raceName
	 * @param raceType
	 * @param district
	 * @throws MixDataException
	 */
	public RaceIdentifier(String raceId, String raceName, RaceType raceType,
			String district) throws MixDataException {

		logger.debug("Creating a new RaceIdentifier: ({},{},{})", raceId,
				raceName, raceType);

		if (raceId != null) {
			this.raceId = raceId;
		} else {
			logger.error("A Race identifier must provide a valid race id");
			throw new MixDataException(
					"A Race identifier must provide a valid race id");
		}

		if (raceType != null) {
			this.raceType = raceType;
		} else {
			logger.error("A Race identifier must provide a valid race type");
			throw new MixDataException(
					"A Race identifier must provide a valid race type");
		}

		this.district = district;

		this.raceName = raceName;

		logger.debug(
				"Successfully created a new RaceIdentifier: ({},{},{},{})",
				raceId, raceName, raceType, district);
	}

	/**
	 * Getter for the race id of the race
	 * 
	 * @return raceId
	 */
	public String getRaceId() {
		return this.raceId;
	}

	/**
	 * Getter for the name of the race
	 * 
	 * @return raceName
	 */
	public String getRaceName() {
		return this.raceName;
	}

	/**
	 * Getter for the race type
	 * 
	 * @return raceType
	 */
	public RaceType getRaceType() {
		return this.raceType;
	}

	/**
	 * Getter for the race district
	 * 
	 * @return district
	 */
	public String getDistrict() {
		return this.district;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.raceId == null) ? 0 : this.raceId.hashCode());
		result = prime * result
				+ ((this.raceType == null) ? 0 : this.raceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RaceIdentifier other = (RaceIdentifier) obj;
		if (this.raceId == null) {
			if (other.raceId != null)
				return false;
		} else if (!this.raceId.equals(other.raceId))
			return false;
		if (this.raceType != other.raceType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RaceIdentifier [raceId=" + this.raceId + ", raceName="
				+ this.raceName + ", raceType=" + this.raceType + ", district="
				+ this.district + "]";
	}
}
