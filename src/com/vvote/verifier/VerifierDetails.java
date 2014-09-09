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
package com.vvote.verifier;

/**
 * Holds the details for a verifier including the actual concrete class, its
 * name, its specification file and configuration file
 * 
 * @author James Rumble
 * 
 */
public class VerifierDetails {

	/**
	 * The path and name of the verifier which should include the package name
	 * and class name itself
	 */
	private final String verifierClass;

	/**
	 * A textual name of the verifier
	 */
	private final String verifierName;

	/**
	 * The location/path specification file for the verifier
	 */
	private final String verifierSpecFile;

	/**
	 * Constructor for a VerifierDetails object
	 * 
	 * @param verifierClass
	 * @param verifierName
	 * @param verifierSpecFile
	 */
	public VerifierDetails(String verifierClass, String verifierName, String verifierSpecFile) {
		this.verifierClass = verifierClass;
		this.verifierName = verifierName;
		this.verifierSpecFile = verifierSpecFile;
	}

	/**
	 * Getter for the verifier class
	 * 
	 * @return verifierClass
	 */
	public String getVerifierClass() {
		return this.verifierClass;
	}

	/**
	 * Getter for the textual name of the verifier
	 * 
	 * @return verifierName
	 */
	public String getVerifierName() {
		return this.verifierName;
	}

	/**
	 * Getter for the location/path of the spec file
	 * 
	 * @return verifierSpecFile
	 */
	public String getVerifierSpecFile() {
		return this.verifierSpecFile;
	}
}
