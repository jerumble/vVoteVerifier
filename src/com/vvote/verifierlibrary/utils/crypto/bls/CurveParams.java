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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.DefaultCurveParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.BLSConstants;
import com.vvote.verifierlibrary.exceptions.BLSSignatureException;

/**
 * Singleton for providing access to parameters of the BLS Signatures
 * 
 * The main purpose of this class is to provide access to the pairing object and
 * the curve parameters. We create this as a singleton to save having to reload
 * the data each time an object needs access to the system parameters for BLS.
 * 
 * 
 * @author James Rumble
 * 
 */
public class CurveParams {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CurveParams.class);

	/**
	 * Single instance of CurveParams
	 */
	private static CurveParams instance = null;

	/**
	 * Contains the loaded curve parameters contained in the PARAMS_FILE
	 */
	private DefaultCurveParameters curveParams;

	/**
	 * Holds a reference to the pairing associated with the curve specified in
	 * the PARAMS_FILE
	 */
	private Pairing pairing;

	/**
	 * Private constructor used during initialisation of the singleton.
	 * 
	 * @throws BLSSignatureException
	 */
	private CurveParams() throws BLSSignatureException {
		logger.debug("Creating CurveParams instance from {}", BLSConstants.BLSParamsFile.PARAMS_FILE);

		try (InputStream paramsStream = new FileInputStream(BLSConstants.BLSParamsFile.PARAMS_FILE)) {
			this.curveParams = new DefaultCurveParameters().load(paramsStream);

			this.pairing = PairingFactory.getPairing(this.curveParams);

			logger.debug("Finished creating curveParams and pairing");
		} catch (FileNotFoundException e) {
			logger.error("Unable to create CurveParams", e);
			throw new BLSSignatureException("Unable to create CurveParams", e);
		} catch (IOException e) {
			logger.error("Unable to create CurveParams", e);
			throw new BLSSignatureException("Unable to create CurveParams", e);
		}
	}

	/**
	 * Gets the Pairing from the previously initialised curve parameters
	 * 
	 * @return Pairing associated with the default curve parameters
	 */
	public Pairing getPairing() {
		return this.pairing;
	}

	/**
	 * Gets the DefaultCurveParameters loaded from the default file
	 * 
	 * @return DefaultCurveParameters loaded from the file specified in the
	 *         constant PARAMS_FILE
	 */
	public DefaultCurveParameters getCurveParams() {
		return this.curveParams;
	}

	/**
	 * Gets the singleton instance of the CurveParams singleton that then
	 * provides access to the curve parameters and pairing objects.
	 * 
	 * @return singleton instance of CurveParams
	 * @throws BLSSignatureException
	 */
	public static final CurveParams getInstance() throws BLSSignatureException {
		if (instance == null) {
			instance = new CurveParams();
		}
		return instance;
	}
}
