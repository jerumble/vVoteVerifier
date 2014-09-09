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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.DistrictConfig;
import com.vvote.datafiles.commits.gencommit.CommittedBallot;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.messages.typed.vote.PODMessage;
import com.vvote.messages.typed.vote.RaceType;
import com.vvote.messages.typed.vote.Reduction;
import com.vvote.verifier.exceptions.VotePackingException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;

/**
 * Holds a reduced ballot which is initialised from a committed ballot
 * 
 * @author James Rumble
 * 
 */
public class ReducedBallot {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ReducedBallot.class);

	/**
	 * Holds the serial number for a specific committed ballot
	 */
	private final String serialNo;

	/**
	 * Holds an array of ElGamalECPoint objects representing the encrypted
	 * candidate ids
	 */
	private final List<ElGamalECPoint> reducedCiphers;

	/**
	 * Is set during construction and verification of the ballot reductions
	 */
	private boolean isValid = true;

	/**
	 * name of the district for the reduced ballot
	 */
	private final String district;

	/**
	 * Constructor for a <code>ReducedBallot</code>
	 * 
	 * @param committedBallot
	 * @param podMessage
	 * @param baseEncryptedIds
	 * @param publicKey
	 * @param genericBallotSizes
	 * @param districtConfig
	 * @throws VotePackingException
	 */
	public ReducedBallot(CommittedBallot committedBallot, PODMessage podMessage, List<ElGamalECPoint> baseEncryptedIds, ECPoint publicKey, Map<RaceType, Integer> genericBallotSizes,
			DistrictConfig districtConfig) throws VotePackingException {

		if (committedBallot != null) {

			logger.debug("Creating a new Reduced Ballot object from committedBallot: {}", committedBallot.getSerialNo());

			if (committedBallot.getSerialNo().equals(podMessage.getSerialNo())) {

				this.serialNo = committedBallot.getSerialNo();
				// initialise from the list of ciphers for the committed ballot
				this.reducedCiphers = new ArrayList<ElGamalECPoint>(committedBallot.getCiphers());

				if (podMessage.getBallotReductions().hasReductions()) {
					for (Reduction reduction : podMessage.getBallotReductions().getLaRaceReduction().getReductions()) {
						if (!this.reduceBallot(RaceType.LA, reduction.getIndex(), reduction.getCandidateIndex(), reduction.getRandomness(), baseEncryptedIds, publicKey, genericBallotSizes,
								districtConfig, committedBallot.getCiphers())) {
							this.isValid = false;
						}
					}
					for (Reduction reduction : podMessage.getBallotReductions().getLcATLRaceReduction().getReductions()) {
						if (!this.reduceBallot(RaceType.LC_ATL, reduction.getIndex(), reduction.getCandidateIndex(), reduction.getRandomness(), baseEncryptedIds, publicKey, genericBallotSizes,
								districtConfig, committedBallot.getCiphers())) {
							this.isValid = false;
						}
					}
					for (Reduction reduction : podMessage.getBallotReductions().getLcBTLRaceReduction().getReductions()) {
						if (!this.reduceBallot(RaceType.LC_BTL, reduction.getIndex(), reduction.getCandidateIndex(), reduction.getRandomness(), baseEncryptedIds, publicKey, genericBallotSizes,
								districtConfig, committedBallot.getCiphers())) {
							this.isValid = false;
						}
					}
				}

				this.district = districtConfig.getDistrictName();
				
				if (!this.isValid) {
					logger.error("Ballot reductions are not valid for ballot with serial number: {}", committedBallot.getSerialNo());
				}

			} else {
				logger.error("A reduced ballot must be provided matching podMessage and committed ballots");
				throw new VotePackingException("A reduced ballot must be provided matching podMessage and committed ballots");
			}
		} else {
			logger.error("A reduced ballot must be provided initially with a valid committed ballot");
			throw new VotePackingException("A reduced ballot must be provided initially with a valid committed ballot");
		}
	}

	/**
	 * Getter for the serial number of the reduced ballot
	 * 
	 * @return serialNo
	 */
	public String getSerialNo() {
		return this.serialNo;
	}

	/**
	 * Getter for the list of reduced ciphers
	 * 
	 * @return reducedCiphers
	 */
	public List<ElGamalECPoint> getReducedCiphers() {
		return Collections.unmodifiableList(this.reducedCiphers);
	}

	/**
	 * Reduces the specified ballot
	 * 
	 * @param raceType
	 * @param index
	 * @param candidateIndex
	 * @param randomness
	 * @param baseEncryptedIds
	 * @param publicKey
	 * @param genericBallotSizes
	 * @param districtConfig
	 * @param ciphers
	 * @return true if the reduction was carried out successfully
	 */
	private boolean reduceBallot(RaceType raceType, int index, int candidateIndex, String randomness, List<ElGamalECPoint> baseEncryptedIds, ECPoint publicKey,
			Map<RaceType, Integer> genericBallotSizes, DistrictConfig districtConfig, List<ElGamalECPoint> ciphers) {

		// get the race index
		int indexWithOffset = 0;
		int candidateIndexWithOffset = 0;
		if (raceType == RaceType.LA) {

			if (candidateIndex >= districtConfig.getLaSize() && candidateIndex < genericBallotSizes.get(RaceType.LA)) {
				indexWithOffset = index;
				candidateIndexWithOffset = candidateIndex;
			} else {
				return false;
			}

		} else if (raceType == RaceType.LC_ATL) {

			if (candidateIndex >= districtConfig.getLcATLSize() && candidateIndex < genericBallotSizes.get(RaceType.LC_ATL)) {
				indexWithOffset = index + genericBallotSizes.get(RaceType.LA);
				candidateIndexWithOffset = candidateIndex + genericBallotSizes.get(RaceType.LA);
			} else {
				return false;
			}

		} else if (raceType == RaceType.LC_BTL) {

			if (candidateIndex >= districtConfig.getLcBTLSize() && candidateIndex < genericBallotSizes.get(RaceType.LC_BTL)) {
				indexWithOffset = index + genericBallotSizes.get(RaceType.LA) + genericBallotSizes.get(RaceType.LC_ATL);
				candidateIndexWithOffset = candidateIndex + genericBallotSizes.get(RaceType.LA) + genericBallotSizes.get(RaceType.LC_ATL);
			} else {
				return false;
			}
		}

		ElGamalECPoint toRemove = ciphers.get(indexWithOffset);

		this.reducedCiphers.remove(toRemove);

		ElGamalECPoint baseEncryptedCandidateId = baseEncryptedIds.get(candidateIndexWithOffset);

		ElGamalECPoint reencryptedCandidateId = ECUtils.reencrypt(baseEncryptedCandidateId, publicKey, new BigInteger(1, Utils.decodeBase64Data(randomness)));

		if (reencryptedCandidateId.equals(toRemove)) {
			return true;
		}

		return false;
	}

	/**
	 * Getter for the cipher at the specific index
	 * 
	 * @param index
	 * @return the cipher at the specific index
	 */
	public ElGamalECPoint getReducedCipher(int index) {
		return this.reducedCiphers.get(index);
	}

	/**
	 * Getter for the flag determining whether the current reduced ballot is
	 * valid
	 * 
	 * @return isValid
	 */
	public boolean isValid() {
		return this.isValid;
	}

	/**
	 * Getter for the name of the district
	 * 
	 * @return district
	 */
	public String getDistrict() {
		return this.district;
	}
}
