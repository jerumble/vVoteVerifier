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

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import com.vvote.datafiles.wbb.WBBCert;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;

/**
 * Utility class for working with bls signatures
 * 
 * @author James Rumble
 * 
 */
public class BLSUtils {

	/**
	 * Utility method for converting a byte array containing the byte contents
	 * of the signature Element, back into an Element of G1. This is useful when
	 * saving and loading signatures from files, where they are likely to have
	 * been stored as pure bytes.
	 * 
	 * @param signature
	 *            byte array containing a signature element
	 * @return Element on G1 of the byte array
	 * @throws BLSSignatureException
	 */
	public static Element getSignatureElement(byte[] signature) throws BLSSignatureException {
		Element elementSignature = CurveParams.getInstance().getPairing().getG1().newElement();
		elementSignature.setFromBytes(signature);
		return elementSignature;
	}

	/**
	 * Performs verification that the provided hash is equal to the data signed
	 * in the provided signature
	 * 
	 * @param hash
	 * @param signature
	 * @param cert
	 * @return true if the provided hash is equal to the data signed in the
	 *         provided signature
	 * @throws BLSSignatureException
	 */
	public static boolean verifyBLSSignature(byte[] hash, Element signature, WBBCert cert) throws BLSSignatureException {
		// Gets a reference to the curve pairing object
		Pairing pairing = CurveParams.getInstance().getPairing();

		// Map the hash onto an element in G1
		Element h = pairing.getG1().newElement().setFromHash(hash, 0, hash.length).getImmutable();

		// Create the signature pairing
		Element sigPairing = pairing.pairing(signature, cert.getPublicKeyEntry().getgElem());

		// Create the hash pairing
		Element hashPairing = pairing.pairing(h, cert.getPublicKeyEntry().getPublicKeyElem());

		// If the pairing are equal the signature is valid
		return sigPairing.isEqual(hashPairing);
	}
}
