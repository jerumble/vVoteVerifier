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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.cert.CertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.CertsConstants;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;
import com.vvote.verifierlibrary.utils.crypto.certs.CertFactory;

/**
 * Provides the representation for the file certs.bks
 * 
 * @author James Rumble
 * 
 */
public class CertificatesFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CertificatesFile.class);

	/**
	 * jks file path
	 */
	private final String jksFile;

	/**
	 * list of peer certs
	 */
	private List<PeerCert> peerCerts;

	/**
	 * wbb cert
	 */
	private WBBCert wbbCert;

	/**
	 * Json representing the file
	 */
	private final JSONObject json;

	/**
	 * Holds a mapping of peer to sequence number
	 */
	private final Map<String, Integer> peerMapping;

	/**
	 * Constructor for a certificates file
	 * 
	 * @param json
	 * @throws CertException
	 */
	private CertificatesFile(JSONObject json) throws CertException {
		// set the json entry
		this.json = json;

		// check that the JSON entry is not null
		if (this.getJson() != null) {

			try {
				if (this.getJson().has(CertsConstants.JKS_PATH)) {
					this.jksFile = this.getJson().getString(CertsConstants.JKS_PATH);
				} else {
					this.jksFile = null;
				}

				this.peerCerts = new ArrayList<PeerCert>();
				this.peerMapping = new HashMap<String, Integer>();

				JSONObject currentObject = null;

				CertEntry certEntry = null;

				for (String key : JSONObject.getNames(this.getJson())) {
					if (this.getJson().has(key)) {
						if (!key.equals(CertsConstants.JKS_PATH)) {
							currentObject = this.getJson().getJSONObject(key);

							certEntry = CertFactory.constructCertEntry(key, currentObject);

							if (certEntry.isPeer()) {
								if (certEntry instanceof PeerCert) {
									this.peerCerts.add((PeerCert) certEntry);

									this.peerMapping.put(this.getPeerString(certEntry.getId()), ((PeerCert) certEntry).getPublicKeyEntry().getSequenceNumber());
								}
							} else {
								if (certEntry instanceof WBBCert) {
									this.wbbCert = (WBBCert) certEntry;
								}
							}
						}
					}
				}

			} catch (JSONException e) {
				logger.error("Unable to create a CertificatesFile. Error: {}", e);
				throw new CertException("Unable to create a CertificatesFile.", e);
			}
		} else {
			logger.error("A CertificatesFile object must be a valid JSON entry");
			throw new CertException("A CertificatesFile object must be a valid JSON entry");
		}
	}

	/**
	 * Constructor for a certificates file
	 * 
	 * @param certs
	 * @throws CertException
	 * @throws JSONException
	 */
	public CertificatesFile(String certs) throws CertException, JSONException {
		this(new JSONObject(certs));
	}

	/**
	 * Gets the peer string from the identifier
	 * 
	 * @param id
	 * @return the peer string
	 * @throws CertException
	 */
	private String getPeerString(String id) throws CertException {
		if (id.contains(CertsConstants.SIGNING_SK2)) {
			return id.replace(CertsConstants.SIGNING_SK2, "");
		}
		logger.error("A peer string must contain: {}", CertsConstants.SIGNING_SK2);
		throw new CertException("A peer string must contain: " + CertsConstants.SIGNING_SK2);
	}

	/**
	 * Getter for the json
	 * 
	 * @return json
	 */
	public JSONObject getJson() {
		return this.json;
	}

	/**
	 * getter for the jks file
	 * 
	 * @return jksFile
	 */
	public String getJksFile() {
		return this.jksFile;
	}

	/**
	 * Getter for the wbb cert
	 * 
	 * @return wbbCert
	 */
	public WBBCert getWbbCert() {
		return this.wbbCert;
	}

	/**
	 * Getter for the list of peer certs
	 * 
	 * @return peerCerts
	 */
	public List<PeerCert> getPeerCerts() {
		return this.peerCerts;
	}

	/**
	 * Gets the sequence number for an existing peer
	 * 
	 * @param peer
	 * @return the sequence number for the peer
	 * @throws CertException
	 */
	public int getSequenceNumberForPeer(String peer) throws CertException {
		if (this.peerMapping.containsKey(peer)) {
			return this.peerMapping.get(peer);
		}
		logger.error("Peer does not exist: {}", peer);
		throw new CertException("Peer does not exist: " + peer);
	}

	@Override
	public String toString() {
		return "CertificatesFile [jksFile=" + this.jksFile + ", peerCerts=" + this.peerCerts + ", wbbCert=" + this.wbbCert + ", json=" + this.json + ", peerMapping=" + this.peerMapping + "]";
	}
}
