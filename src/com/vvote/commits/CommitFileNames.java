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
package com.vvote.commits;

/**
 * @author James Rumble
 * 
 */
public enum CommitFileNames {

	/**
	 * A string contained in the filename of an attachment file
	 */
	ATTACHMENT_FILE("_attachments"),
	/**
	 * A string contained in the filename of a signature file
	 */
	SIGNATURE_NAME("_signature"),
	/**
	 * A string contained in the filename of a wbb upload zip file
	 */
	WBB_UPLOAD("WBBUpload");

	/**
	 * A string contained in the filename of the commit file
	 */
	private final String filename;

	/**
	 * Private constructor for the File type
	 * 
	 * @param filename
	 *            A string contained in the filename of the commit file
	 * 
	 */
	private CommitFileNames(String filename) {
		this.filename = filename;
	}

	/**
	 * Getter for the type of file
	 * 
	 * @return filename A string contained in the filename of the commit file
	 */
	public String getFileName() {
		return this.filename;
	}
}
