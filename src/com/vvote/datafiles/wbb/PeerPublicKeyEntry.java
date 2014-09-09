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

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import org.bouncycastle.cert.CertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.CertsConstants;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;
import com.vvote.verifierlibrary.utils.Utils;
import com.vvote.verifierlibrary.utils.crypto.bls.CurveParams;

/**
 * Provides storage for a peer public key entry
 * 
 * @author James Rumble
 * 
 */
public class PeerPublicKeyEntry extends PublicKeyEntry {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PeerPublicKeyEntry.class);

	/**
	 * partial public key
	 */
	private final String partialPublicKey;

	/**
	 * Sequence number
	 */
	private final int sequenceNumber;

	/**
	 * Partial public key element
	 */
	private final Element partialPublicKeyElem;

	/**
	 * Constructor for a <code>PublicKeyEntry</code>
	 * 
	 * @param entry
	 * @throws CertException
	 */
	public PeerPublicKeyEntry(JSONObject entry) throws CertException {
		super(entry);

		try {
			// get and set the partial public key
			if (this.getJsonEntry().has(CertsConstants.PeerPublicKeyEntry.PARTIAL_PUBLIC_KEY)) {
				this.partialPublicKey = this.getJsonEntry().getString(CertsConstants.PeerPublicKeyEntry.PARTIAL_PUBLIC_KEY);
			} else {
				logger.error("partial public key must be provided for a peer public key entry");
				throw new CertException("partial public key must be provided for a peer public key entry");
			}

			// get and set the sequence number
			if (this.getJsonEntry().has(CertsConstants.PeerPublicKeyEntry.SEQUENCE_NUMBER)) {
				this.sequenceNumber = this.getJsonEntry().getInt(CertsConstants.PeerPublicKeyEntry.SEQUENCE_NUMBER);
			} else {
				logger.error("sequence value must be provided for a public key entry");
				throw new CertException("sequence value must be provided for a public key entry");
			}

			// Create a pairing object which will be used for loading the key
			Pairing pairing = CurveParams.getInstance().getPairing();

			// Construct new element
			this.partialPublicKeyElem = pairing.getG2().newElement();
			// Set the element value from the bytes decoded as base 64
			this.partialPublicKeyElem.setFromBytes(Utils.decodeBase64Data(this.partialPublicKey));

		} catch (JSONException e) {
			logger.error("Unable to create a PeerPublicKeyEntry. Error: {}", e);
			throw new CertException("Unable to create a PeerPublicKeyEntry.", e);
		} catch (BLSSignatureException e) {
			logger.error("Unable to create a PeerPublicKeyEntry. Error: {}", e);
			throw new CertException("Unable to create a PeerPublicKeyEntry.", e);
		}
	}

	/**
	 * Getter for the partial public key
	 * 
	 * @return partialPublicKey
	 */
	public String getPartialPublicKey() {
		return this.partialPublicKey;
	}

	/**
	 * Getter for the sequence number
	 * 
	 * @return sequenceNumber
	 */
	public int getSequenceNumber() {
		return this.sequenceNumber;
	}

	@Override
	public String toString() {
		return "PeerPublicKeyEntry [partialPublicKey=" + this.partialPublicKey + ", sequenceNumber=" + this.sequenceNumber + "]";
	}
}
