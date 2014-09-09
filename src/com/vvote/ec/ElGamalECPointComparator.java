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
import java.util.Comparator;

/**
 * Provides a means of comparing two ElGamalECPoint objects together and
 * determining how they should be compared against one another.
 * 
 * @author James Rumble
 * 
 */
public class ElGamalECPointComparator implements Comparator<ElGamalECPoint> {

	/**
	 * Provides an implementation of the compare method
	 */
	@Override
	public int compare(ElGamalECPoint p1, ElGamalECPoint p2) {
		BigInteger gr1 = new BigInteger(1, p1.getGr().getEncoded());
		BigInteger myr1 = new BigInteger(1, p1.getMyr().getEncoded());
		BigInteger gr2 = new BigInteger(1, p2.getGr().getEncoded());
		BigInteger myr2 = new BigInteger(1, p2.getMyr().getEncoded());
		
		if (gr1.equals(gr2)) {
			return myr1.compareTo(myr2);
		}
		return gr1.compareTo(gr2);
	}

}
