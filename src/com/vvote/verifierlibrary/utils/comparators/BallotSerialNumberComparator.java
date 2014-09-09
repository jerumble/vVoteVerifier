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
package com.vvote.verifierlibrary.utils.comparators;

import java.util.Comparator;

import com.vvote.JSONConstants;

/**
 * Allows serial numbers for ballots to be easily sorted. Serial numbers are
 * made up of a string and a number
 * 
 * @author James Rumble
 * 
 */
public class BallotSerialNumberComparator implements Comparator<String> {

	@Override
	public int compare(String serial1, String serial2) {

		// compare the string part of the serial number
		int comp = serial1.substring(0, serial1.indexOf(JSONConstants.SERIAL_NO_SEPARATOR)).compareTo(serial2.substring(0, serial2.indexOf(JSONConstants.SERIAL_NO_SEPARATOR)));

		// if serial numbers are from the same device compare the number after
		// the separator
		if (comp == 0) {
			
			Integer first = Integer.parseInt(serial1.substring(serial1.indexOf(JSONConstants.SERIAL_NO_SEPARATOR) + 1));
			Integer second = Integer.parseInt(serial2.substring(serial2.indexOf(JSONConstants.SERIAL_NO_SEPARATOR) + 1));
			
			return Integer.compare(first, second);
		}
		return comp;
	}

}
