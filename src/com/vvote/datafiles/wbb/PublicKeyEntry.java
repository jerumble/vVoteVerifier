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
 * Provides storage for a public key entry
 * 
 * @author James Rumble
 * 
 */
public class PublicKeyEntry {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(PublicKeyEntry.class);

	/**
	 * The public key entry in json form
	 */
	private final JSONObject jsonEntry;

	/**
	 * Element to hold the Joint Public Key value
	 */
	private final Element publicKeyElem;

	/**
	 * Element containing the generator g for the public key values
	 */
	private final Element gElem;

	/**
	 * The public key
	 */
	private final String publicKey;

	/**
	 * The generator value
	 */
	private final String g;

	/**
	 * Constructor for a <code>PublicKeyEntry</code>
	 * 
	 * @param entry
	 * @throws CertException
	 */
	public PublicKeyEntry(JSONObject entry) throws CertException {
		// set the json entry
		this.jsonEntry = entry;

		// check that the JSON entry is not null
		if (this.getJsonEntry() != null) {

			try {
				// get and set the public key
				if (this.getJsonEntry().has(CertsConstants.PublicKeyEntry.PUBLIC_KEY)) {
					this.publicKey = this.getJsonEntry().getString(CertsConstants.PublicKeyEntry.PUBLIC_KEY);
				} else {
					logger.error("public key must be provided for a public key entry");
					throw new CertException("public key must be provided for a public key entry");
				}

				// get and set the generator
				if (this.getJsonEntry().has(CertsConstants.PublicKeyEntry.G)) {
					this.g = this.getJsonEntry().getString(CertsConstants.PublicKeyEntry.G);
				} else {
					logger.error("Generator value must be provided for a public key entry");
					throw new CertException("Generator value must be provided for a public key entry");
				}

				// Create a pairing object which will be used for loading the key
				Pairing pairing = CurveParams.getInstance().getPairing();

				// Construct new element
				this.publicKeyElem = pairing.getG2().newElement();
				// Set the element value from the bytes decoded as base 64
				this.publicKeyElem.setFromBytes(Utils.decodeBase64Data(this.publicKey));

				// Construct new element
				this.gElem = pairing.getG2().newElement();
				// Set the element value from the bytes decoded as base 64
				this.gElem.setFromBytes(Utils.decodeBase64Data(this.g));

			} catch (JSONException e) {
				logger.error("Unable to create a PublicKeyEntry. Error: {}", e);
				throw new CertException("Unable to create a PublicKeyEntry.", e);
			} catch (BLSSignatureException e) {
				logger.error("Unable to create a PublicKeyEntry. Error: {}", e);
				throw new CertException("Unable to create a PublicKeyEntry.", e);
			}
		} else {
			logger.error("A PublicKeyEntry object must be a valid JSON entry");
			throw new CertException("A PublicKeyEntry object must be a valid JSON entry");
		}
	}

	/**
	 * Getter for the public key element
	 * 
	 * @return publicKeyElem
	 */
	public Element getPublicKeyElem() {
		return this.publicKeyElem;
	}

	/**
	 * Getter for the generator element
	 * 
	 * @return gElem
	 */
	public Element getgElem() {
		return this.gElem;
	}

	/**
	 * Getter for the json entry
	 * 
	 * @return jsonEntry
	 */
	public JSONObject getJsonEntry() {
		return this.jsonEntry;
	}

	/**
	 * Getter for the public key
	 * 
	 * @return publicKey
	 */
	public String getPublicKey() {
		return this.publicKey;
	}

	/**
	 * Getter for the generator
	 * 
	 * @return g
	 */
	public String getG() {
		return this.g;
	}

	@Override
	public String toString() {
		return "PublicKeyEntry [jsonEntry=" + this.jsonEntry + ", publicKey=" + this.publicKey + ", g=" + this.g + "]";
	}
}
