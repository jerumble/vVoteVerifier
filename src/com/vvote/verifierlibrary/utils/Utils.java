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
package com.vvote.verifierlibrary.utils;

import java.io.File;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.datafiles.exceptions.MixDataException;
import com.vvote.datafiles.mix.RaceIdentifier;
import com.vvote.datafiles.mix.RaceIdentifierConstants;
import com.vvote.messages.typed.vote.RaceType;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a number of utility functions such as decoding, encoding data and
 * checking extensions
 * 
 * @author James Rumble
 * 
 */
public class Utils {

	/**
	 * provides loggging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Converts a byte array into base64 string format
	 * 
	 * @param bytes
	 * @return base64 string
	 */
	public static String byteToBase64String(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * Converts a byte array into hexadecimal string format
	 * 
	 * @param bytes
	 * @return hex string
	 */
	public static String byteToHexString(byte[] bytes) {
		return Hex.encodeHexString(bytes);
	}

	/**
	 * Decodes a string in base64 format into byte format
	 * 
	 * @param data
	 * @return byte data
	 */
	public static byte[] decodeBase64Data(String data) {
		return Base64.decodeBase64(data);
	}

	/**
	 * Decodes a string in hexadecimal format into byte format
	 * 
	 * @param data
	 * @return byte data
	 */
	public static byte[] decodeHexData(String data) {
		try {
			return Hex.decodeHex(data.toCharArray());
		} catch (DecoderException e) {
			logger.error("Unable to decode hex data from string: {}", data);
		}

		return null;
	}

	/**
	 * Gets the race identifier from a csv filename
	 * 
	 * @param filename
	 * @param hasRaceMap 
	 * @return RaceIdentifier
	 */
	public static RaceIdentifier getRaceIdentifierFromCSVFileName(String filename, boolean hasRaceMap) {

		if (IOUtils.checkExtension(FileType.CSV, filename)) {
			return getRaceIdentifierFromFileName(filename, hasRaceMap);
		}
		logger.error("Unable to get race identifier from filename: {}", filename);
		return null;
	}

	/**
	 * Gets the race identifier from a valid filename
	 * 
	 * @param filename
	 * @param hasRaceMap 
	 * @return RaceIdentifier
	 */
	public static RaceIdentifier getRaceIdentifierFromFileName(String filename, boolean hasRaceMap) {
		final String fileName;

		final File file = new File(filename);

		String raceId = null;
		RaceType raceType = null;
		String raceName = null;
		String district = null;

		if (file.getName().equals(filename)) {
			fileName = IOUtils.getFileNameWithoutExtension(filename);
		} else {
			fileName = IOUtils.getFileNameWithoutExtension(file.getName());
		}

		String[] filenameParts = fileName.split(RaceIdentifierConstants.SEPARATOR);

		if (filenameParts.length <= RaceIdentifierConstants.RACE_POSITION) {
			logger.error("Unable to get race identifier from filename: {}", filename);
			return null;
		}

		raceId = filenameParts[RaceIdentifierConstants.RACE_ID_POSITION];

		raceType = RaceType.fromString(filenameParts[RaceIdentifierConstants.RACE_POSITION]);

		if (raceType == null) {
			logger.error("Unable to get race identifier from filename: {}", filename);
			return null;
		}

		if (!hasRaceMap) {

			if (raceType.equals(RaceType.LA)) {
				if (filenameParts.length != 3) {
					logger.error("Unable to get race identifier from filename: {}", filename);
					return null;
				}

				String[] splitForDistrict = filenameParts[RaceIdentifierConstants.LA_DISTRICT_POSITION].split(RaceIdentifierConstants.SEPARATOR_POINT);

				if (splitForDistrict.length == RaceIdentifierConstants.LA_DISTRICT_SPLIT_LENGTH) {
					district = splitForDistrict[RaceIdentifierConstants.LA_DISTRICT_SPLIT_POSITION];
					raceName = district;
				}
			} else if (raceType.equals(RaceType.LC_ATL) || raceType.equals(RaceType.LC_BTL)) {
				if (filenameParts.length == 4) {
					String[] splitForRaceName = filenameParts[RaceIdentifierConstants.LC_RACE_NAME_POSITION].split(RaceIdentifierConstants.SEPARATOR_POINT);

					if (splitForRaceName.length == RaceIdentifierConstants.LC_RACE_NAME_SPLIT_LENGTH) {
						raceName = splitForRaceName[RaceIdentifierConstants.LC_RACE_NAME_SPLIT_POSITION];
					} else {
						logger.error("Unable to get race identifier from filename: {}", filename);
						return null;
					}

					district = filenameParts[RaceIdentifierConstants.LC_DISTRICT_POSITION];
				} else if (filenameParts.length == 3) {
					district = raceId.substring(raceId.lastIndexOf(RaceIdentifierConstants.RACE_ID_SEPARATOR) + 1);
				} else {
					logger.error("Unable to get race identifier from filename: {}", filename);
					return null;
				}
			}
		}
		try {
			return new RaceIdentifier(raceId, raceName, raceType, district);
		} catch (MixDataException e) {
			logger.error("Unable to get race identifier from filename: {}", filename);
			return null;
		}
	}
}
