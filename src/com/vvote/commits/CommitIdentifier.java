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

import com.vvote.commits.exceptions.CommitIdentifierException;

/**
 * Provides a storage class for a printer id and a string which is included in
 * the filename of a group of commitment objects (file message (
 * <code>CommitFileMessage</code>), attachment (<code>CommitAttachment</code>)
 * and signature (<code>CommitSignature</code>))
 * 
 * @author James Rumble
 * 
 */
public class CommitIdentifier {

	/**
	 * provides logging for the class
	 */
	private static final Logger logger = LoggerFactory.getLogger(CommitIdentifier.class);

	/**
	 * the identifier for the <code>CommitIdentifier</code>
	 */
	private final String identifier;

	/**
	 * the printerId for the <code>CommitIdentifier</code>
	 */
	private final String printerId;

	/**
	 * Constructor for a Commitment identifier which includes a printer id and a
	 * string included in the filename which is used as the 'identifier'
	 * 
	 * @param identifier
	 * @param printerId
	 * @throws CommitIdentifierException
	 */
	public CommitIdentifier(String identifier, String printerId) throws CommitIdentifierException {

		if (identifier == null || printerId == null) {
			logger.error("Unable to create CommitIdentifier object: {}, {}", identifier, printerId);
			throw new CommitIdentifierException("Unable to create CommitIdentifier object: (" + identifier + ", " + printerId + ")");
		}

		this.identifier = identifier;
		this.printerId = printerId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		CommitIdentifier other = (CommitIdentifier) obj;

		if (this.identifier == null) {
			if (other.identifier != null) {
				return false;
			}
		} else if (!this.identifier.equals(other.identifier)) {
			return false;
		}

		if (this.printerId == null) {
			if (other.printerId != null) {
				return false;
			}
		} else if (!this.printerId.equals(other.printerId)) {
			return false;
		}

		return true;
	}

	/**
	 * Getter for the identifier of a <code>CommitIdentifier</code>
	 * 
	 * @return identifier
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * Getter for the printer id of a <code>CommitIdentifier</code>
	 * 
	 * @return printerId
	 */
	public String getPrinterId() {
		return this.printerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.identifier == null) ? 0 : this.identifier.hashCode());
		result = prime * result + ((this.printerId == null) ? 0 : this.printerId.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "CommitIdentifier [identifier=" + this.identifier + ", printerId=" + this.printerId + "]";
	}
}
