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
package com.vvote.datafiles.wbb;

import org.bouncycastle.cert.CertException;

import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides representation for a peer cert
 * 
 * @author James Rumble
 * 
 */
public class PeerCert extends CertEntry {

	/**
	 * The public key entry for a peer cert
	 */
	private final PeerPublicKeyEntry publicKeyEntry;

	/**
	 * Constructor for a <code>PeerCert</code>
	 * 
	 * @param identifier
	 * @param cert
	 * @throws CertException
	 */
	public PeerCert(String identifier, JSONObject cert) throws CertException {
		super(identifier, cert);

		this.publicKeyEntry = new PeerPublicKeyEntry(this.getCert());
	}

	/**
	 * Getter for the public key entry
	 * 
	 * @return publicKeyEntry
	 */
	public PeerPublicKeyEntry getPublicKeyEntry() {
		return this.publicKeyEntry;
	}

	@Override
	public String toString() {
		return "PeerCert [publicKeyEntry=" + this.publicKeyEntry + "]";
	}
}
