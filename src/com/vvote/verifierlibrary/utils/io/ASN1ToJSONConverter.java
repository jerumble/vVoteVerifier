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
package com.vvote.verifierlibrary.utils.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.ec.ElGamalECPoint;
import com.vvote.thirdparty.json.orgjson.JSONArray;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.verifierlibrary.exceptions.ASN1Exception;
import com.vvote.verifierlibrary.exceptions.JSONIOException;
import com.vvote.verifierlibrary.utils.crypto.ECUtils;

/**
 * Provides utility methods for converting asn.1 files to json files for easier
 * analysis and debugging
 * 
 * Modified from sample code provided by Chris Culnane.
 * 
 * @author James Rumble
 * 
 */
public class ASN1ToJSONConverter {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(ASN1ToJSONConverter.class);

	/**
	 * Utility method used for converting asn.1 files to json
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param fileType
	 * @return whether the conversion was successful
	 * @throws ASN1Exception
	 */
	public static boolean asn1ToJSON(String inputFile, String outputFile, FileType fileType) throws ASN1Exception {

		logger.debug("Reading in asn.1 file: {}", inputFile);

		JSONArray out = new JSONArray();

		// create the ASN1 input stream
		try (ASN1InputStream ais = new ASN1InputStream(new FileInputStream(inputFile))) {
			ASN1Primitive obj = null;

			// loop over each ASN1 primitive
			while ((obj = ais.readObject()) != null) {
				switch (fileType) {
				// convert plaintexts
				case MIX_OUTPUT:
					convertASN1ECPoints(obj, out);
					break;
				// convert ciphers
				case MIX_INPUT:
					convertASN1Ciphers(obj, out);
					break;
				default:
					return false;
				}
			}

			// write the output JSON to file
			IOUtils.writeJSONToFile(out, outputFile);

		} catch (FileNotFoundException e) {
			logger.error("Cannot find file", e);
			throw new ASN1Exception("Cannot find file", e);
		} catch (IOException e) {
			logger.error("Cannot read file", e);
			throw new ASN1Exception("Cannot read file", e);
		} catch (JSONException e) {
			logger.error("There was a problem during conversion", e);
			throw new ASN1Exception("There was a problem during conversion", e);
		} catch (JSONIOException e) {
			logger.error("Unable to write JSON to file", e);
			throw new ASN1Exception("Unable to write JSON to file", e);
		}

		return true;
	}

	/**
	 * Converts an ASN.1 file containing ciphers to JSON format
	 * 
	 * @param obj
	 * @param parent
	 * @throws JSONException
	 */
	private static void convertASN1Ciphers(Object obj, JSONArray parent) throws JSONException {
		// ensure obj is an ASN1Sequence object before the cast
		if (obj instanceof ASN1Sequence) {
			ASN1Sequence seq = (ASN1Sequence) obj;

			// check whether the sequence is a re-encrypted candidate ciphertext
			if (seq.size() == 2 && seq.getObjectAt(0) instanceof DEROctetString) {
				ECPoint gr = ECUtils.getParams().getCurve().decodePoint(((DEROctetString) seq.getObjectAt(0)).getOctets());
				ECPoint myr = ECUtils.getParams().getCurve().decodePoint(((DEROctetString) seq.getObjectAt(1)).getOctets());
				ElGamalECPoint cipher = new ElGamalECPoint(myr, gr);
				parent.put(ECUtils.constructJSONFromCipher(cipher));

				// if not it is a sequence of ciphers with each needing
				// conversion
			} else {
				Enumeration<?> objs = seq.getObjects();
				JSONArray seqArr = new JSONArray();
				while (objs.hasMoreElements()) {
					convertASN1Ciphers(objs.nextElement(), seqArr);
				}
				parent.put(seqArr);
			}
		} else {
			System.err.println("Unknown ASN1 Type:" + obj.getClass().getName());
		}

	}

	/**
	 * Converts an ASN.1 file containing ECPoint's to JSON format
	 * 
	 * @param obj
	 * @param parent
	 * @throws JSONException
	 */
	private static void convertASN1ECPoints(Object obj, JSONArray parent) throws JSONException {
		// check whether obj is an ASN1Sequence object. If its a sequence need
		// to convert each element
		if (obj instanceof ASN1Sequence) {
			ASN1Sequence seq = (ASN1Sequence) obj;
			Enumeration<?> objs = seq.getObjects();
			JSONArray seqArr = new JSONArray();
			// as it is a sequence we need to convert each element of the
			// sequence
			while (objs.hasMoreElements()) {
				convertASN1ECPoints(objs.nextElement(), seqArr);
			}
			parent.put(seqArr);

			// else we can convert directly the DEROctet
		} else if (obj instanceof DEROctetString) {
			ECPoint point = ECUtils.getParams().getCurve().decodePoint(((DEROctetString) obj).getOctets());
			parent.put(ECUtils.constructJSONFromECPoint(point));
		} else {
			System.err.println("Unknown ASN1 Type:" + obj.getClass().getName());
		}

	}
}
