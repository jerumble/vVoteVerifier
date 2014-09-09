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
package com.vvote.datafiles.mix;

/**
 * Constants used for extracting a <code>RaceIdentifier</code> from a filename
 * 
 * @author James Rumble
 * 
 */
public class RaceIdentifierConstants {

	/**
	 * Separator for the filename
	 */
	public static final String SEPARATOR = "_";
	
	/**
	 * Separator for the race id
	 */
	public static final String RACE_ID_SEPARATOR = "-";

	/**
	 * Separator point for the filename
	 */
	public static final String SEPARATOR_POINT = "\\.";

	/**
	 * Position of the race id
	 */
	public static final int RACE_ID_POSITION = 0;

	/**
	 * Position of the race type
	 */
	public static final int RACE_POSITION = 1;

	/**
	 * Position of the district in the la race identifier
	 */
	public static final int LA_DISTRICT_POSITION = 2;

	/**
	 * Position of the district in the la race after splitting the filename at
	 * LA_DISTRICT_POSITION
	 */
	public static final int LA_DISTRICT_SPLIT_POSITION = 1;

	/**
	 * Size of the element of the filename where the district can be found
	 */
	public static final int LA_DISTRICT_SPLIT_LENGTH = 2;

	/**
	 * Position of the race name in the lc race identifier
	 */
	public static final int LC_RACE_NAME_POSITION = 2;

	/**
	 * Size of the element of the filename where the race name can be found
	 */
	public static final int LC_RACE_NAME_SPLIT_LENGTH = 2;

	/**
	 * Position of the race name in the lc race after splitting the filename at
	 * LC_RACE_NAME_POSITION
	 */
	public static final int LC_RACE_NAME_SPLIT_POSITION = 1;

	/**
	 * Position of the district in the lc race identifier
	 */
	public static final int LC_DISTRICT_POSITION = 3;
}
