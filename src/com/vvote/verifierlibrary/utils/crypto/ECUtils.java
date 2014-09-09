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
package com.vvote.verifierlibrary.utils.crypto;

import java.math.BigInteger;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import com.vvote.CryptoConstants;
import com.vvote.ec.ElGamalECPoint;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides Elliptic Curve crypto operations. Provides a number of utility
 * methods
 * 
 * @author James Rumble
 * 
 */
public class ECUtils {

	/**
	 * Holds a reference to the EC parameter spec as defined by the curve name
	 */
	private static ECParameterSpec params = ECNamedCurveTable.getParameterSpec(CryptoConstants.EC.CURVE_NAME);

	/**
	 * Change the curve being used for elliptic curve operations
	 * 
	 * @param curveName
	 */
	public static void changeCurve(String curveName) {
		params = ECNamedCurveTable.getParameterSpec(curveName);
	}

	/**
	 * Constructs a single ECPoint from the given JSON Object
	 * 
	 * @param json
	 * @return the ECPoint from json
	 * @throws JSONException
	 */
	public static ECPoint constructECPointFromJSON(JSONObject json) throws JSONException {
		return params.getCurve().createPoint(new BigInteger(json.getString(CryptoConstants.EC.X), 16), new BigInteger(json.getString(CryptoConstants.EC.Y), 16));
	}

	/**
	 * Constructs an ElGamalECPoint from the required JSON object
	 * 
	 * @param json
	 * @return the ElGamalECPoint from json
	 * @throws JSONException
	 */
	public static ElGamalECPoint constructElGamalECPointFromJSON(JSONObject json) throws JSONException {
		return new ElGamalECPoint(constructECPointFromJSON(json.getJSONObject(CryptoConstants.EC.MYR)), constructECPointFromJSON(json.getJSONObject(CryptoConstants.EC.GR)));
	}

	/**
	 * Converts a cipher which contains two ECPoints into a JSON object. Both
	 * points are converted using constructJSONFromECPoint
	 * 
	 * @param point
	 * @return the JSONObject
	 * @throws JSONException
	 */
	public static JSONObject constructJSONFromCipher(ElGamalECPoint point) throws JSONException {
		JSONObject cipher = new JSONObject();
		cipher.put(CryptoConstants.EC.GR, ECUtils.constructJSONFromECPoint(point.getGr()));
		cipher.put(CryptoConstants.EC.MYR, ECUtils.constructJSONFromECPoint(point.getMyr()));

		return cipher;
	}

	/**
	 * Convers a single ECPoint into a JSONObject. THe coordinates are stored in
	 * Hex representations of their underlying BigInteger format
	 * 
	 * @param point
	 * @return the JSONObject
	 * @throws JSONException
	 */
	public static JSONObject constructJSONFromECPoint(ECPoint point) throws JSONException {
		JSONObject jpoint = new JSONObject();
		jpoint.put(CryptoConstants.EC.X, point.getXCoord().toBigInteger().toString(16));
		jpoint.put(CryptoConstants.EC.Y, point.getYCoord().toBigInteger().toString(16));

		return jpoint;
	}

	/**
	 * Performs an encryption on a plaintext in the form of an ECPoint using the
	 * given public key and randomness value
	 * 
	 * @param plaintext
	 * @param publicKey
	 * @param randomness
	 * @return resulting encrypted ElGamalECPoint
	 */
	public static ElGamalECPoint encrypt(ECPoint plaintext, ECPoint publicKey, BigInteger randomness) {
		// g^r maps to g.r
		ECPoint gr = params.getG().multiply(randomness);

		// m*y^r maps to m + y.r
		ECPoint myr = publicKey.multiply(randomness).add(plaintext);

		return new ElGamalECPoint(myr, gr);
	}

	/**
	 * Performs a re-encryption of a given ElGamalECPoint using the provided
	 * public key and randomness value
	 * 
	 * @param cipher
	 * @param publicKey
	 * @param randomness
	 * @return resulting reencrypted ElGamalECPoint
	 */
	public static ElGamalECPoint reencrypt(ElGamalECPoint cipher, ECPoint publicKey, BigInteger randomness) {
		// gr = existing gr + g.r
		ECPoint gr = cipher.getGr().add(params.getG().multiply(randomness));

		// myr = existing myr + y.r
		ECPoint myr = cipher.getMyr().add(publicKey.multiply(randomness));

		return new ElGamalECPoint(myr, gr);
	}

	/**
	 * Prevents the class being externally created
	 */
	private ECUtils() {
		return;
	}

	/**
	 * Utility method to get the G value of the underlying curve
	 * 
	 * @return ECPoint of G
	 */
	public static ECPoint getG() {
		return params.getG();
	}

	/**
	 * Utility method to get the Order Upper Bound (N) of the underlying curve
	 * 
	 * @return BigInteger of the order of the upper bound (N)
	 */
	public static BigInteger getOrderUpperBound() {
		return params.getN();
	}

	/**
	 * Get the underlying parameter spec for this instance of ECUtils
	 * 
	 * @return ECParameterSpec for the curve being used
	 */
	public static ECParameterSpec getParams() {
		return params;
	}
}
