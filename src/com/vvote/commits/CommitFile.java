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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides an abstract representation of a <code>CommitFile</code> which is
 * placed in the final_commits folder on the public wbb
 * 
 * @author James Rumble
 * 
 */
public abstract class CommitFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitFile.class);

	/**
	 * Identifier for the committed file attachment
	 */
	private final String identifier;

	/**
	 * Name of the file
	 */
	private final String filePath;

	/**
	 * Type of the file
	 */
	private final FileType fileType;

	/**
	 * Constructor for a <code>CommitFile</code>
	 * 
	 * @param filePath
	 * @throws CommitFileInitException
	 */
	public CommitFile(String filePath) throws CommitFileInitException {

		logger.debug("Creating a CommitFile object: {}", filePath);

		this.filePath = filePath;
		
		this.fileType = this.initFileType();

		if (this.filePath != null) {

			if (this.filePath.length() > 0) {

				this.verifyExtension();

				this.identifier = this.findIdentifier();

				logger.debug("Identifier: {}", this.identifier);

			} else {
				logger.error("A Commit file must have a valid filename");
				throw new CommitFileInitException("A Commit file must have a valid filename");
			}
		} else {
			logger.error("A Commit file must have a valid filename");
			throw new CommitFileInitException("A Commit file must have a valid filename");
		}

		logger.debug("Successfully created a CommitFile object: {}", filePath);

	}

	/**
	 * Gets the identifier from the filepath
	 * 
	 * @return the identifier for the file
	 */
	public abstract String findIdentifier();

	/**
	 * Getter for the filepath of the <code>CommitFile</code> object
	 * 
	 * @return filePath
	 */
	public String getFilePath() {
		return this.filePath;
	}

	/**
	 * Getter for the identifier of the <code>CommitFile</code> object
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Sets the file type for the concrete class
	 * 
	 * @return the type of the file
	 */
	public abstract FileType initFileType();

	/**
	 * Verifies the extension for the current type of <code>CommitFile</code>
	 * object
	 * 
	 * @return true if the extension is what is expected
	 */
	public boolean verifyExtension() {
		return IOUtils.checkExtension(this.fileType, this.getFilePath());
	}

	@Override
	public String toString() {
		return "CommitFile [identifier=" + this.identifier + ", filePath=" + this.filePath + ", fileType=" + this.fileType + "]";
	}
}
