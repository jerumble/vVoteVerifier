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

import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.messages.typed.vote.RaceType;

/**
 * Provides an identifier for a specific race. Constructed from vote data
 * utilising only the race type and the race district
 * 
 * @author James Rumble
 * 
 */
public class BallotRaceIdentifier {

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
	 * @param raceType
	 * @param district
	 * @throws MixDataException
	 */
	public BallotRaceIdentifier(RaceType raceType, String district)
			throws MixDataException {

		if (raceType != null) {
			this.raceType = raceType;
		} else {
			throw new MixDataException(
					"A Race identifier must provide a valid race type");
		}

		if (district != null) {
			this.district = district;
		} else {
			throw new MixDataException(
					"A Race identifier must provide a valid district");
		}
	}

	@Override
	public String toString() {
		return "BallotRaceIdentifier [raceType=" + this.raceType
				+ ", district=" + this.district + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.district == null) ? 0 : this.district.hashCode());
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
		BallotRaceIdentifier other = (BallotRaceIdentifier) obj;
		if (this.district == null) {
			if (other.district != null)
				return false;
		} else if (!this.district.equals(other.district))
			return false;
		if (this.raceType != other.raceType)
			return false;
		return true;
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
}
