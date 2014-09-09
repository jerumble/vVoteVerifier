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

import org.bouncycastle.math.ec.ECPoint;

/**
 * Provides a representation of an indexed <code>ElGamalECPoint</code> which
 * allows to check that the re-encryption and sorting produces the correct
 * permutation. When first created we give the re-encrypted base candidate ids
 * along with the initial index of that base candidate id. The indexed
 * <code>ElGamalECPoint</code> objects are then sorted. The index is then used
 * to produce a permutation string.
 * 
 * @author James Rumble
 * 
 */
public class IndexedElGamalECPoint extends ElGamalECPoint {

	/**
	 * Holds the initial candidate index
	 */
	private final int index;

	/**
	 * Constructor for a new <code>IndexedElGamalECPoint</code>. The
	 * <code>IndexedElGamalECPoint</code> will be a re-encrypted base candidate
	 * id and the index will be the initial candidate index before the ciphers
	 * are sorted.
	 * 
	 * @param myr
	 * @param gr
	 * @param index
	 * 
	 */
	public IndexedElGamalECPoint(ECPoint myr, ECPoint gr, int index) {
		super(myr, gr);
		this.index = index;
	}

	/**
	 * Constructor for a new <code>IndexedElGamalECPoint</code> object
	 * 
	 * @param point
	 * @param index
	 */
	public IndexedElGamalECPoint(ElGamalECPoint point, int index) {
		super(point.getMyr(), point.getGr());
		this.index = index;
	}

	/**
	 * Getter for the original candidate index for this point
	 * 
	 * @return index
	 */
	public final int getIndex() {
		return this.index;
	}
}
