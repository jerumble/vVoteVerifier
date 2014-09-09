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
package com.vvote.verifierlibrary.utils.io;

/**
 * Enum for the different types of files to be worked on
 * 
 * @author James Rumble
 * 
 */
public enum FileType {

	/**
	 * ZIP File
	 */
	ZIP("zip"),
	/**
	 * JSON file
	 */
	JSON("json"),
	/**
	 * Candidate table
	 */
	CANDIDATE_TABLE("cid"),
	/**
	 * Comma separated values file
	 */
	CSV("csv"),
	/**
	 * Mix input files. These contain ciphers
	 */
	MIX_INPUT("blt"),
	/**
	 * Mix output files. These contain plaintexts
	 */
	MIX_OUTPUT("out");

	/**
	 * The extension for a file
	 */
	private final String extension;

	/**
	 * Private constructor for the File type
	 * 
	 * @param extension
	 */
	private FileType(String extension) {
		this.extension = extension;
	}

	/**
	 * Getter for the type of file
	 * 
	 * @return extension
	 */
	public String getExtension() {
		return this.extension;
	}

}
