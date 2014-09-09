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
package com.vvote.datafiles.commits.auditcommit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.BallotAuditCommitException;

/**
 * Holds a pair of randomness values
 * 
 * @author James Rumble
 * 
 */
public final class RandomnessPair {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(RandomnessPair.class);

	/**
	 * Holds the witness value used for commitments
	 */
	private final String witness;

	/**
	 * Holds the actual randomness value used for encryption
	 */
	private final String randomnessValue;

	/**
	 * Constructor for a randomness pair taking in a witness and random value
	 * 
	 * @param rComm
	 * @param r
	 * @throws BallotAuditCommitException 
	 */
	public RandomnessPair(String rComm, String r) throws BallotAuditCommitException {

		if (rComm == null || r == null) {
			logger.error("Unable to create RandomnessPair object: {}, {}",
					rComm, r);
			logger.error("Randomness values must be 256 bits or larger (32 bytes) or 64 hex characters");
			throw new BallotAuditCommitException("Randomness values must be 256 bits or larger (32 bytes) or 64 hex characters");
		}
		if (rComm.length() < 64 || r.length() < 64) {
			logger.error("Unable to create RandomnessPair object: {}, {}",
					rComm, r);
			logger.error("Randomness values must be 256 bits or larger (32 bytes) or 64 hex characters");
			throw new BallotAuditCommitException("Randomness values must be 256 bits or larger (32 bytes) or 64 hex characters");
		}
		
		logger.debug("Creating a new RandomnessPair object: {}, {}", rComm,
				r);
		this.witness = rComm;
		this.randomnessValue = r;
	}

	/**
	 * Overridden equals method for a randomness pair - compares both the
	 * witness and random value
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RandomnessPair)) {
			return false;
		}
		RandomnessPair other = (RandomnessPair) obj;
		if (this.randomnessValue == null) {
			if (other.randomnessValue != null) {
				return false;
			}
		} else if (!this.randomnessValue.equals(other.randomnessValue)) {
			return false;
		}
		if (this.witness == null) {
			if (other.witness != null) {
				return false;
			}
		} else if (!this.witness.equals(other.witness)) {
			return false;
		}
		return true;
	}

	/**
	 * Getter for the randomness value
	 * 
	 * @return the randomness value
	 */
	public final String getRandomnessValue() {
		return this.randomnessValue;
	}

	/**
	 * Getter for the witness value
	 * 
	 * @return the witness value
	 */
	public final String getWitness() {
		return this.witness;
	}

	/**
	 * Overridden hash code for a randomness pair
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.randomnessValue == null) ? 0 : this.randomnessValue.hashCode());
		result = prime * result + ((this.witness == null) ? 0 : this.witness.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "RandomnessPair [witness=" + this.witness + ", randomnessValue=" + this.randomnessValue + "]";
	}
}
