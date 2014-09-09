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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.CertsConstants;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Represents an abstract certificate entry
 * 
 * @author James Rumble
 * 
 */
public abstract class CertEntry {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CertEntry.class);

	/**
	 * The identifier for a cert entry
	 */
	private final String id;

	/**
	 * The certificate
	 */
	private final JSONObject cert;

	/**
	 * Constructor for a <code>CertEntry</code>
	 * 
	 * @param identifier
	 * @param cert
	 * @throws CertException
	 */
	public CertEntry(String identifier, JSONObject cert) throws CertException {
		if (identifier != null) {
			this.id = identifier;
		} else {
			logger.error("A cert entry must be provided with a valid identifier");
			throw new CertException("A cert entry must be provided with a valid identifier");
		}

		if (cert != null) {
			if (cert.has(CertsConstants.PUBLIC_KEY_ENTRY)) {
				try {
					this.cert = cert.getJSONObject(CertsConstants.PUBLIC_KEY_ENTRY);
				} catch (JSONException e) {
					logger.error("A cert entry must be provided with a valid certificate");
					throw new CertException("A cert entry must be provided with a valid certificate");
				}
			} else {
				logger.error("A cert entry must be provided with a valid certificate");
				throw new CertException("A cert entry must be provided with a valid certificate");
			}
		} else {
			logger.error("A cert entry must be provided with a valid certificate");
			throw new CertException("A cert entry must be provided with a valid certificate");
		}
	}

	/**
	 * Returns whether the cert entry is a wbb cert or a peer
	 * 
	 * @return whether true if the cert entry is a peer
	 */
	public boolean isPeer() {
		return !this.getId().equals(CertsConstants.WBB_CERT);
	}

	/**
	 * Getter for the identifier of a cert entry
	 * 
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Getter for the json cert
	 * 
	 * @return cert
	 */
	public JSONObject getCert() {
		return this.cert;
	}

	@Override
	public String toString() {
		return "CertEntry [id=" + this.id + ", cert=" + this.cert + "]";
	}
}
