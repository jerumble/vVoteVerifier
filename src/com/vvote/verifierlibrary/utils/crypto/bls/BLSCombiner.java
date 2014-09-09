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
package com.vvote.verifierlibrary.utils.crypto.bls;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.thirdparty.ximix.util.LagrangeWeightCalculator;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;

/**
 * A utility class for combining signatures together into a single combined
 * signature
 * 
 * @author James Rumble
 * 
 */
public class BLSCombiner {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(BLSCombiner.class);

	/**
	 * Array of elements - each of them belonging to a peer
	 */
	private final Element[] signatures;

	/**
	 * threshold value - elements are required to successfully combine
	 */
	private final int threshold;

	/**
	 * The total number of nodes
	 */
	private final int numberOfNodes;

	/**
	 * The current number of signatures held
	 */
	private int numberOfSignatures = 0;

	/**
	 * Constructor for a <code>BLSCombiner</code>
	 * 
	 * @param numberOfNodes
	 * @param threshold
	 * @throws BLSSignatureException
	 */
	public BLSCombiner(int numberOfNodes, int threshold) throws BLSSignatureException {

		logger.debug("Creating new combiner with {} peers and a threshold of {}", numberOfNodes, threshold);
		if (threshold > numberOfNodes) {
			logger.error("Cannot create a BLS combiner a larger threshold than number of nodes");
			throw new BLSSignatureException("Cannot create a BLS combiner a larger threshold than number of nodes");
		}

		this.numberOfNodes = numberOfNodes;
		this.signatures = new Element[this.numberOfNodes];

		this.threshold = threshold;
	}

	/**
	 * Getter for the signatures list
	 * 
	 * @return signatures
	 */
	public Element[] getSignatures() {
		return this.signatures;
	}

	/**
	 * Getter for the threshold value
	 * 
	 * @return threshold
	 */
	public int getThreshold() {
		return this.threshold;
	}

	/**
	 * Adds a signature to the combiner without validating it
	 * 
	 * @param share
	 * @param id
	 * @throws BLSSignatureException
	 */
	public void addShare(byte[] share, int id) throws BLSSignatureException {
		if (id < this.numberOfNodes) {
			if (this.signatures[id] == null) {
				this.numberOfSignatures++;
			} else {
				logger.error("Cannot add a share to the BLS combiner at an id which already has a share: {}", id);
				throw new BLSSignatureException("Cannot add a share to the BLS combiner at an id which already has a share: " + id);
			}
			this.signatures[id] = BLSUtils.getSignatureElement(share);
		} else {
			logger.error("Cannot add a share to the BLS combiner at an id larger than the number of nodes: {}", id);
			throw new BLSSignatureException("Cannot add a share to the BLS combiner at an id larger than the number of nodes: " + id);
		}
	}

	/**
	 * Gets the combined signature by calculating the Lagrange Interpolation and
	 * then combining the shares
	 * 
	 * @return the combined signature
	 * @throws BLSSignatureException
	 */
	public Element combineSignatures() throws BLSSignatureException {
		if (this.numberOfSignatures >= this.threshold) {
			logger.debug("Combining {} signature shares together", this.numberOfSignatures);

			Element combinedSignature = null;

			// If we have a threshold of shares calculate the LagrangeWeight.
			// If a share is missing it will be null in the array, this is
			// important in the lagrange calculation.
			LagrangeWeightCalculator weightCalculator = new LagrangeWeightCalculator(this.numberOfNodes, CurveParams.getInstance().getPairing().getZr().getOrder());

			// Calculate the actual weights
			BigInteger[] weights = weightCalculator.computeWeights(this.signatures);

			// Step through the signatures array, applying the weight (if an
			// element exists) and combining the values
			for (int i = 0; i < this.signatures.length; i++) {
				if (combinedSignature == null) {
					// This is the first element we are going to add to the
					// combined value so we just set it to the signature share
					// multiplied by
					// the weight
					if (this.signatures[i] != null) {
						combinedSignature = this.signatures[i].mul(weights[i]);
					}
				} else {
					if (this.signatures[i] != null) {
						// If we have a signature, apply the weight and combine
						// with the previous shares
						combinedSignature = combinedSignature.duplicate().mul(this.signatures[i].mul(weights[i]));
					}
				}
			}
			return combinedSignature;
		}

		logger.error("Cannot combine signatures with only {} shares when the threshold was {}", this.numberOfSignatures, this.threshold);
		// We haven't received enough shares so throw an exception
		throw new BLSSignatureException("Cannot combine signatures with only: " + this.numberOfSignatures + " shares when the threshold was: " + this.threshold);
	}
}
