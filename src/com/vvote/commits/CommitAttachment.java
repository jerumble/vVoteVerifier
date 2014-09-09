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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.exceptions.CommitAttachmentInitException;
import com.vvote.commits.exceptions.CommitFileInitException;
import com.vvote.verifierlibrary.utils.io.FileType;
import com.vvote.verifierlibrary.utils.io.IOUtils;

/**
 * Provides a representation of a commit attachment zip file object which will
 * hold all the attachment files for any messages included within a related
 * <code>CommitFileMessage</code> object
 * 
 * @author James Rumble
 * 
 */
public final class CommitAttachment extends CommitFile {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitAttachment.class);

	/**
	 * Files in the attachment
	 */
	private final Set<String> zipFiles;

	/**
	 * Constructor for a <code>CommitAttachment</code> object taking as input
	 * the filename
	 * 
	 * @param filePath
	 * @throws CommitAttachmentInitException
	 * @throws CommitFileInitException
	 */
	public CommitAttachment(String filePath) throws CommitAttachmentInitException, CommitFileInitException {
		super(filePath);

		logger.debug("Creating a CommitFileMessage object: {}", filePath);

		if (!this.verifyExtension()) {
			logger.error("A Commit attachment must be a .zip file");
			throw new CommitAttachmentInitException("A Commit attachment must be a .zip file");
		}

		try {
			this.zipFiles = new HashSet<String>(IOUtils.readZipFileNames(this.getFilePath()));
		} catch (IOException e) {
			logger.error("There was a problem reading the zip file for the CommitAttachment object: " + filePath, e);
			throw new CommitAttachmentInitException("There was a problem reading the zip file for the CommitAttachment object: " + filePath, e);
		}

		logger.debug("Successfully created a CommitAttachment object: {}", filePath);
	}

	@Override
	public String findIdentifier() {
		if (this.getFilePath().contains(CommitFileNames.ATTACHMENT_FILE.getFileName())) {
			return IOUtils.getFileNameWithoutExtension(this.getFilePath()).replace(CommitFileNames.ATTACHMENT_FILE.getFileName(), "");
		}
		return IOUtils.getFileNameWithoutExtension(this.getFilePath());
	}

	/**
	 * Getter for the files contained in the zip file
	 * 
	 * @return zipFiles
	 */
	public final Set<String> getZipFiles() {
		return Collections.unmodifiableSet(this.zipFiles);
	}

	@Override
	public FileType initFileType() {
		return FileType.ZIP;
	}

	@Override
	public String toString() {
		return "CommitAttachment [zipFiles=" + this.zipFiles + ", getFilePath()=" + getFilePath() + ", getIdentifier()=" + getIdentifier() + "]";
	}
}
