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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vvote.commits.exceptions.FinalCommitInitException;

/**
 * Provides the representation for a final commitment which includes a json
 * message containing the messages committed to the public wbb for a specific
 * commit, an attachments file which contains any file submissions for the
 * commit and a commitment signature which can be used to verify the submission
 * files
 * 
 * @author James Rumble
 * 
 */
public final class FinalCommitment {

	/**
	 * Provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FinalCommitment.class);

	/**
	 * The commit file message which contains JSON messages which have been
	 * submitted to the public wbb for the commitment
	 */
	private CommitFileMessage fileMessage;

	/**
	 * An attachment file which contains any file submissions for the commitment
	 */
	private CommitAttachment attachment;

	/**
	 * A signature message which can be used to verify the submission files
	 */
	private CommitSignature signature;

	/**
	 * The identifier for the commitment to the public wbb
	 */
	private final String identifier;

	/**
	 * Constructor taking a <code>CommitAttachment</code> object
	 * 
	 * @param attachment
	 * @throws FinalCommitInitException
	 */
	public FinalCommitment(CommitAttachment attachment) throws FinalCommitInitException {

		logger.debug("Creating a new FinalCommitment object");

		this.init(attachment);

		this.identifier = this.attachment.getIdentifier();

		logger.debug("Successfully created a new FinalCommitment object: {}", this.attachment.getFilePath());
	}

	/**
	 * Constructor taking a <code>CommitFileMessage</code> object
	 * 
	 * @param fileMessage
	 * @throws FinalCommitInitException
	 */
	public FinalCommitment(CommitFileMessage fileMessage) throws FinalCommitInitException {

		logger.debug("Creating a new FinalCommitment object");

		this.init(fileMessage);

		this.identifier = this.fileMessage.getIdentifier();

		logger.debug("Successfully created a new FinalCommitment object: {}", this.fileMessage.getFilePath());
	}

	/**
	 * Constructor taking a <code>CommitFileMessage</code> object, a
	 * <code>CommitAttachment</code> object and a a <code>CommitSignature</code>
	 * object
	 * 
	 * @param fileMessage
	 * @param attachment
	 * @param signature
	 * @throws FinalCommitInitException
	 */
	public FinalCommitment(CommitFileMessage fileMessage, CommitAttachment attachment, CommitSignature signature) throws FinalCommitInitException {

		logger.debug("Creating a new FinalCommitment object");

		this.init(fileMessage);

		this.init(attachment);

		this.init(signature);

		logger.debug("Creating a new FinalCommitment object: {}, {}, {}", this.fileMessage.getFilePath(), this.attachment.getFilePath(), this.signature.getFilePath());

		if (!this.verifyIdentifiers()) {
			throw new FinalCommitInitException("Identifiers are not valid: " + this.fileMessage.getIdentifier() + ", " + this.attachment.getIdentifier() + ", " + this.signature.getIdentifier());
		}

		if (!this.verifySignatureFileNames()) {
			throw new FinalCommitInitException("Filenames are not valid: " + this.fileMessage.getFilePath() + ", " + this.attachment.getFilePath() + ", " + this.signature.getFilePath());
		}

		this.identifier = this.fileMessage.getIdentifier();

		logger.debug("Successfully created a new FinalCommitment object");
	}

	/**
	 * Constructor taking a <code>CommitSignature</code> object
	 * 
	 * @param signature
	 * @throws FinalCommitInitException
	 */
	public FinalCommitment(CommitSignature signature) throws FinalCommitInitException {

		logger.debug("Creating a new FinalCommitment object");

		this.init(signature);

		this.identifier = this.signature.getIdentifier();

		logger.debug("Successfully created a new FinalCommitment object: {}", this.signature.getFilePath());
	}

	/**
	 * Getter for the <code>CommitAttachment</code> object
	 * 
	 * @return attachment
	 */
	public final CommitAttachment getAttachment() {
		return this.attachment;
	}

	/**
	 * Getter for the <code>CommitFileMessage</code> object
	 * 
	 * @return fileMessage
	 */
	public final CommitFileMessage getFileMessage() {
		return this.fileMessage;
	}

	/**
	 * Getter for the identifier for the final commitment
	 * 
	 * @return identifier
	 */
	public final String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Getter for the <code>CommitSignature</code> object
	 * 
	 * @return signature
	 */
	public final CommitSignature getSignature() {
		return this.signature;
	}

	/**
	 * Init taking in a <code>CommitAttachment</code> object
	 * 
	 * @param attachment
	 * @throws FinalCommitInitException
	 */
	private final void init(CommitAttachment attachment) throws FinalCommitInitException {
		if (attachment != null) {
			this.attachment = attachment;
		} else {
			throw new FinalCommitInitException("A Final Commitment object must be provided with a valid attachment file object");
		}
	}

	/**
	 * Init taking in a <code>CommitFileMessage</code> object
	 * 
	 * @param fileMessage
	 * @throws FinalCommitInitException
	 */
	private final void init(CommitFileMessage fileMessage) throws FinalCommitInitException {
		if (fileMessage != null) {
			this.fileMessage = fileMessage;
		} else {
			throw new FinalCommitInitException("A Final Commitment object must be provided with a valid file message object");
		}
	}

	/**
	 * Init taking in a <code>CommitSignature</code> object
	 * 
	 * @param signature
	 * @throws FinalCommitInitException
	 */
	private final void init(CommitSignature signature) throws FinalCommitInitException {
		if (signature != null) {
			this.signature = signature;
		} else {
			throw new FinalCommitInitException("A Final Commitment object must be provided with a valid signature file object");
		}
	}

	/**
	 * Setter for the <code>CommitAttachment</code> object
	 * 
	 * @param attachment
	 */
	public final void setAttachment(CommitAttachment attachment) {
		if (this.attachment == null) {
			if (attachment != null) {
				this.attachment = attachment;
				if (!this.verifyIdentifiers()) {
					this.attachment = null;
				}
				if (!this.verifySignatureFileNames()) {
					this.attachment = null;
				}
			}
		}
	}

	/**
	 * Setter for the <code>CommitFileMessage</code> object
	 * 
	 * @param fileMessage
	 */
	public final void setFileMessage(CommitFileMessage fileMessage) {
		if (this.fileMessage == null) {
			if (fileMessage != null) {
				this.fileMessage = fileMessage;
				if (!this.verifyIdentifiers()) {
					this.fileMessage = null;
				}
				if (!this.verifySignatureFileNames()) {
					this.fileMessage = null;
				}
			}
		}
	}

	/**
	 * Setter for the <code>CommitSignature</code> object
	 * 
	 * @param signature
	 */
	public final void setSignature(CommitSignature signature) {
		if (this.signature == null) {
			if (signature != null) {
				this.signature = signature;
				if (!this.verifyIdentifiers()) {
					this.signature = null;
				}
				if (!this.verifySignatureFileNames()) {
					this.signature = null;
				}
			}
		}
	}

	/**
	 * Private helper method to verify the identifiers for all currently
	 * included parts of the final commit
	 * 
	 * @return true if the identifiers match across the final commitment object
	 */
	private boolean verifyIdentifiers() {

		logger.debug("Verifying that the identifiers and file names provided match");

		if (this.fileMessage != null && this.attachment != null) {
			if (!this.fileMessage.getIdentifier().equals(this.attachment.getIdentifier())) {
				logger.error("File message identifier doesn't match the attachment identifier");
				return false;
			}
		}

		if (this.fileMessage != null && this.signature != null) {
			if (!this.fileMessage.getIdentifier().equals(this.signature.getIdentifier())) {
				logger.error("File message identifier doesn't match the signature identifier");
				return false;
			}
		}

		if (this.signature != null && this.attachment != null) {
			if (!this.signature.getIdentifier().equals(this.attachment.getIdentifier())) {
				logger.error("Signature identifier doesn't match the attachment identifier");
				return false;
			}
		}

		logger.debug("Verifyied that the identifiers and file names provided match");

		return true;
	}

	/**
	 * Verifies that the filenames provided in the signature file matches those
	 * of the the actual files provided
	 * 
	 * @return true if the filenames match
	 */
	private boolean verifySignatureFileNames() {

		logger.debug("Verifying that the file names in the signature message matches the file names provided");

		// look inside the signature file and check that the file message name
		// included matches that of the file message
		if (this.signature != null && this.fileMessage != null) {
			if (!this.signature.getSignatureMessage().getJsonFile().equals(new File(this.fileMessage.getFilePath()).getName())) {
				logger.error("Signature json file doesn't match the file message name");
				return false;
			}
		}

		// look inside the signature file and check that the attachment file
		// name included matches that of the attachment file
		if (this.signature != null && this.attachment != null) {
			if (!this.signature.getSignatureMessage().getAttachmentFile().equals(new File(this.attachment.getFilePath()).getName())) {
				logger.error("Signature attachment file doesn't match the attachment file name");
				return false;
			}
		}
		logger.debug("Verifyied that the file names in the signature message matches the file names provided");

		return true;
	}

	@Override
	public String toString() {
		String output = null;
		if (this.signature == null) {
			output = "FinalCommitment [fileMessage=" + this.fileMessage.getFilePath() + ", attachment=" + this.attachment.getFilePath() + ", identifier=" + this.identifier + "]";
		} else if (this.fileMessage == null) {
			output = "FinalCommitment [attachment=" + this.attachment.getFilePath() + ", signature=" + this.signature.getFilePath() + ", identifier=" + this.identifier + "]";
		} else if (this.attachment == null) {
			output = "FinalCommitment [fileMessage=" + this.fileMessage.getFilePath() + ", signature=" + this.signature.getFilePath() + ", identifier=" + this.identifier + "]";
		} else {
			output = "FinalCommitment [fileMessage=" + this.fileMessage.getFilePath() + ", attachment=" + this.attachment.getFilePath() + ", signature=" + this.signature.getFilePath()
					+ ", identifier=" + this.identifier + "]";
		}
		return output;
	}
}
