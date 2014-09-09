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
package com.vvote.messages.typed.vote;

/**
 * Custom Enum to represent the type of the race
 * 
 * @author James Rumble
 * 
 */
public enum RaceType {

	/**
	 * LA Race
	 */
	LA("LA"),
	/**
	 * LC ATL Race
	 */
	LC_ATL("LC_ATL"),
	/**
	 * LC BTL Race
	 */
	LC_BTL("LC_BTL");

	/**
	 * Creates the correct type of race from a string
	 * 
	 * @param race
	 * @return the race type from a provided string
	 */
	public static RaceType fromString(String race) {
		if (race != null) {
			for (RaceType currentType : RaceType.values()) {
				if (race.equalsIgnoreCase(currentType.getType())) {
					return currentType;
				}
			}

			// corner cases
			if (race.equalsIgnoreCase("LCATL")) {
				return RaceType.LC_ATL;
			} else if (race.equalsIgnoreCase("ATL")) {
				return RaceType.LC_ATL;
			} else if (race.equalsIgnoreCase("LCBTL")) {
				return RaceType.LC_BTL;
			} else if (race.equalsIgnoreCase("BTL")) {
				return RaceType.LC_BTL;
			}
		}
		return null;
	}

	/**
	 * The type of the race
	 */
	private final String type;

	/**
	 * Private constructor for the Race type
	 * 
	 * @param type
	 */
	private RaceType(String type) {
		this.type = type;
	}

	/**
	 * Getter for the type of race
	 * 
	 * @return type
	 */
	public final String getType() {
		return this.type;
	}
}
