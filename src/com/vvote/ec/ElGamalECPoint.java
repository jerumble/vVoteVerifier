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
package com.vvote.ec;

import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;

/**
 * Provides a representation for an ElGamal Elliptic Curve point to be used to
 * store candidate id's and perform encryption of the points.
 * 
 * @author James Rumble
 * 
 */
public class ElGamalECPoint {

	/**
	 * Stores the Elliptic curve point representing m * y^r
	 */
	private ECPoint myr;

	/**
	 * Stores the Elliptic curve point representing g^r
	 */
	private ECPoint gr;

	/**
	 * Constructs an ElGamal Elliptic Curve point from two ECPoint objects
	 * representing m * y^r and g^r.
	 * 
	 * @param myr
	 * @param gr
	 */
	public ElGamalECPoint(ECPoint myr, ECPoint gr) {
		this.myr = myr;
		this.gr = gr;
	}

	/**
	 * Carries out a multiplication of the current point by mul
	 * 
	 * @param mul
	 */
	public void multiply(BigInteger mul) {
		this.myr = this.myr.multiply(mul);
		this.gr = this.gr.multiply(mul);
	}

	/**
	 * Provides a way to compare two ElGamalECPoint objects together. The
	 * comparison takes into account both ECPoint objects gr and myr. Basic
	 * checks are computed first and then both points are checked for equality.
	 * If both gr and myr points are then a positive result is returned.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ElGamalECPoint)) {
			return false;
		}
		ElGamalECPoint other = (ElGamalECPoint) obj;
		if (this.gr == null) {
			if (other.gr != null) {
				return false;
			}
		}
		if (this.myr == null) {
			if (other.myr != null) {
				return false;
			}
		}

		// perform a comparison using both points gr and myr.
		if (!(this.getGr().equals(other.getGr()) && this.getMyr().equals(other.getMyr()))) {
			return false;
		}

		return true;
	}

	/**
	 * Getter for the ECPoint representing g^r
	 * 
	 * @return gr
	 */
	public final ECPoint getGr() {
		return this.gr;
	}

	/**
	 * Getter for the ECPoint representing m * y^r
	 * 
	 * @return myr
	 */
	public final ECPoint getMyr() {
		return this.myr;
	}

	/**
	 * Provides a new representation of computing the hashcode for an
	 * ElGamalECPoint object which takes into account both values gr and myr and
	 * uses the prime 31
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.gr == null) ? 0 : this.gr.hashCode());
		result = prime * result + ((this.myr == null) ? 0 : this.myr.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ElGamalECPoint [myr=" + this.myr + ", gr=" + this.gr + "]";
	}

	/**
	 * Carries out an addition of the current point and the new cipher
	 * 
	 * @param cipher
	 */
	public void add(ElGamalECPoint cipher) {
		this.myr = this.myr.add(cipher.getMyr());
		this.gr = this.gr.add(cipher.getGr());
	}
}
