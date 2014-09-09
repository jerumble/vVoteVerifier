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
package com.vvote.datafiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.DistrictConfigurationException;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation for the district configuration file which includes
 * a number of configurations for different districts
 * 
 * @author James Rumble
 * 
 */
public class DistrictConfigurationFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DistrictConfigurationFile.class);

	/**
	 * Instance variable which allows this class to be a singleton
	 */
	private static DistrictConfigurationFile instance = null;

	/**
	 * Getter for the single instance of the
	 * <code>DistrictConfigurationFile</code>
	 * 
	 * @param json
	 * 
	 * @return <code>DistrictConfigurationFile</code> object
	 * @throws DistrictConfigurationException
	 */
	public static DistrictConfigurationFile getInstance(JSONObject json) throws DistrictConfigurationException {
		if (instance == null) {
			instance = new DistrictConfigurationFile(json);
		}
		return instance;
	}

	/**
	 * A map of district name to district config
	 */
	private final Map<String, DistrictConfig> districts;

	/**
	 * Constructor for the district configuration file store
	 * 
	 * @param json
	 * @throws DistrictConfigurationException
	 */
	private DistrictConfigurationFile(JSONObject json) throws DistrictConfigurationException {

		// check that the JSON message is not null
		if (json != null) {

			this.districts = new HashMap<String, DistrictConfig>();

			Iterator<?> districtIterator = json.keys();

			String districtName = null;

			try {

				while (districtIterator.hasNext()) {
					districtName = (String) districtIterator.next();
					this.districts.put(districtName, new DistrictConfig(districtName, json.getJSONObject(districtName)));
				}

			} catch (JSONException e) {
				logger.error("Unable to create a DistrictConfigurationFile. Error: {}", e);
				throw new DistrictConfigurationException("Unable to create a DistrictConfigurationFile.", e);
			}

		} else {
			throw new NullPointerException("A DistrictConfigurationFile object must be a valid JSON message");
		}
	}

	/**
	 * Constructor for a BallotGenerationConfig object from a string spec
	 * 
	 * @param config
	 *            The configuration in string format
	 * @throws JSONException
	 * @throws DistrictConfigurationException
	 */
	public DistrictConfigurationFile(String config) throws DistrictConfigurationException, JSONException {
		this(new JSONObject(config));
	}
	
	/**
	 * Getter for the list of district configurations
	 * @return districts
	 */
	public Map<String, DistrictConfig> getDistricts() {
		return Collections.unmodifiableMap(this.districts);
	}
}
