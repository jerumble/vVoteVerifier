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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.DistrictConfigurationException;
import com.vvote.datafiles.fields.DataFileFields;
import com.vvote.thirdparty.json.orgjson.JSONException;
import com.vvote.thirdparty.json.orgjson.JSONObject;

/**
 * Provides a representation of a single district configuration including the
 * number of candidates running in each race and its name
 * 
 * @author James Rumble
 * 
 */
public class DistrictConfig {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DistrictConfig.class);

	/**
	 * Name of the district
	 */
	private final String districtName;

	/**
	 * Number of candidates in the la race
	 */
	private final int laSize;

	/**
	 * Number of candidates in the lc atl race
	 */
	private final int lcATLSize;

	/**
	 * Number of candidates in the lc btl race
	 */
	private final int lcBTLSize;

	/**
	 * Total number of candidates
	 */
	private final int totalCandidates;

	/**
	 * Constructor for a district configuration object
	 * 
	 * @param districtName
	 * @param json
	 * @throws DistrictConfigurationException
	 */
	public DistrictConfig(String districtName, JSONObject json) throws DistrictConfigurationException {

		this.districtName = districtName;

		// check that the JSON message is not null
		if (json != null) {

			try {
				// get and set the submission id
				if (json.has(DataFileFields.DistrictConfig.LA)) {
					this.laSize = json.getInt(DataFileFields.DistrictConfig.LA);
				} else {
					logger.error("The size of the la race must be provided for a DistrictConfig");
					throw new DistrictConfigurationException("The size of the la race must be provided for a DistrictConfig");
				}

				// get and set the submission id
				if (json.has(DataFileFields.DistrictConfig.LC_ATL)) {
					this.lcATLSize = json.getInt(DataFileFields.DistrictConfig.LC_ATL);
				} else {
					logger.error("The size of the lc atl race must be provided for a DistrictConfig");
					throw new DistrictConfigurationException("The size of the lc atl race must be provided for a DistrictConfig");
				}

				// get and set the submission id
				if (json.has(DataFileFields.DistrictConfig.LC_BTL)) {
					this.lcBTLSize = json.getInt(DataFileFields.DistrictConfig.LC_BTL);
				} else {
					logger.error("The size of the lc btl race must be provided for a DistrictConfig");
					throw new DistrictConfigurationException("The size of the lc btl race must be provided for a DistrictConfig");
				}

				this.totalCandidates = this.laSize + this.lcATLSize + this.lcBTLSize;

			} catch (JSONException e) {
				logger.error("Unable to create a DistrictConfig. Error: {}", e);
				throw new DistrictConfigurationException("Unable to create a DistrictConfig.", e);
			}
		} else {
			logger.error("A DistrictConfig object must be a valid JSON message");
			throw new DistrictConfigurationException("A DistrictConfig object must be a valid JSON message");
		}
	}

	/**
	 * Getter for the name of the district
	 * 
	 * @return districtName
	 */
	public String getDistrictName() {
		return this.districtName;
	}

	/**
	 * Getter for the number of candidates running in the la race
	 * 
	 * @return laSize
	 */
	public int getLaSize() {
		return this.laSize;
	}

	/**
	 * Getter for the number of candidates running in the lc atl race
	 * 
	 * @return laSize
	 */
	public int getLcATLSize() {
		return this.lcATLSize;
	}

	/**
	 * Getter for the number of candidates running in the lc btl race
	 * 
	 * @return laSize
	 */
	public int getLcBTLSize() {
		return this.lcBTLSize;
	}

	/**
	 * Getter for the total number of candidates running in the district
	 * 
	 * @return totalCandidates
	 */
	public int getNumberOfCandidates() {
		return this.totalCandidates;
	}
}
